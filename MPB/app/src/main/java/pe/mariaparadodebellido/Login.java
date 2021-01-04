package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pe.mariaparadodebellido.model.Apoderado;
import pe.mariaparadodebellido.model.Estudiante;
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

        //Validar sesión iniciada y redirigir
        SharedPreferences preferences = getSharedPreferences("info_usuario",MODE_PRIVATE);
        if(preferences.contains("usuario")){
            startActivity(new Intent(Login.this,MenuEstudiante.class));
            finish();
        }

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
                                        String tipo = datosUsuario.getString("tipo");
                                        Toast.makeText(Login.this, tipo, Toast.LENGTH_SHORT).show();
                                        switch (tipo){
                                            case "apoderado":
                                                usuarioJson = datosUsuario.getJSONObject("apoderado");
                                                break;
                                            case "estudiante":
                                                usuarioJson = datosUsuario.getJSONObject("estudiante");
                                                nuevaSesion(usuarioJson.toString());
                                                finish();
                                                startActivity(new Intent(Login.this, MenuEstudiante.class));
                                                break;
                                            default:
                                                Toast.makeText(Login.this, "No esta funcionando...", Toast.LENGTH_SHORT).show();
                                                break;
                                        }

                                        //finish();
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

    private void nuevaSesion(String usuario) {
        SharedPreferences.Editor editor = getSharedPreferences("info_usuario", MODE_PRIVATE).edit();
        editor.putString("usuario", usuario);
        editor.apply();
    }
}