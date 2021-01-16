package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import pe.mariaparadodebellido.R;
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

        // switch para color de fondo segun tipo notificaciones
        switch (n.getTipo()) {
            case "comunicado":
                // Los comunicados no tienen estado, por eso se ocultan estos elementos:
                holder.tvEstado.setVisibility(View.INVISIBLE);
                holder.ivEstado.setVisibility(View.INVISIBLE);
                holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_amarillo));
                break;
            case "citacion":
                holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_verde));
                break;
            case "permiso":
                holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_celeste));
                break;
        }

        // Cuando la notificación tenga "estado" (permiso y citación)
        if (n.getEstado() != null) {
            String estado = "";
            int color = 0;
            int icono = 0;

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

            holder.ivEstado.setImageResource(icono);
            holder.tvEstado.setText(estado);
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, color));
        }
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
