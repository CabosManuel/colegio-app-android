package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.fragments.AprobarPermisoFragment;
import pe.mariaparadodebellido.fragments.ComunicadoFragment;
import pe.mariaparadodebellido.fragments.ConfirmarCitacionFragment;
import pe.mariaparadodebellido.model.Notificacion;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Notificacion> notificaciones;

    public NotificacionAdapter(Context context) {
        this.context = context;
        notificaciones = new ArrayList<>();
    }

    @NonNull
    @Override
    public NotificacionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notificacion, parent, false);
        return new NotificacionAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NotificacionAdapter.ViewHolder holder, int position) {
        Notificacion n = notificaciones.get(position);

        holder.tvTitulo.setText(n.getTitulo());

        // Texto reducicdo y puntos suspensivos..
        String descripcion = n.getDescripcion().substring(0, 110);
        descripcion += "...";
        holder.tvDescripcion.setText(descripcion);

        // Formato de fecha
        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(n.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(n.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        holder.tvFecha.setText(fechaEnvio);

        holder.tvEstado.setVisibility(View.VISIBLE);
        holder.ivEstado.setVisibility(View.VISIBLE);

        String estado = "";
        int color = 0;
        int icono = 0;

        if (n.getTipo().equals("comunicado")) {
            // Los comunicados no tienen estado, por eso se ocultan estos elementos:
            holder.tvEstado.setVisibility(View.INVISIBLE);
            holder.ivEstado.setVisibility(View.INVISIBLE);
            holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_amarillo));
        }
        // (permiso y citación) cuando la notificación tenga "estado"
        else {
            if (n.getTipo().equals("citacion"))
                holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_verde));
            else
                holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_celeste));

            // switch para texto y color del estado + iciono
            switch (n.getEstado()) {
                case 'P':
                    estado = "Pendiente";
                    color = R.color.rojo_pendiente;
                    icono = R.drawable.ic_estado_pendiente;
                    break;
                case 'V':
                    estado = "Vencido";
                    color = R.color.naranja_vencido;
                    icono = R.drawable.ic_estado_vencido;
                    break;
                case 'C':
                    estado = "Confirmado";
                    color = R.color.verde_confirmado;
                    icono = R.drawable.ic_estado_confirmado;
                    break;
                case 'R':
                    estado = "Rechazado";
                    color = R.color.rojo_rechazado;
                    icono = R.drawable.ic_estado_rechazado;
                    break;
                case 'A':
                    estado = "Aprobado";
                    color = R.color.verde_confirmado;
                    icono = R.drawable.ic_estado_aprobado;
                    break;
                case 'D':
                    estado = "Desaprobado";
                    color = R.color.rojo_rechazado;
                    icono = R.drawable.ic_estado_desaprobado;
                    break;
            }

            n.setEstadoCompleto(estado);
            n.setIconoEstado(icono);
            n.setColor(color);

            holder.ivEstado.setImageResource(n.getIconoEstado());
            holder.tvEstado.setText(n.getEstadoCompleto());
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, n.getColor()));
        }


        holder.ivEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment dialogFragment = null;
                String tag = "";

                // Almacenar todos los datos en un Bundle que se enviará al DialogFragment
                Bundle bundleNotif = new Bundle();
                bundleNotif.putString("dni_estudiante", n.getDniEstudiante());
                bundleNotif.putInt("id_notificacion", n.getIdNofiticacion());
                bundleNotif.putString("titulo", n.getTitulo());
                bundleNotif.putString("descripcion", n.getDescripcion());
                bundleNotif.putString("f_envio", n.getFechaEnvio());
                bundleNotif.putString("tipo", n.getTipo());

                // Diferenciar entre tipos de notificaciones
                if (n.getTipo().equals("comunicado")) {
                    dialogFragment = new ComunicadoFragment();
                    tag = "ComunicadoFragment";
                } else {
                    bundleNotif.putString("f_limite", n.getFechaLimite());
                    bundleNotif.putChar("estado", n.getEstado());
                    bundleNotif.putInt("color", n.getColor());
                    bundleNotif.putString("estado_completo", n.getEstadoCompleto());
                    bundleNotif.putInt("ic_estado", n.getIconoEstado());
                    if (n.getTipo().equals("citacion")) {
                        dialogFragment = new ConfirmarCitacionFragment();
                        tag = "ConfirmarCitacionFragment";
                    } else {
                        dialogFragment = new AprobarPermisoFragment();
                        tag = "AprobarPermisoFragment";
                    }
                }

                if (dialogFragment != null) {
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    dialogFragment.setArguments(bundleNotif);
                    dialogFragment.show(fragmentManager, tag);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    public void agregarNotificaciones(ArrayList<Notificacion> lista) {
        notificaciones.clear();
        notificaciones.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFecha, tvEstado;
        ImageView ivEstado, ivEntrar;
        ConstraintLayout clFondo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tv_n_titulo);
            tvDescripcion = itemView.findViewById(R.id.tv_n_descripcion);
            tvFecha = itemView.findViewById(R.id.tv_n_fecha);
            tvEstado = itemView.findViewById(R.id.tv_n_estado);
            ivEstado = itemView.findViewById(R.id.iv_n_estado);
            ivEntrar = itemView.findViewById(R.id.iv_n_entrar);
            clFondo = itemView.findViewById(R.id.cl_n_tarjeta);
        }
    }
}
