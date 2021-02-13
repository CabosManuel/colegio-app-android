package pe.mariaparadodebellido.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class ComunicadoFragment extends DialogFragment {

    private View viewFragment;
    private TextView tvTitulo, tvDescripcion,tvFechaEnvio;

    private Notificacion notificacion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notificacion = new Notificacion(
                    getArguments().getInt("id_notificacion"),
                    getArguments().getString("tipo"),
                    getArguments().getString("f_envio"),
                    getArguments().getString("titulo"),
                    getArguments().getString("descripcion"),
                    getArguments().getString("dni_estudiante"),
                    getArguments().getInt("color")
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        viewFragment = inflater.inflate(R.layout.fragment_comunicado,null);
        tvTitulo = viewFragment.findViewById(R.id.tv_com_titulo);
        tvDescripcion = viewFragment.findViewById(R.id.tv_com_descripcion);
        tvFechaEnvio = viewFragment.findViewById(R.id.tv_com_fecha);
        cargarDatos();

        builder.setView(viewFragment);
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarDatos() {
        tvTitulo.setText(notificacion.getTitulo());
        tvDescripcion.setText(notificacion.getDescripcion());

        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(notificacion.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(notificacion.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        tvFechaEnvio.setText(fechaEnvio);
    }
}