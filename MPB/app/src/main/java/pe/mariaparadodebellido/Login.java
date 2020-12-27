package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText dnilogin, clavelogin;
    private TextView mensajeLogin;
    private Button buttonLogin;
    private RequestQueue colaPeticiones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dnilogin = findViewById(R.id.txt_dni);
        clavelogin = findViewById(R.id.txt_contraseña);
        mensajeLogin = findViewById(R.id.mensajeLogin);
        mensajeLogin.setText("");
        buttonLogin= findViewById(R.id.btn_iniciar);
        colaPeticiones = Volley.newRequestQueue(this);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url = "";

                Map<String, String> objjson = new HashMap<>();
                objjson.put("dni", dnilogin.getText().toString());
                objjson.put("clave", clavelogin.getText().toString());

                JSONObject parametroJson = new JSONObject(objjson);

                JsonObjectRequest requerimiento = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        parametroJson,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {


                                try {

                                    if (response.getBoolean("rpta"))
                                    {

                                     mensajeLogin.setText(response.getString("mensaje"));
                                    } else
                                    {

                                        mensajeLogin.setText(response.getString("mensaje"));
                                    }

                                } catch (Exception ex)
                                {
                                    Log.e("Error Conexion Volet",ex.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("Error Conexion Volet",error.getMessage());
                            }
                        }

                );
                colaPeticiones.add(requerimiento);
            }
        });


    }
    }