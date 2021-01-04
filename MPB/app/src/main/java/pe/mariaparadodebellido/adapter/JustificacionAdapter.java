package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Justificacion;

public class JustificacionAdapter extends  RecyclerView.Adapter<JustificacionAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Justificacion> listaJustificaciones;

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
        
        // Formato de fecha
        String fecha = DateTimeFormatter.ofPattern("dd/MM").format(LocalDate.parse(justificacion.getFechaEnvio().substring(0, 10)));
        String hora = DateTimeFormatter.ofPattern("h:m a").format(LocalTime.parse(justificacion.getFechaEnvio().substring(11)));
        String fechaEnvio = hora + " " + fecha;
        holder.etFecha.setText(fechaEnvio);
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etTitulo = itemView.findViewById(R.id.Id_titulo);
            etFecha = itemView.findViewById(R.id.id_fecha);
            etDescripcion = itemView.findViewById(R.id.id_descripcion);
        }
    }
}


