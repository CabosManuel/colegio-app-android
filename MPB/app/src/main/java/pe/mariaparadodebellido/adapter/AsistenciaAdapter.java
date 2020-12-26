package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Asistencia;
import pe.mariaparadodebellido.model.Curso;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Asistencia> asistencias;

    public AsistenciaAdapter(Context context) {
        this.context = context;
        asistencias = new ArrayList<>();
    }

    @NonNull
    @Override
    public AsistenciaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_asistencia,parent,false);
        return new ViewHolder(view);
    }

    // Anotación para usar el LocalDate
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AsistenciaAdapter.ViewHolder holder, int position) {
        Asistencia a = asistencias.get(position);

        // Coloreado al ConstraintLayout y A: asistencias o F: falta
        if(a.getEstado()){
            holder.tvEstado.setText("A");
            holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.verde_asistencia));
        }else{
            holder.tvEstado.setText("F");
            holder.clFondo.setBackgroundColor(ContextCompat.getColor(context, R.color.rojo_falta));
        }

        // Fecha en el formato "25/12"
        LocalDate fechaSQL = LocalDate.parse(a.getFecha());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM");
        holder.tvFecha.setText(fechaSQL.format(f));
    }

    @Override
    public int getItemCount() {
        return asistencias.size();
    }

    public void agregarAsistencia(ArrayList<Asistencia> lista){
        asistencias.clear();
        asistencias.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEstado,tvFecha;
        ConstraintLayout clFondo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            clFondo = itemView.findViewById(R.id.cl_fondo);
        }
    }
}
