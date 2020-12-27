package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.mariaparadodebellido.adapter.JustificacionAdapter;
import pe.mariaparadodebellido.model.Justificacion;
import pe.mariaparadodebellido.util.Url;

public class ListarJustificacion extends AppCompatActivity {

    private ArrayList<Justificacion> justificaciones = new ArrayList<Justificacion>();
    private String dniEstudiante="61933011";
    private RecyclerView rvJustificaciones;
    private JustificacionAdapter justificacionAdapter;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_justificacion);

        rvJustificaciones = findViewById(R.id.recyclerView);
        rvJustificaciones.setLayoutManager(new LinearLayoutManager(ListarJustificacion.this));
        justificacionAdapter = new JustificacionAdapter(ListarJustificacion.this);
        rvJustificaciones.setAdapter(justificacionAdapter);
        justificaciones = new ArrayList<Justificacion>();

        queue = Volley.newRequestQueue(this);
        getListarJustificaciones();
    }

    private void getListarJustificaciones() {
        String url = "http://"+ Url.IP+":"+Url.PUERTO+"/idat/rest/justificacion/listar_justificacion?dniEstudiante="+dniEstudiante+"&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try{
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject objjson = jsonArray.getJSONObject(i);

                                justificaciones.add(new Justificacion(
                                        objjson.getString("descripcion"),
                                        objjson.getString("fecha"),
                                        objjson.getString("titulo")
                                ));
                            }
                            justificacionAdapter.agregarJustificacion(justificaciones);
                        }catch(JSONException ex){
                            Log.e("ErrorVolley", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorRequest", volleyError.getMessage());
            }
        });
        queue.add(peticion);
    }
    }
