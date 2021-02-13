package pe.mariaparadodebellido.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Justificacion;

public class JustificacionFragment extends DialogFragment {

    private View viewFragment;
    private TextView tvTitulo, tvDescripcion,tvFechaEnvio, tvCerrar;
    private ImageView ivJustificacion;

    private final String RUTA = "justificaciones/";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private Justificacion just;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            just = new Justificacion(
                    getArguments().getInt("id_justificacion"),
                    getArguments().getString("titulo"),
                    getArguments().getString("f_envio"),
                    getArguments().getString("descripcion")
            );
            System.out.println(just.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        viewFragment = inflater.inflate(R.layout.fragment_justificacion,null);
        tvTitulo = viewFragment.findViewById(R.id.tv_just_titulo);
        tvDescripcion = viewFragment.findViewById(R.id.tv_just_descripcion);
        tvFechaEnvio = viewFragment.findViewById(R.id.tv_just_fecha);
        ivJustificacion = viewFragment.findViewById(R.id.iv_justificacion);
        cargarDatos();

        tvCerrar = viewFragment.findViewById(R.id.tv_just_cerrar);
        tvCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(viewFragment);
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarDatos() {
        tvTitulo.setText(just.getTitulo());
        tvDescripcion.setText(just.getDescripcion());

        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(just.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(just.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        tvFechaEnvio.setText(fechaEnvio);

        String archivo = just.getFechaEnvio().substring(0, 10) + "_" + just.getJustificacionId();
        storageRef = storage.getReference(RUTA + archivo +".jpg");
        Glide.with(getContext())
                .load(storageRef)
                .into(ivJustificacion);
    }
}