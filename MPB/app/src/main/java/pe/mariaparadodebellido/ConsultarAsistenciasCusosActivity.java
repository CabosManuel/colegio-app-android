package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

import pe.mariaparadodebellido.adapter.CursoAdapter;
import pe.mariaparadodebellido.model.Curso;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.util.Url;

public class ConsultarAsistenciasCusosActivity extends AppCompatActivity {

    private String dniEstudiante = /*"61933011"*/"";

    private RecyclerView rvCursos;
    private CursoAdapter cursoAdapter;
    private RequestQueue queue;
    private ArrayList<Curso> listaCursos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_asistencias_cursos);

        SharedPreferences preferences = getSharedPreferences("info_usuario",MODE_PRIVATE);
        try {
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "cliente no existe"));
            dniEstudiante = eJson.getString("dniEstudiante");

        } catch (JSONException e) {
            Toast.makeText(this, "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        rvCursos = findViewById(R.id.rv_cursos);
        rvCursos.setLayoutManager(new LinearLayoutManager(ConsultarAsistenciasCusosActivity.this));
        cursoAdapter = new CursoAdapter(ConsultarAsistenciasCusosActivity.this);
        rvCursos.setAdapter(cursoAdapter);
        listaCursos = new ArrayList<>();
        
        queue = Volley.newRequestQueue(this);
        getConsultarCursos();
    }

    private void getConsultarCursos() {
        String url = "http://"+ Url.IP+":"+Url.PUERTO+"/idat/rest/curso/listar?dniEstudiante="+dniEstudiante+"&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try{
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject CursoJson = jsonArray.getJSONObject(i);

                                listaCursos.add(new Curso(
                                        CursoJson.getInt("curso_id"),
                                        CursoJson.getString("nombre")
                                ));
                            }
                            cursoAdapter.agregarCurso(listaCursos);
                        }catch(JSONException ex){
                            Log.e("ErrorVolley", ex.getMessage());
                            Toast.makeText(ConsultarAsistenciasCusosActivity.this, "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorRequest", volleyError.getMessage());
                Toast.makeText(ConsultarAsistenciasCusosActivity.this, "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }
}