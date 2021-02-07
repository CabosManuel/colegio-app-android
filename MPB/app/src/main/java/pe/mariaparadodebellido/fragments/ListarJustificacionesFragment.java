package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Justificacion;
import pe.mariaparadodebellido.util.Url;

import static android.content.Context.MODE_PRIVATE;

public class ListarJustificacionesFragment extends Fragment {

    private ArrayList<Justificacion> justificaciones = new ArrayList<>();

    private String dniApoderado = "";
    private String dniEstudiante = "";
    private Spinner spEstudiantes;
    private ArrayList<Estudiante> estudiantes = new ArrayList<>();

    private FloatingActionButton fabAgregar;
    private RecyclerView rvJustificaciones;
    private JustificacionAdapter justificacionAdapter;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_listar_justificaciones, container, false);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "usuario no existe"));
            dniApoderado = eJson.getString("dniApoderado");
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        rvJustificaciones = viewFragment.findViewById(R.id.rv_justificaciones);
        rvJustificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        justificacionAdapter = new JustificacionAdapter(getContext());
        rvJustificaciones.setAdapter(justificacionAdapter);
        justificaciones = new ArrayList<>();

        spEstudiantes = viewFragment.findViewById(R.id.sp_be_estudiantes);
        getEstudiantes();

        queue = Volley.newRequestQueue(getContext());
        getListarJustificaciones();

        fabAgregar = viewFragment.findViewById(R.id.fab_agregar);
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_listar_justificaciones_to_nav_registrar_justificacion);
            }
        });

        return viewFragment;
    }

    private void getListarJustificaciones() {
        justificaciones = new ArrayList<>();
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
            }
        });
        queue.add(peticion);
    }

    private void getEstudiantes() {
        String url = Url.URL_BASE + "/idat/rest/apoderados/nombre_estudiantes/" + dniApoderado;
        queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                estudiantes.add(new Estudiante(
                                        objjson.getString("dniEstudiante"),
                                        objjson.getString("nombre")
                                ));
                            }
                            llenarSpinner();
                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar estudiantes.", Toast.LENGTH_SHORT).show();
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

    private void llenarSpinner() {
        /*
         *  Elspinner se llena solo con el "nombre" porque al model Estudiante se le está sobreescribiendo
         *  el método "toString()" que indica que solo se llene con ese, sucede algo como esto:
         *
         *                 ArrayAdapter<Estudiante> = ArrayAdapter<Estudiante.toString()>
         */
        spEstudiantes.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, estudiantes));

        // Listener
        spEstudiantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // Lo que pasa cuando selecciona un item del spinner
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Estudiante e = (Estudiante) adapterView.getSelectedItem();

                // Obtener el DNI del estudiante seleccionado y cambiar el dni que se envía a la consulta al WebService
                dniEstudiante = e.getDniEstudiante();
                getListarJustificaciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}