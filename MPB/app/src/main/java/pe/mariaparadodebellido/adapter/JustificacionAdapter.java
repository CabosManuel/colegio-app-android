package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.mariaparadodebellido.ListarJustificacion;
import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Justificacion;

public class JustificacionAdapter extends RecyclerView.Adapter<JustificacionAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Justificacion> listaListarJustificacion;

    public JustificacionAdapter(Context context) {
        this.context = context;
        listaListarJustificacion = new ArrayList<>();
    }

    @NonNull
    @Override
    public JustificacionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_justificacion,parent,false);
        return new JustificacionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JustificacionAdapter.ViewHolder holder, int position) {
        final Justificacion listarJustificacion = listaListarJustificacion.get(position);

        holder.id_descripcion.setText(listarJustificacion.getDescripcion());
        holder.id_titulo.setText(listarJustificacion.getTitulo());
        holder.id_fecha.setText(listarJustificacion.getFecha());
    }

    @Override
    public int getItemCount() {
        return listaListarJustificacion.size();
    }

    public void agregarJustificacion(ArrayList<Justificacion> lista){
        listaListarJustificacion.clear();
        listaListarJustificacion.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id_titulo,id_fecha,id_descripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id_titulo = itemView.findViewById(R.id.Id_titulo);
            id_fecha = itemView.findViewById(R.id.id_fecha);
            id_descripcion = itemView.findViewById(R.id.id_descripcion);
        }
    }
}


