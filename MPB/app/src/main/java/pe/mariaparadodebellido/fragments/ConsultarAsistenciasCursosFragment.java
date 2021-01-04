package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.CursoAdapter;
import pe.mariaparadodebellido.model.Curso;
import pe.mariaparadodebellido.util.Url;

public class ConsultarAsistenciasCursosFragment extends Fragment {

    private String dniEstudiante = /*"61933011"*/"";

    private RecyclerView rvCursos;
    private CursoAdapter cursoAdapter;
    private RequestQueue queue;
    private ArrayList<Curso> listaCursos;

    /*
    public ConsultarAsistenciasCursosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewFragment = inflater.inflate(R.layout.fragment_consultar_asistencias_cursos,
                container, false);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "cliente no existe"));
            dniEstudiante = eJson.getString("dniEstudiante");

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        rvCursos = viewFragment.findViewById(R.id.rv_cursos);
        rvCursos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        cursoAdapter = new CursoAdapter(this.getContext());
        rvCursos.setAdapter(cursoAdapter);
        listaCursos = new ArrayList<>();

        queue = Volley.newRequestQueue(getContext());
        getConsultarCursos();

        return viewFragment;
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
                            Toast.makeText(getContext(), "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorRequest", volleyError.getMessage());
                Toast.makeText(getContext(), "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }
}