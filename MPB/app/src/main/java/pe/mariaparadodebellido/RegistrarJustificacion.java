package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import pe.mariaparadodebellido.fragments.ListarJustificacionesFragment;


public class RegistrarJustificacion extends AppCompatActivity {

        private EditText txtFechaInasistencia, txtJustificacion, txttitulo;
        private TextView txt_adjuntar;
        //private TextView titulo,fechaEnvio,fechaJustificacion,dniEstudiante,descripcion;
        private Button Enviar, Cancelar;
        RequestQueue request;
        RequestQueue Justi_queue;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registrar_justificacion);

            txtFechaInasistencia = findViewById(R.id.txtFechaInasistencia);
            txtJustificacion = findViewById(R.id.txtJustificacion);
            txttitulo = findViewById(R.id.txttitulo);
            txt_adjuntar = findViewById(R.id.txt_adjuntar);

            Enviar = findViewById(R.id.btnEnviar);
            Cancelar = findViewById(R.id.btnCancelar);

            Enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validar()) {
                        cargarWebServices();
                    }
                }
            });
        }


        public boolean validar(){

            boolean retorno=true;

            //int jjustificacionId;
            String jfechaJustificacion,jjustificacion,jadjuntar, jtitulo;


            jfechaJustificacion=txtFechaInasistencia.getText().toString();
            jjustificacion=txtJustificacion.getText().toString();
            jadjuntar=txt_adjuntar.getText().toString();
            jtitulo = txttitulo.getText().toString();


        /*
            if(jfechaJustificacion.isEmpty()){

                jfechaJustificacion.setError("Este campo no puede quedar vacio");
                retorno = false;

            }

            if(fechaEnvio.isEmpty()){

                apellido.setError("Este campo no puede quedar vacio");
                retorno = false;

            }

            if(fechaJustificacion.isEmpty()){

                edad.setError("Este campo no puede quedar vacio");
                retorno = false;

            }

            */

         return  retorno;
        }


    private void cargarWebServices() {
        String urlJusti ="http:/localhost:6060/justificacion/registrar";


        final JSONObject jsonObject_principal = new JSONObject();

        final JSONObject jsonObject_justiID = new JSONObject();



        try {

            jsonObject_principal.put("justificacionid", jsonObject_justiID);
//------------------------------------


            //----------------------------------------------

            jsonObject_principal.put("descripcion", txtJustificacion);


            jsonObject_principal.put("fecha", txtFechaInasistencia.getText().toString());

            jsonObject_principal.put("titulo",txttitulo.getText().toString());


        }catch (JSONException e){

        }


    //------------------------------------------
    JsonObjectRequest  postRequest = new JsonObjectRequest(Request.Method.POST, urlJusti,jsonObject_principal,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject  response) {

                    Toast.makeText(getApplicationContext(),"Registro Exitoso",Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegistrarJustificacion.this, ListarJustificacionesFragment.class));

                }
            },

            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }

            }

    )

    {

    };

        request.add(postRequest);

}

}








