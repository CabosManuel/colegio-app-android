package pe.mariaparadodebellido.fragments;

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

public class ListarDocentesFragment extends Fragment {

    private ArrayList<Docente> Docente = new ArrayList<>();
    private String DocenteId = "";

    private FloatingActionButton fabAgregar;
    private RecyclerView rv_Docentes;
    private DocenteAdapter DocenteAdapter;
    private RequestQueue queue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_listar_docentes, container, false);

        rv_Docentes = viewFragment.findViewById(R.id.rv_Docentes);
        rv_Docentes.setLayoutManager(new LinearLayoutManager(getContext()));
        DocenteAdapter = new DocenteAdapter(getContext());
        rv_Docentes.setAdapter(DocenteAdapter);
        Docente = new ArrayList<>();

        queue = Volley.newRequestQueue(getContext());
        getListarDocentes();

        fabAgregar = viewFragment.findViewById(R.id.fab_agregar);
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Registrar Docente (Sprint 4)", Toast.LENGTH_SHORT).show();
            }
        });

        return viewFragment;
    }
    private void getListarDocentes() {
        String url = Url.URL_BASE + "/idat/rest/Docente/listar_docente?DocenteId=" + DocenteId;
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                Docente.add(new Docente (
                                        objjson.getInt("DocenteId"),
                                        objjson.getString("Nombres"),
                                        objjson.getString("Apellido"),
                                        objjson.getString("Celular"),
                                        objjson.getString("Correo"),
                                        objjson.getString("Sexo"),
                                        objjson.getString("Numero")
                                ));
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



