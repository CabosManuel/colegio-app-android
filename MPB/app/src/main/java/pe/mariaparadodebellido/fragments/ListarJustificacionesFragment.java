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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.JustificacionAdapter;
import pe.mariaparadodebellido.model.Justificacion;
import pe.mariaparadodebellido.util.Url;

public class ListarJustificacionesFragment extends Fragment {

    private ArrayList<Justificacion> justificaciones = new ArrayList<>();
    private String dniEstudiante = "61933011";

    private FloatingActionButton fabAgregar;
    private RecyclerView rvJustificaciones;
    private JustificacionAdapter justificacionAdapter;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_listar_justificaciones, container, false);

        rvJustificaciones = viewFragment.findViewById(R.id.rv_justificaciones);
        rvJustificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        justificacionAdapter = new JustificacionAdapter(getContext());
        rvJustificaciones.setAdapter(justificacionAdapter);
        justificaciones = new ArrayList<>();

        queue = Volley.newRequestQueue(getContext());
        getListarJustificaciones();

        fabAgregar = viewFragment.findViewById(R.id.fab_agregar);
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Registrar justificación (Sprint 4)", Toast.LENGTH_SHORT).show();
            }
        });

        return viewFragment;
    }

    private void getListarJustificaciones() {
        String url = Url.URL_BASE + "/idat/rest/justificaciones/listar_justificaciones?dniEstudiante=" + dniEstudiante;
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
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar justificaciones.", Toast.LENGTH_SHORT).show();
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