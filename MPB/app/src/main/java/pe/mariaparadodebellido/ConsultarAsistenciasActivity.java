package pe.mariaparadodebellido;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import pe.mariaparadodebellido.adapter.AsistenciaAdapter;
import pe.mariaparadodebellido.model.Asistencia;
import pe.mariaparadodebellido.util.Url;

// Esta anotación es para que pueda usar la clase LocalDate
@RequiresApi(api = Build.VERSION_CODES.O)
public class ConsultarAsistenciasActivity extends AppCompatActivity {

    private String dniEstudiante = "61933011";

    private TextView tvNFaltas, tvNAsistencias;
    private Integer nAsistencias = 0, nFaltas = 0; // Contador en 0

    private EditText etFecha;
    private DateTimeFormatter formatoSQL = DateTimeFormatter.ofPattern("yyyy-MM-dd"), // formato para enviar al WS
            formatoNormal = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // formato para mostrar al usuario
    private LocalDate hoy = LocalDate.now();
    private LocalDate primerDia = hoy.withDayOfMonth(1); // El primer día del més actual

    private RecyclerView rvAsistencias;
    private AsistenciaAdapter asistenciaAdapter;
    ArrayList<Asistencia> listaAsistencias;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_asistencias);

        tvNFaltas = findViewById(R.id.tv_n_faltas);
        tvNAsistencias = findViewById(R.id.tv_n_asistencias);

        etFecha = findViewById(R.id.et_fecha_asistencia);
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lista vacía nueva y reinicio de contadores cada vez que quiera
                // elegir una fecha nueva
                listaAsistencias = new ArrayList<>();
                nAsistencias = 0;
                nFaltas = 0;

                mostrarCalendario(etFecha);
            }
        });

        // RecyclerView
        rvAsistencias = findViewById(R.id.rv_asistencias);
        rvAsistencias.setLayoutManager(new GridLayoutManager(
                ConsultarAsistenciasActivity.this, 5));
        asistenciaAdapter = new AsistenciaAdapter(ConsultarAsistenciasActivity.this);
        rvAsistencias.setAdapter(asistenciaAdapter);
        listaAsistencias = new ArrayList<>();

        queue = Volley.newRequestQueue(this);

        etFecha.setText(hoy.format(formatoNormal)); // Cargo por defecto en el EditText la fecha de hoy
        getAsistecias(primerDia.format(formatoSQL)); // Consulta al WS por defecto con la fecha actual
    }

    // Variables para el DatePickerDialog
    private final Calendar FECHA_MAX = Calendar.getInstance();
    private Calendar cldr = Calendar.getInstance();
    private DatePickerDialog dtpCalendario;

    private void mostrarCalendario(final EditText txt) {
        int dia = cldr.get(Calendar.DAY_OF_MONTH);
        int mes = cldr.get(Calendar.MONTH);
        int año = cldr.get(Calendar.YEAR);

        dtpCalendario = new DatePickerDialog(ConsultarAsistenciasActivity.this, new DatePickerDialog.OnDateSetListener() {

            /* Este método es lo que pasa cuando selecciona una fecha en el calendario */
            @Override
            public void onDateSet(DatePicker view, int añoSeleccionado, int mesSeleccionado, int diaSeleccionado) {

                // Esta parte esporque el mes y día vienen sin el 0 a la izquierda ---------------------------------------------
                String diaConcatenado = String.valueOf(diaSeleccionado), mesConcatenado = String.valueOf(mesSeleccionado + 1);
                if (diaSeleccionado < 10) diaConcatenado = "0" + diaSeleccionado;
                if (mesSeleccionado < 10) mesConcatenado = "0" + mesSeleccionado;
                // -------------------------------------------------------------------------------------------------------------

                txt.setText(diaConcatenado + "-" + mesConcatenado + "-" + añoSeleccionado); // Mostrar fecha que seleccionó
                cldr.set(añoSeleccionado, mesSeleccionado, diaSeleccionado); // Persistir en el calendario la fecha que seleccionó

                getAsistecias(añoSeleccionado + "-" + mesConcatenado + "-" + "01"); // Consultar asistencias (siempre desde el primer día)
            }
        }, año, mes, dia);

        dtpCalendario.getDatePicker().setMaxDate(FECHA_MAX.getTimeInMillis()); // Fecha máxima (siempre es hoy)
        dtpCalendario.show(); // Mostrar calendario
    }

    private void getAsistecias(String fecha) {
        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/asistencia/consultar_asistencias?dniEstudiante=" + dniEstudiante + "&fecha=" + fecha + "&?wsdl";
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
                            Log.e("ErrorVolley", ex.getMessage());
                            Toast.makeText(ConsultarAsistenciasActivity.this, "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorRequest", volleyError.getMessage());
                Toast.makeText(ConsultarAsistenciasActivity.this, "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }
}