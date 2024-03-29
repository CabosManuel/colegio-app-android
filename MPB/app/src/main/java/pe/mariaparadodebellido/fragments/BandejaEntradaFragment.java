package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.NotificacionAdapter;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class BandejaEntradaFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private String dniApoderado = "";
    private String dniEstudiante = "";

    private String t1 = "x", t2 = "x", t3 = "x"; // Variables para la consulta al WebService

    private Spinner spEstudiantes;
    private ArrayList<Estudiante> estudiantes = new ArrayList<>();

    private ChipGroup cgTipoNotificación;
    private Chip cCitationes, cComunicados, cPermisos;

    private RecyclerView rvNotificaciones;
    private NotificacionAdapter notificacionAdapter;
    private RequestQueue queue;
    private ArrayList<Notificacion> listaNotificaciones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewFragment = inflater.inflate(R.layout.fragment_bandeja_entrada, container, false);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "usuario no existe"));
            dniApoderado = eJson.getString("dniApoderado");
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }


        spEstudiantes = viewFragment.findViewById(R.id.sp_be_estudiantes);
        getEstudiantes();

        cgTipoNotificación = viewFragment.findViewById(R.id.cg_be_notficiaciones);
        cCitationes = viewFragment.findViewById(R.id.chip_citatciones);
        cComunicados = viewFragment.findViewById(R.id.chip_comunicados);
        cPermisos = viewFragment.findViewById(R.id.chip_permisos);

        cCitationes.setOnCheckedChangeListener(this);
        cComunicados.setOnCheckedChangeListener(this);
        cPermisos.setOnCheckedChangeListener(this);

        rvNotificaciones = viewFragment.findViewById(R.id.rv_notificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        notificacionAdapter = new NotificacionAdapter(getContext());
        rvNotificaciones.setAdapter(notificacionAdapter);

        // Hacer una consulta por defecto con todos los chips activados
        cCitationes.setChecked(true);
        cComunicados.setChecked(true);
        cPermisos.setChecked(true);
        getNotificaciones();

        return viewFragment;
    }

    // Método cuando se activa cualquier chip
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ArrayList<Integer> ids = (ArrayList<Integer>) cgTipoNotificación.getCheckedChipIds(); //Obtener ids de chips actualmente seleccionados
        ArrayList<CharSequence> titulos = new ArrayList<>(); // Reiniciar lista donde se guardan los nombres de los textos chips
        t1 = "x";
        t2 = "x";
        t3 = "x"; // Reiniciar variables que se envian para consultar al WebService

        // Recorrer y almacenar titulos
        for (Integer id : ids) {
            Chip chip = cgTipoNotificación.findViewById(id);
            titulos.add(chip.getText());
        }

        // Modificar variables si es que el chip esta seleccionado
        for (CharSequence titulo : titulos) {
            if ("Citaciones".equals(titulo)) {
                t1 = "citacion";
            } else if ("Comunicados".equals(titulo)) {
                t2 = "comunicado";
            } else if ("Permisos".equals(titulo)) {
                t3 = "permiso";
            }
        }

        getNotificaciones(); // Consultar con los chip seleccionados
    }

    private void getNotificaciones() {
        listaNotificaciones = new ArrayList<>(); // Reiniciar lista
        String url = Url.URL_BASE + "/idat/rest/apoderados/bandeja_entrada" +
                "?dniEstudiante=" + dniEstudiante + "&tipo1=" + t1 + "&tipo2=" + t2 + "&tipo3=" + t3;
        queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);

                                Notificacion n = new Notificacion();
                                n.setIdNofiticacion(objjson.getInt("idNofiticacion"));
                                n.setTipo(objjson.getString("tipo"));
                                n.setFechaEnvio(objjson.getString("fechaEnvio"));
                                n.setTitulo(objjson.getString("titulo"));
                                n.setDescripcion(objjson.getString("descripcion"));
                                n.setDniEstudiante(objjson.getString("dniEstudiante"));

                                // Cuando NO SEA UN COMUNICADO agregar el estado y fecha limite
                                if (!n.getTipo().equals("comunicado")) {
                                    n.setFechaLimite(objjson.getString("fechaLimite"));
                                    n.setEstado(objjson.getString("estado").charAt(0));
                                }
                                listaNotificaciones.add(n);
                                notificacionAdapter.agregarNotificaciones(listaNotificaciones);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar notificaciones.", Toast.LENGTH_SHORT).show();
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

                // Acvitvar todos los chips y consultar
                cCitationes.setChecked(true);
                cComunicados.setChecked(true);
                cPermisos.setChecked(true);
                getNotificaciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}