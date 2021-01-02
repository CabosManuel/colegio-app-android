package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.mariaparadodebellido.adapter.JustificacionAdapter;
import pe.mariaparadodebellido.model.Justificacion;
import pe.mariaparadodebellido.util.Url;

public class ListarJustificacion extends AppCompatActivity {

    private ArrayList<Justificacion> justificaciones = new ArrayList<>();
    private String dniEstudiante = "61933011";

    private FloatingActionButton fabAgregar;
    private RecyclerView rvJustificaciones;
    private JustificacionAdapter justificacionAdapter;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_justificacion);

        fabAgregar = findViewById(R.id.fbt_agregar);
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ListarJustificacion.this, "Nueva justifiación.", Toast.LENGTH_SHORT).show();
            }
        });

        rvJustificaciones = findViewById(R.id.rv_justificaciones);
        rvJustificaciones.setLayoutManager(new LinearLayoutManager(ListarJustificacion.this));
        justificacionAdapter = new JustificacionAdapter(ListarJustificacion.this);
        rvJustificaciones.setAdapter(justificacionAdapter);
        justificaciones = new ArrayList<>();

        queue = Volley.newRequestQueue(this);
        getListarJustificaciones();
    }

    private void getListarJustificaciones() {
        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/justificaciones/listar_justificaciones?dniEstudiante=" + dniEstudiante + "&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);

                                justificaciones.add(new Justificacion(
                                        objjson.getInt("justificacionId"),
                                        objjson.getString("titulo"),
                                        objjson.getString("fechaEnvio"),
                                        objjson.getString("fechaJustificacion"),
                                        objjson.getString("dniEstudiante"),
                                        objjson.getString("descripcion")
                                ));
                            }

                            justificacionAdapter.agregarJustificacion(justificaciones);
                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorVolley", volleyError.getMessage());
            }
        });
        queue.add(peticion);
    }
}
