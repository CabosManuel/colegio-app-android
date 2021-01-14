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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.HorarioAdapter;
import pe.mariaparadodebellido.model.HorarioDetalle;
import pe.mariaparadodebellido.util.Url;

import static android.content.Context.MODE_PRIVATE;

public class ConsultarHorarioFragment extends Fragment {

    private String dniEstudiante;

    private RecyclerView rvHorario;
    private HorarioAdapter horarioAdapter;
    private RequestQueue queue;
    private ArrayList<HorarioDetalle> horario = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_consultar_horario, container, false);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "cliente no existe"));
            try {
                dniEstudiante = eJson.getString("dniEstudiante");
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(getContext(), "Error al cargar datos del usuario.", Toast.LENGTH_SHORT).show();
                try {
                    dniEstudiante = preferences.getString("dniEstudiante", "");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        rvHorario = viewFragment.findViewById(R.id.rv_horario);
        rvHorario.setLayoutManager(new LinearLayoutManager(getContext()));
        horarioAdapter = new HorarioAdapter(getContext());
        rvHorario.setAdapter(horarioAdapter);
        horario = new ArrayList<>();

        queue = Volley.newRequestQueue(getContext());
        getHorario();

        return viewFragment;
    }

    private void getHorario() {
        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/horariodetalle/consultar_horario?dniEstudiante=" + dniEstudiante + "&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);

                                horario.add(new HorarioDetalle(
                                        objjson.getString("dia"),
                                        objjson.getString("hora_inicio"),
                                        objjson.getString("hora_fin"),
                                        objjson.getString("nombre")
                                ));
                            }
                            horarioAdapter.agregarHorario(horario);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar horario.", Toast.LENGTH_SHORT).show();

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