package pe.mariaparadodebellido;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import pe.mariaparadodebellido.adapter.AsistenciaAdapter;
import pe.mariaparadodebellido.model.Asistencia;
import pe.mariaparadodebellido.util.Url;

// Esta @Anotación es para que pueda usar la clase LocalDate
@RequiresApi(api = Build.VERSION_CODES.O)
public class ConsultarAsistenciasActivity extends AppCompatActivity {

    private String dniEstudiante = "";

    private Integer cursoId;

    private ImageView ivCurso;
    private TextView tvCurso, tvNFaltas, tvNAsistencias;
    private Integer nAsistencias = 0, nFaltas = 0; // Contador en 0

    private Spinner spMes;
    private ArrayList<String> meses = new ArrayList<>(); // Array para los meses del spinner

    private RecyclerView rvAsistencias;
    private AsistenciaAdapter asistenciaAdapter;
    ArrayList<Asistencia> listaAsistencias;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_asistencias);

        SharedPreferences preferences = this/*.getActivity()*/.getSharedPreferences("info_usuario", MODE_PRIVATE);
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

        // Datos traido del Intent en el CursoAdapter
        Bundle datos = getIntent().getExtras();
        cursoId = datos.getInt("curso_id");
        Integer icono = datos.getInt("icono");
        String nombreCurso = datos.getString("nombre");

        ivCurso = findViewById(R.id.iv_ca_icono_curso);
        tvCurso = findViewById(R.id.tv_ca_curso);
        ivCurso.setImageResource(icono);
        tvCurso.setText(nombreCurso);

        tvNFaltas = findViewById(R.id.tv_n_faltas);
        tvNAsistencias = findViewById(R.id.tv_n_asistencias);

        spMes = findViewById(R.id.sp_mes);
        getMeses(); // Método que llena los meses que tenga el estudiante

        // RecyclerView
        rvAsistencias = findViewById(R.id.rv_asistencias);
        rvAsistencias.setLayoutManager(new GridLayoutManager(
                ConsultarAsistenciasActivity.this, 4)); // GridLayoutManager 4: cantidad de cuadraditos
        asistenciaAdapter = new AsistenciaAdapter(ConsultarAsistenciasActivity.this);
        rvAsistencias.setAdapter(asistenciaAdapter);
        listaAsistencias = new ArrayList<>();

        queue = Volley.newRequestQueue(this);
    }

    private void getConsultarAsistencias(Integer mes) {
        String url = Url.URL_BASE + "/idat/rest/asistencia/consultar_asistencias?dniEstudiante=" + dniEstudiante + "&mes=" + mes + "&cursoId=" + cursoId;
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject asistenciaJson = jsonArray.getJSONObject(i);

                                listaAsistencias.add(new Asistencia(
                                        asistenciaJson.getString("fecha"),
                                        asistenciaJson.getBoolean("estado")
                                ));
                            }
                            asistenciaAdapter.agregarAsistencia(listaAsistencias);

                            // Aquí aumentan los contadores
                            for (Asistencia a : listaAsistencias) {
                                if (a.getEstado()) {
                                    nAsistencias++;
                                } else {
                                    nFaltas++;
                                }
                            }
                            tvNFaltas.setText(nFaltas.toString());
                            tvNAsistencias.setText(nAsistencias.toString());

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(ConsultarAsistenciasActivity.this, "Error al cargar asistencias.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(ConsultarAsistenciasActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }

    // 1. Método para OPTENER todos lo meses que asistió la estudiante
    private void getMeses() {
        String url = Url.URL_BASE + "/idat/rest/asistencia/meses?dniEstudiante=" + dniEstudiante;
        queue = Volley.newRequestQueue(ConsultarAsistenciasActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray resultados = new JSONArray(response);
                            llenarSpinner(resultados);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ConsultarAsistenciasActivity.this, "Error al cargar meses.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(ConsultarAsistenciasActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    // 2. Método para cargar MESES en spinner + onItemSelectedListener
    private void llenarSpinner(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                meses.add(convertirMes(jsonArray.getInt(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spMes.setAdapter(new ArrayAdapter<String>(ConsultarAsistenciasActivity.this,
                android.R.layout.simple_spinner_dropdown_item, meses));

        // Listener
        spMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // Lo que pasa cuando selecciona un item del spinner
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Reinicio de contador y tarjetas
                nAsistencias = 0;
                nFaltas = 0;
                listaAsistencias = new ArrayList<>();

                String nombreMes = spMes.getSelectedItem().toString();
                getConsultarAsistencias(convertirMes(nombreMes));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Despues de toda la configuración seleccionar el último més (el más reciente)
        spMes.setSelection(meses.size() - 1);
    }

    // Convertir los nombres de meses a números
    private Integer convertirMes(String nombreMes){
        Integer mes = 0;
        switch (nombreMes) {
            case "Enero":
                mes = 1;
                break;
            case "Febrero":
                mes = 2;
                break;
            case "Marzo":
                mes = 3;
                break;
            case "Abril":
                mes = 4;
                break;
            case "Mayo":
                mes = 5;
                break;
            case "Junio":
                mes = 6;
                break;
            case "Julio":
                mes = 7;
                break;
            case "Agosto":
                mes = 8;
                break;
            case "Septiembre":
                mes = 9;
                break;
            case "Octubre":
                mes = 10;
                break;
            case "Noviembre":
                mes = 11;
                break;
            case "Diciembre":
                mes = 12;
                break;
            default:
                mes = 13;
                break;
        }
        return mes;
    }

    // Convertir numero de meses a nombre
    private String convertirMes(Integer mes){
        String nombreMes = "";
        switch (mes) {
            case 1:
                nombreMes = "Enero";
                break;
            case 2:
                nombreMes = "Febrero";
                break;
            case 3:
                nombreMes = "Marzo";
                break;
            case 4:
                nombreMes = "Abril";
                break;
            case 5:
                nombreMes = "Mayo";
                break;
            case 6:
                nombreMes = "Junio";
                break;
            case 7:
                nombreMes = "Julio";
                break;
            case 8:
                nombreMes = "Agosto";
                break;
            case 9:
                nombreMes = "Septiembre";
                break;
            case 10:
                nombreMes = "Octubre";
                break;
            case 11:
                nombreMes = "Noviembre";
                break;
            case 12:
                nombreMes = "Diciembre";
                break;
            default:
                nombreMes = "No mapeado.";
                break;
        }
        return nombreMes;
    }
}