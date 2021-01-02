package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pe.mariaparadodebellido.util.Url;

public class Login extends AppCompatActivity {

    private TextInputEditText etDni, etClave;
    // private TextView mensajeLogin;
    private Button bntLogin;
    private RequestQueue colaPeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etDni = findViewById(R.id.txt_dni);
        etClave = findViewById(R.id.txt_clave);
        //mensajeLogin = findViewById(R.id.mensajeLogin);
        //mensajeLogin.setText("");

        bntLogin = findViewById(R.id.btn_iniciar);
        colaPeticiones = Volley.newRequestQueue(this);

        bntLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/login";

                Map<String, String> usuarioJson = new HashMap<>();
                usuarioJson.put("dni", etDni.getText().toString());
                usuarioJson.put("pass", etClave.getText().toString());

                JSONObject parametroJson = new JSONObject(usuarioJson);

                JsonObjectRequest requerimiento = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        parametroJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject datosUsuario) {
                                try {
                                    if(datosUsuario.getBoolean("rpta")) {

                                        JSONObject usuarioJson = new JSONObject();
                                        switch (datosUsuario.getString("tipo")){
                                            case "apoderado":
                                                usuarioJson = datosUsuario.getJSONObject("apoderado");

                                                break;
                                            case "estudiante":
                                                Toast.makeText(Login.this, "3: estudiante", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                        //nuevaSesion(usuarioJson);

                                        startActivity(new Intent(Login.this, ListarJustificacion.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Login.this, datosUsuario.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                        //mensajeLogin.setText(response.getString("mensaje"));
                                    }
                                } catch (Exception ex) {
                                    Log.e("Error Conexion Voley", ex.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error Conexion Voley", error.getMessage());
                            }
                        }
                );
                colaPeticiones.add(requerimiento);
            }
        });
    }
}