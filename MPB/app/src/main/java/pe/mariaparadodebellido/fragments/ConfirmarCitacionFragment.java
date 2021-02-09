package pe.mariaparadodebellido.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class ConfirmarCitacionFragment extends DialogFragment implements View.OnClickListener /*implements View.OnClickListener*/ {

    private View viewFragment;
    private TextView tvTitulo, tvDescripcion, tvFechaLimite, tvEstado, tvFechaEnvio, tvConfirmar, tvRechazar;
    private ImageView ivEstado;

    private Notificacion notificacion;
    private RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getContext());
        if (getArguments() != null) {
            notificacion = new Notificacion(
                    getArguments().getInt("id_notificacion"),
                    getArguments().getString("tipo"),
                    getArguments().getString("f_envio"),
                    getArguments().getString("f_limite"),
                    getArguments().getString("titulo"),
                    getArguments().getString("descripcion"),
                    getArguments().getChar("estado"),
                    getArguments().getString("dni_estudiante"),
                    getArguments().getInt("color"),
                    getArguments().getString("estado_completo"),
                    getArguments().getInt("ic_estado")
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        viewFragment = inflater.inflate(R.layout.fragment_confirmar_citacion,null);
        tvTitulo = viewFragment.findViewById(R.id.tv_cit_titulo);
        tvDescripcion = viewFragment.findViewById(R.id.tv_cit_descripcion);
        tvFechaLimite = viewFragment.findViewById(R.id.tv_cit_fecha_limite);
        ivEstado = viewFragment.findViewById(R.id.iv_cit_estado);
        tvEstado = viewFragment.findViewById(R.id.tv_cit_estado);
        tvFechaEnvio = viewFragment.findViewById(R.id.tv_cit_fecha);
        cargarDatos();
        tvConfirmar = viewFragment.findViewById(R.id.tv_cit_confirmar);
        tvRechazar = viewFragment.findViewById(R.id.tv_cit_rechazar);

        if(!notificacion.getEstado().equals('P')){
            tvConfirmar.setVisibility(View.INVISIBLE);
            tvRechazar.setVisibility(View.INVISIBLE);
        }else {
            tvConfirmar.setOnClickListener(this);
            tvRechazar.setOnClickListener(this);
        }

        builder.setView(viewFragment);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cit_confirmar:
                confirmarCitacion(notificacion.getIdNofiticacion(), 'C');
                break;
            case R.id.tv_cit_rechazar:
                confirmarCitacion(notificacion.getIdNofiticacion(), 'R');
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarDatos() {
        tvTitulo.setText(notificacion.getTitulo());
        tvDescripcion.setText(notificacion.getDescripcion());
        ivEstado.setImageResource(notificacion.getIconoEstado());
        tvEstado.setText(notificacion.getEstadoCompleto());
        tvEstado.setTextColor(ContextCompat.getColor(getContext(), notificacion.getColor()));

        String fecha1 = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(notificacion.getFechaLimite().substring(0, 10)));
        String hora1 = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(notificacion.getFechaLimite().substring(11)));
        String fechaLimite = fecha1 + " " + hora1;
        tvFechaLimite.setText(fechaLimite);

        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(notificacion.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(notificacion.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        tvFechaEnvio.setText(fechaEnvio);
    }

    private void confirmarCitacion(Integer citacionId, Character estado) {
        String url = Url.URL_BASE + "/idat/rest/notificacion/citacion/" + citacionId + "/" + estado;

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(estado.equals('C')){
                            Toast.makeText(getActivity(), "Citación confirmada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Citación rechazada", Toast.LENGTH_SHORT).show();
                        }
                        BandejaEntradaFragment bdf = new BandejaEntradaFragment();

                        dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(peticion);
    }
}