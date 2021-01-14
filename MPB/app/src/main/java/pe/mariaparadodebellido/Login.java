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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pe.mariaparadodebellido.util.Url;

public class Login extends AppCompatActivity {

    private TextInputEditText etDni, etClave;
    private Button bntLogin;
    private RequestQueue colaPeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Validar sesión iniciada y redirigir
        SharedPreferences preferences = getSharedPreferences("info_usuario", MODE_PRIVATE);
        if (preferences.contains("usuario")) {
            String tipo = preferences.getString("tipo", "Error al obteber sesión.");
            switch (tipo) {
                case "apoderado":
                    startActivity(new Intent(Login.this, MenuApoderado.class));
                    break;
                case "estudiante":
                    startActivity(new Intent(Login.this, MenuEstudiante.class));
                    break;
                default:

                    Toast.makeText(this, tipo, Toast.LENGTH_SHORT).show();
                    break;
            }

            finish();
        }

        etDni = findViewById(R.id.txt_dni);
        etClave = findViewById(R.id.txt_clave);

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
                                    if (datosUsuario.getBoolean("rpta")) {

                                        JSONObject usuarioJson = new JSONObject();
                                        String tipo = datosUsuario.getString("tipo");
                                        Class tipoMenu = null;

                                        switch (tipo) {
                                            case "apoderado":
                                                usuarioJson = datosUsuario.getJSONObject("apoderado");
                                                tipoMenu = MenuApoderado.class;
                                                break;
                                            case "estudiante":
                                                usuarioJson = datosUsuario.getJSONObject("estudiante");
                                                tipoMenu = MenuEstudiante.class;
                                                break;
                                        }

                                        nuevaSesion(usuarioJson.toString(), tipo);
                                        Toast.makeText(Login.this, datosUsuario.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, tipoMenu));
                                        finish();

                                    } else {
                                        Toast.makeText(Login.this, datosUsuario.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Login.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }
                );
                colaPeticiones.add(requerimiento);
            }
        });
    }

    private void nuevaSesion(String usuario, String tipo) {
        SharedPreferences.Editor editor = getSharedPreferences("info_usuario", MODE_PRIVATE).edit();
        editor.putString("usuario", usuario);
        editor.putString("tipo", tipo);
        editor.apply();
    }
}