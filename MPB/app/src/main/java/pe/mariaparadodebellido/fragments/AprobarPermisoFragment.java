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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Permiso;
import pe.mariaparadodebellido.util.Url;

public class AprobarPermisoFragment extends DialogFragment implements View.OnClickListener {

    private View viewFragment;
    private TextView tvTitulo, tvDescripcion, tvFechaLimite, tvEstado, tvFechaEnvio, tvGuardar, tvCancelar;
    private Switch swiAprobacion;
    private ImageView ivEstado;

    private Permiso permiso;
    private RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getContext());
        if (getArguments() != null) {
            permiso = new Permiso(
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
        viewFragment = inflater.inflate(R.layout.fragment_aprobar_permiso, null);
        tvTitulo = viewFragment.findViewById(R.id.tv_per_titulo);
        tvDescripcion = viewFragment.findViewById(R.id.tv_per_descripcion);
        tvFechaLimite = viewFragment.findViewById(R.id.tv_per_fecha_limite);
        ivEstado = viewFragment.findViewById(R.id.iv_per_estado);
        tvEstado = viewFragment.findViewById(R.id.tv_per_estado);
        tvFechaEnvio = viewFragment.findViewById(R.id.tv_per_fecha);
        swiAprobacion = viewFragment.findViewById(R.id.swi_aprobacion);
        tvGuardar = viewFragment.findViewById(R.id.tv_per_guardar);
        tvCancelar = viewFragment.findViewById(R.id.tv_per_cancelar);
        tvCancelar.setOnClickListener(this);
        cargarDatos();

        builder.setView(viewFragment);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_per_guardar:
                if (swiAprobacion.isChecked()) {
                    aprobarPermiso(permiso.getIdPermiso(), 'A');
                } else {
                    aprobarPermiso(permiso.getIdPermiso(), 'D');
                }
                break;
            case R.id.tv_per_cancelar:
                dismiss();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarDatos() {
        tvTitulo.setText(permiso.getTitulo());
        tvDescripcion.setText(permiso.getDescripcion());
        ivEstado.setImageResource(permiso.getIconoEstado());
        tvEstado.setText(permiso.getEstadoCompleto());
        tvEstado.setTextColor(ContextCompat.getColor(getContext(), permiso.getColor()));

        if (permiso.getEstado().equals('A')) swiAprobacion.setChecked(true);
        else swiAprobacion.setChecked(false);

        String fecha1 = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(permiso.getFechaLimite().substring(0, 10)));
        String hora1 = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(permiso.getFechaLimite().substring(11)));
        String fechaLimite = fecha1 + " " + hora1;
        tvFechaLimite.setText(fechaLimite);

        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(permiso.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(permiso.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        tvFechaEnvio.setText(fechaEnvio);

        if (!permiso.getEstado().equals('P')) {
            tvGuardar.setVisibility(View.INVISIBLE);
            swiAprobacion.setEnabled(false);
        } else {
            tvGuardar.setOnClickListener(this);
        }
    }

    private void aprobarPermiso(Integer permisoId, Character estado) {
        String url = Url.URL_BASE + "/idat/rest/notificacion/cambiar_estado/" + permisoId + "/" + estado;

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (estado.equals('A')) {
                            Toast.makeText(getActivity(), "Permiso aprobado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Permiso desaprobado", Toast.LENGTH_SHORT).show();
                        }

                        dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }
}