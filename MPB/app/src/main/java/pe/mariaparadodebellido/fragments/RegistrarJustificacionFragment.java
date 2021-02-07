package pe.mariaparadodebellido.fragments;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class RegistrarJustificacionFragment extends Fragment implements View.OnClickListener {

    private EditText txtFechaInasistencia, txtJustificacion, txttitulo;
    private ImageView ivImagen;
    private Button btnEnviar, btnCancelar, btnSeleccionar, btnBorrar;

    RequestQueue request;
    RequestQueue Justi_queue;

    public RegistrarJustificacionFragment() {
        // Required empty public constructor
    }

    public static RegistrarJustificacionFragment newInstance(String param1, String param2) {
        RegistrarJustificacionFragment fragment = new RegistrarJustificacionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment =  inflater.inflate(R.layout.fragment_registrar_justificacion, container, false);

        txtFechaInasistencia = viewFragment.findViewById(R.id.txtFechaInasistencia);
        txtJustificacion = viewFragment.findViewById(R.id.txt_Justificacion);
        txttitulo = viewFragment.findViewById(R.id.txttitulo);
        ivImagen = viewFragment.findViewById(R.id.iv_imagen);
        btnSeleccionar = viewFragment.findViewById(R.id.btn_seleccionar);
        btnBorrar = viewFragment.findViewById(R.id.btn_borrar);
        btnEnviar = viewFragment.findViewById(R.id.btn_Enviar);
        btnCancelar = viewFragment.findViewById(R.id.btn_Cancelar);
        ivImagen.setOnClickListener(this);
        btnSeleccionar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        return viewFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtFechaInasistencia:
                mostrarCalendario();
                break;
            case R.id.btn_borrar:
                break;
            case R.id.btn_Enviar:
                if (validar()) {
                    cargarWebServices();
                }
                break;
            case R.id.btn_Cancelar:
                break;
            default:
                Toast.makeText(getContext(), "default", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean validar() {
        boolean retorno = true;

        //int jjustificacionId;
        String jfechaJustificacion, jjustificacion, jtitulo;

        jfechaJustificacion = txtFechaInasistencia.getText().toString();
        jjustificacion = txtJustificacion.getText().toString();
        jtitulo = txttitulo.getText().toString();

        /*
            if(jfechaJustificacion.isEmpty()){
                jfechaJustificacion.setError("Este campo no puede quedar vacio");
                retorno = false;
            }
            if(fechaEnvio.isEmpty()){
                apellido.setError("Este campo no puede quedar vacio");
                retorno = false;
            }
            if(fechaJustificacion.isEmpty()){
                edad.setError("Este campo no puede quedar vacio");
                retorno = false;
            }
            */
        return retorno;
    }

    private void cargarWebServices() {
        String urlJusti = Url.URL_BASE+"/justificacion/registrar";
        final JSONObject jsonObject_principal = new JSONObject();
        final JSONObject jsonObject_justiID = new JSONObject();

        try {
            jsonObject_principal.put("justificacionid", jsonObject_justiID);
            jsonObject_principal.put("descripcion", txtJustificacion);
            jsonObject_principal.put("fecha", txtFechaInasistencia.getText().toString());
            jsonObject_principal.put("titulo", txttitulo.getText().toString());
        } catch (JSONException e) {

        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, urlJusti, jsonObject_principal,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(RegistrarJustificacion.this, ListarJustificacionesFragment.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }

                }) {

        };

        request.add(postRequest);
    }

    // Método para funcionalidad del Calendario de "txtFechaInasistencia"
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mostrarCalendario() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(txtFechaInasistencia.getText(), formato);

        int anio = fecha.getYear();
        int mes = fecha.getMonthValue() - 1;
        int dia = fecha.getDayOfMonth();

        DatePickerDialog dtpCalendario = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {
                mesSeleccionado++;
                // Esta parte esporque el mes y día vienen sin el 0 a la izquierda
                String diaConcatenado = String.valueOf(diaSeleccionado), mesConcatenado = String.valueOf(mesSeleccionado);
                if (diaSeleccionado < 10) diaConcatenado = "0" + diaSeleccionado;
                if (mesSeleccionado < 10) mesConcatenado = "0" + mesSeleccionado;

                txtFechaInasistencia.setText(diaConcatenado + "/" + mesConcatenado + "/" + anioSeleccionado);
            }
        }, anio, mes, dia);

        // Fechha mínima
        Calendar c = Calendar.getInstance();
        c = Calendar.getInstance();
        dtpCalendario.getDatePicker().setMinDate(c.getTimeInMillis());

        // Fechha máxima
        fecha.plusDays(Notificacion.FECHA_MAX);
        c.set(fecha.getDayOfYear(), fecha.getMonth().getValue(), fecha.getDayOfMonth());
        dtpCalendario.getDatePicker().setMaxDate(c.getTimeInMillis());

        dtpCalendario.show();
    }
}