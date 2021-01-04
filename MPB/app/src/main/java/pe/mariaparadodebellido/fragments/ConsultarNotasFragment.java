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

public class ConsultarNotasFragment extends Fragment {

    private Spinner spAnio;
    private ArrayList<String> anios = new ArrayList<>();
    // Año actual por defecto
    private String anio =  String.valueOf(Calendar.getInstance().get(Calendar.YEAR)-1); //2020?

    private String dniEstudiante=/*"61933011"*/""; // DNI, debería venir por un Intent o Session?

    private RecyclerView rvNotas;
    private NotaAdapter notaAdapter;
    private RequestQueue queue;
    private ArrayList<Nota> listaNotas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_consultar_notas,
                container, false);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "cliente no existe"));
            dniEstudiante = eJson.getString("dniEstudiante");

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        spAnio = viewFragment.findViewById(R.id.sp_anio);
        getAnios();

        rvNotas = viewFragment.findViewById(R.id.rv_f_notas);
        rvNotas.setLayoutManager(new LinearLayoutManager(this.getContext()));
        notaAdapter = new NotaAdapter(this.getContext());
        rvNotas.setAdapter(notaAdapter);
        listaNotas = new ArrayList<>();

        queue = Volley.newRequestQueue(getContext());
        getConsultarNota();

        return viewFragment;
    }

    // Método para listar notas en las tarjetas
    private void getConsultarNota() {
        String url = "http://"+ Url.IP+":"+Url.PUERTO+"/idat/rest/nota/consultar_notas?dniEstudiante="+dniEstudiante+"&anio="+anio+"&?wsdl";
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try{
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject objjson = jsonArray.getJSONObject(i);

                                /* Código de prueba
                                System.out.println("Integer[] notas = {null,null,null};");
                                Integer[] notas = {null,null,null};

                                for(int j=0;j<3;j++){
                                    if(objjson.get("nota"+(j+1)).toString().equals("null")) {
                                        System.out.println("es null, pero no va a pasar");
                                    }else{
                                        System.out.println("nota"+(j+1)+" = "+objjson.get("nota"+(j+1)));
                                        notas[j] = Integer.parseInt(objjson.get("nota" + (j + 1)).toString());
                                    }
                                }*/

                                listaNotas.add(new Nota(
                                        objjson.getString("curso"),
                                        objjson.getInt("nota1"),
                                        objjson.getInt("nota2"),
                                        objjson.getInt("nota3")
                                        /*notas[0],
                                        notas[1],
                                        notas[2]*/
                                ));
                            }
                            notaAdapter.agregarNota(listaNotas);
                        }catch(JSONException ex){
                            Log.e("ErrorVolley", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorRequest", volleyError.getMessage());
            }
        });
        queue.add(peticion);
    }

    // 1. Método para OPTENER todos lo años del estudiante
    private void getAnios() {
        String url = "http://"+ Url.IP+":"+Url.PUERTO+"/idat/rest/nota/anios?dniEstudiante="+dniEstudiante+"&?wsdl";
        queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray j = null;
                        JSONArray resultados;
                        try{
                            j = new JSONArray(response);
                            resultados = j;
                            llenarSpinner(resultados);
                        }catch (JSONException e){
                            System.out.println("Fallo en getAnios()");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                System.out.println("ERROR: " + volleyError.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    // 2. Método para cargar años en spinner + onItemSelectedListener
    private void llenarSpinner(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                anios.add(jsonArray.get(i).toString());
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        spAnio.setAdapter(new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, anios));

        spAnio.setSelection(anios.size()-1);

        // Listener
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