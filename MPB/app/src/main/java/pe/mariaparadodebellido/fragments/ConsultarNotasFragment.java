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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.NotaAdapter;
import pe.mariaparadodebellido.model.Nota;
import pe.mariaparadodebellido.util.Url;

import static android.content.Context.MODE_PRIVATE;

public class ConsultarNotasFragment extends Fragment {

    private Spinner spAnio;

    // Año actual por defecto
    private String anio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1); // 2020

    private String dniEstudiante = "";

    private RecyclerView rvNotas;
    private NotaAdapter notaAdapter;
    private RequestQueue colaPeticiones;
    private ArrayList<Nota> listaNotas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_consultar_notas, container, false);

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

        spAnio = viewFragment.findViewById(R.id.sp_anio);
        getAnios();

        rvNotas = viewFragment.findViewById(R.id.rv_f_notas);
        rvNotas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        notaAdapter = new NotaAdapter(this.getContext());
        rvNotas.setAdapter(notaAdapter);
        listaNotas = new ArrayList<>();

        colaPeticiones = Volley.newRequestQueue(getContext());
        getConsultarNota();

        return viewFragment;
    }

    // Método para listar notas en las tarjetas
    private void getConsultarNota() {
        String url = Url.URL_BASE + "/idat/rest/nota/consultar_notas?dniEstudiante=" + dniEstudiante + "&anio=" + anio;
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                listaNotas.add(new Nota(
                                        objjson.getString("curso"),
                                        objjson.getDouble("nota1"),
                                        objjson.getDouble("nota2"),
                                        objjson.getDouble("nota3")
                                ));
                            }
                            notaAdapter.agregarNota(listaNotas);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar notas.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
        colaPeticiones.add(peticion);
    }

    // Método para OPTENER todos lo años del estudiante
    private void getAnios() {
        String url = Url.URL_BASE + "/idat/rest/nota/anios?dniEstudiante=" + dniEstudiante;
        colaPeticiones = Volley.newRequestQueue(getContext());
        StringRequest peticion = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray aniosArray = new JSONArray(response);
                            llenarSpinner(aniosArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar años.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
        colaPeticiones.add(peticion);
    }

    // Método para cargar años en spinner + onItemSelectedListener
    private void llenarSpinner(JSONArray jsonArray) {
        ArrayList<String> anios = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                anios.add(jsonArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Cargar datos en el spinner
        spAnio.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, anios));
        // Seleccionar el ultimo item del spinner (2020)
        spAnio.setSelection(anios.size() - 1);

        // Agregar Listener
        spAnio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // Lo que pasa cuando selecciona un item del spinner
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                anio = spAnio.getSelectedItem().toString();
                listaNotas = new ArrayList<>();
                getConsultarNota();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}