package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import pe.mariaparadodebellido.adapter.NotificacionAdapter;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class BandejaEntradaActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener /*implements ChipGroup.OnCheckedChangeListener*/ {

    private final String DNI_APORDERADO = "06662516";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja_entrada);

        spEstudiantes = findViewById(R.id.sp_be_estudiantes);
        getEstudiantes();

        cgTipoNotificación = findViewById(R.id.cg_be_notficiaciones);
        cCitationes = findViewById(R.id.chip_citatciones);
        cComunicados = findViewById(R.id.chip_comunicados);
        cPermisos = findViewById(R.id.chip_permisos);

        cCitationes.setOnCheckedChangeListener(this);
        cComunicados.setOnCheckedChangeListener(this);
        cPermisos.setOnCheckedChangeListener(this);

        rvNotificaciones = findViewById(R.id.rv_notificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(BandejaEntradaActivity.this));
        notificacionAdapter = new NotificacionAdapter(BandejaEntradaActivity.this);
        rvNotificaciones.setAdapter(notificacionAdapter);

        // Consultar con todos los chips activados al abrir Activity
        cCitationes.setChecked(true);
        cComunicados.setChecked(true);
        cPermisos.setChecked(true);
        getNotificaciones();
    }

    // Método cuando se activa cualquier chip
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        ArrayList<Integer> ids = (ArrayList<Integer>) cgTipoNotificación.getCheckedChipIds(); //Obtener ids de chips actualmente seleccionados
        ArrayList<CharSequence> titulos = new ArrayList<>(); // Reiniciar lista donde se guardan los nombres de los textos chips
        t1 = "x"; t2 = "x"; t3 = "x"; // Reiniciar variables que se envian para consultar al WebService

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

        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/apoderados/bandeja_entrada?dniEstudiante=" + dniEstudiante + "&tipo1=" + t1 + "&tipo2=" + t2 + "&tipo3=" + t3 + "&?wsdl";
        queue = Volley.newRequestQueue(BandejaEntradaActivity.this);
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

                                // Cuando no sea un comunicado agregar el estado y fecha limite
                                if (!n.getTipo().equals("comunicado")) {
                                    n.setFechaLimite(objjson.getString("fechaLimite"));
                                    n.setEstado(objjson.getString("estado").charAt(0));
                                }

                                listaNotificaciones.add(n);

                                notificacionAdapter.agregarNotificaciones(listaNotificaciones);
                            }
                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                            ex.printStackTrace();
                            Toast.makeText(BandejaEntradaActivity.this, "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e("ErrorVolley", volleyError.getMessage());
                Toast.makeText(BandejaEntradaActivity.this, "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }

    private void getEstudiantes() {
        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/apoderados/nombre_estudiantes/" + DNI_APORDERADO + "?wsdl";
        queue = Volley.newRequestQueue(BandejaEntradaActivity.this);
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
                            Toast.makeText(BandejaEntradaActivity.this, "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e("ErrorVolley", volleyError.getMessage());
                Toast.makeText(BandejaEntradaActivity.this, "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }

    private void llenarSpinner() {
        /** ArrayAdapter<Estudiante>: Almacena un lista de objectos Estudiante (en el que almaceno el
         *  id y nombre, se podría almacenar más).
         *
         *  Elspinner se llena solo con el "nombre" porque al model Estudiante se le está sobreescribiendo
         *  el método "toString()" que indica que solo se llene con ese, sucede algo como esto:
         *                 ArrayAdapter<Estudiante> = ArrayAdapter<Estudiante.toString()>
         */
        spEstudiantes.setAdapter(new ArrayAdapter<Estudiante>(BandejaEntradaActivity.this, android.R.layout.simple_spinner_dropdown_item, estudiantes));

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