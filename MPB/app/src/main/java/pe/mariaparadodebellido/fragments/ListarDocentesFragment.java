package pe.mariaparadodebellido.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.DocenteAdapter;
import pe.mariaparadodebellido.adapter.JustificacionAdapter;
import pe.mariaparadodebellido.model.Docente;
import pe.mariaparadodebellido.model.Justificacion;
import pe.mariaparadodebellido.util.Url;

import static android.content.Context.MODE_PRIVATE;

public class ListarDocentesFragment extends Fragment {

    private ArrayList<Docente> Docente = new ArrayList<>();
    private String dniEstudiante = "";

    private RecyclerView rv_Docentes;
    private DocenteAdapter DocenteAdapter;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_listar_docentes, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", MODE_PRIVATE);
        try { // 1 Tratar de obtener un usuario
            JSONObject eJson = new JSONObject(preferences.getString("usuario", ""));

            try { // 2 Cuando sea una ESTUDIANTE, capturar su DNI
                dniEstudiante = eJson.getString("dniEstudiante");
            } catch (JSONException e) {
                System.err.println("ERROR try 2");
                e.printStackTrace();

                try { // 3 Cuando sea un APODERADO, capturar el dni seleccionado desde "Acceder a info. estudiantes"
                    dniEstudiante = preferences.getString("dniEstudiante", "Error al seleccionar estudiante.");
                } catch (Exception exception) {
                    System.err.println("ERROR try 3: " + dniEstudiante);
                    exception.printStackTrace();
                }
            }
        } catch (JSONException e) {
            System.err.println("ERROR try 1");
            e.printStackTrace();
        }

        rv_Docentes = viewFragment.findViewById(R.id.rv_Docentes);
        rv_Docentes.setLayoutManager(new LinearLayoutManager(getContext()));
        DocenteAdapter = new DocenteAdapter(getContext());
        rv_Docentes.setAdapter(DocenteAdapter);
        Docente = new ArrayList<>();

        queue = Volley.newRequestQueue(getContext());
        getListarDocentes();

        return viewFragment;
    }
    private void getListarDocentes() {
        String url = Url.URL_BASE + "/idat/rest/trabajador/listar_docentes?dniEstudiante=" + dniEstudiante;
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                Docente docente = new Docente();
                                docente.setNombres(objjson.getString("nombres"));
                                docente.setApellido(objjson.getString("apellidos"));
                                docente.setCelular(objjson.getString("celular"));
                                docente.setCorreo(objjson.getString("correo"));
                                docente.setSexo(objjson.getString("sexo"));
                                docente.setCurso(objjson.getString("curso"));

                                Docente.add(docente);
                            }
                            DocenteAdapter.agregarDocente(Docente);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar docente", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }
}



