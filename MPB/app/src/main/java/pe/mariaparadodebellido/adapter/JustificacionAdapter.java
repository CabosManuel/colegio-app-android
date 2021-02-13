package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.fragments.JustificacionFragment;
import pe.mariaparadodebellido.model.Justificacion;

public class JustificacionAdapter extends  RecyclerView.Adapter<JustificacionAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Justificacion> listaJustificaciones;

    private final String RUTA = "justificaciones/";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    public JustificacionAdapter(Context context) {
        this.context = context;
        listaJustificaciones = new ArrayList<>();
    }

    @NonNull
    @Override
    public JustificacionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_justificacion,parent,false);
        return new JustificacionAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull JustificacionAdapter.ViewHolder holder, int position) {
        Justificacion justificacion = listaJustificaciones.get(position);

        holder.etTitulo.setText(justificacion.getTitulo());
        holder.etDescripcion.setText(justificacion.getDescripcion());

        String archivo = justificacion.getFechaEnvio().substring(0, 10) + "_" + justificacion.getJustificacionId();
        storageRef = storage.getReference(RUTA + archivo +".jpg");

        Glide.with(context)
                .load(storageRef)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.ivJustificacion.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.ivJustificacion);

        // Formato de fecha
        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(justificacion.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(justificacion.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        holder.etFecha.setText(fechaEnvio);

        holder.cvJustificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("on click");
                System.out.println(justificacion.toString());
                Bundle bundleJusti = new Bundle();
                bundleJusti.putInt("id_justificacion", justificacion.getJustificacionId());
                bundleJusti.putString("titulo", justificacion.getTitulo());
                bundleJusti.putString("descripcion", justificacion.getDescripcion());
                bundleJusti.putString("f_envio", justificacion.getFechaEnvio());

                DialogFragment dialogFragment = new JustificacionFragment();
                String tag = "JustificacionFragment";
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                dialogFragment.setArguments(bundleJusti);
                dialogFragment.show(fragmentManager, tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaJustificaciones.size();
    }

    public void agregarJustificacion(ArrayList<Justificacion> lista) {
        listaJustificaciones.clear();
        listaJustificaciones.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView etTitulo, etFecha, etDescripcion;
        ImageView ivJustificacion;
        CardView cvJustificacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etTitulo = itemView.findViewById(R.id.Id_titulo);
            etFecha = itemView.findViewById(R.id.id_fecha);
            etDescripcion = itemView.findViewById(R.id.id_descripcion);
            ivJustificacion = itemView.findViewById(R.id.iv_justificacion);
            cvJustificacion = itemView.findViewById(R.id.card_justificacion);
        }
    }
}


