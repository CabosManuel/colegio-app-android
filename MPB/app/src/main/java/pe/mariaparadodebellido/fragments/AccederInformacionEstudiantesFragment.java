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
import pe.mariaparadodebellido.adapter.InfoEstudianteAdapter;
import pe.mariaparadodebellido.adapter.NotificacionAdapter;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class AccederInformacionEstudiantesFragment extends Fragment {

    private String dniApoderado = /*"06662516"*/"";

    private RecyclerView rvinfoEstudiante;
    private InfoEstudianteAdapter infoEstudianteAdapter;
    private RequestQueue queue;
    private ArrayList<Estudiante> estudiantes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_acceder_informacion_estudiantes, container, false);

        rvinfoEstudiante = viewFragment.findViewById(R.id.rv_info_estudiantes);
        rvinfoEstudiante.setLayoutManager(new LinearLayoutManager(getContext()));
        infoEstudianteAdapter = new InfoEstudianteAdapter(getContext());
        rvinfoEstudiante.setAdapter(infoEstudianteAdapter);

        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "usuario no existe"));
            dniApoderado = eJson.getString("dniApoderado");

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        getEstudiantes();
        return viewFragment;
    }

    private void getEstudiantes() {
        estudiantes = new ArrayList<>(); // Reiniciar lista
        queue = Volley.newRequestQueue(getContext());

        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/apoderados/estudiantes?dniApoderado=" + dniApoderado + "&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject estudianteJSON = jsonArray.getJSONObject(i);

                                Estudiante e = new Estudiante();
                                e.setDniEstudiante(estudianteJSON.getString("dni_estudiante"));
                                e.setNombre(estudianteJSON.getString("nombre"));
                                e.setApellido(estudianteJSON.getString("apellido"));

                                estudiantes.add(e);

                                infoEstudianteAdapter.agregarInfoEstudiantes(estudiantes);
                            }
                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                System.err.println("getCause: "+volleyError.getCause());
                Toast.makeText(getContext(), "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }
}