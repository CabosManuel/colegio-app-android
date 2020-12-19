package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import pe.mariaparadodebellido.adapter.NotaAdapter;
import pe.mariaparadodebellido.model.Nota;

public class ConsultarNotasActivity extends AppCompatActivity implements View.OnClickListener{

    private final String IP="192.168.0.27"; // CABOS = 192.168.0.27
    private final String PUERTO="8085"; // CABOS = 8085

    private Spinner spAnio;
    private ArrayList<String> anios;
    private String anio;

    private RecyclerView rvNotas;
    private NotaAdapter notaAdapter;
    private RequestQueue queue;
    private ArrayList<Nota> listaNotas;

    private String dniEstudiante="61933011";
    final private Calendar hoy = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_notas);

        spAnio = findViewById(R.id.sp_anio);
        getAnios();

        rvNotas = findViewById(R.id.rv_notas);
        rvNotas.setLayoutManager(
                new LinearLayoutManager(ConsultarNotasActivity.this));
        notaAdapter = new NotaAdapter(ConsultarNotasActivity.this);
        rvNotas.setAdapter(notaAdapter);
        listaNotas = new ArrayList<>();

        queue = Volley.newRequestQueue(this);
        getConsultarNota();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.sp_anio:
                //mostrarClendario();
                //getConsultarNota();
                break;*/
            default:
                Toast.makeText(this, "No mapeado.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getConsultarNota() {
        String url = "http://"+IP+":"+PUERTO+"/idat/rest/nota/consultar_notas?dniEstudiante="+dniEstudiante+"&anio="+anio+"&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try{
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                listaNotas.add(new Nota(
                                        objjson.getString("curso"),
                                        objjson.getInt("nota1"),
                                        objjson.getInt("nota2"),
                                        objjson.getInt("nota3")
                                ));
                            }
                            notaAdapter.agregarNota(listaNotas);
                        }catch(JSONException ex){
                            Log.e("ErrorVolley", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorRequest", volleyError.getMessage());
            }
        }
        );

        queue.add(peticion);
    }

    private void getAnios() {
        String url = "http://"+IP+":"+PUERTO+"/idat/rest/nota/anios?dniEstudiante="+dniEstudiante+"&?wsdl";
        queue = Volley.newRequestQueue(ConsultarNotasActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Dentro de onResponse...");
                        System.out.println(response);
                        JSONArray j = null;
                        JSONArray resultados;
                        try{
                            j = new JSONArray(response);
                            resultados = j;
                            llenarSpinner(resultados);
                        }catch (JSONException e){
                            System.out.println("Fallo en getAnios()");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                System.out.println("ERROR: " + volleyError.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void llenarSpinner(JSONArray jsonArray){
        try {
            System.out.println("Dentro de llenarSpinner.. ("+jsonArray.length()+")");
            for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println(jsonArray.get(i).toString());
                String a = jsonArray.get(i).toString();
                System.out.println("a = "+a);

                anios.add("nofunciona");
                anios.add(a);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        spAnio.setAdapter(new ArrayAdapter<String>(
                ConsultarNotasActivity.this, android.R.layout.simple_spinner_dropdown_item, anios));

        spAnio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                anio = spAnio.getSelectedItem().toString();
                getConsultarNota();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}