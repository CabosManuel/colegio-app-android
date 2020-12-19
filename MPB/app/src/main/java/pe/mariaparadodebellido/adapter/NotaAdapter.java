package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Nota;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Nota> listaNotas;

    public NotaAdapter(Context context) {
        this.context = context;
        listaNotas = new ArrayList<>();
    }

    @NonNull
    @Override
    public NotaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nota,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaAdapter.ViewHolder holder, int position) {
        final Nota objServicio = listaNotas.get(position);

        Integer n1 = objServicio.getNota1(),
                n2 = objServicio.getNota2(),
                n3 = objServicio.getNota3(),
                p = Math.round((n1+n2+n3)/3);

        holder.tvCurso.setText(objServicio.getCurso());
        holder.tvN1.setText(n1.toString());
        holder.tvN2.setText(n2.toString());
        holder.tvN3.setText(n3.toString());
        holder.tvP.setText(p.toString());
    }

    public void agregarNota(ArrayList<Nota> lista){
        listaNotas.clear();
        listaNotas.addAll(lista);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCurso,tvN1,tvN2,tvN3,tvP;
        CardView cardNota;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurso = itemView.findViewById(R.id.tv_cruso);
            tvN1 = itemView.findViewById(R.id.tv_nota1);
            tvN2 = itemView.findViewById(R.id.tv_nota2);
            tvN3 = itemView.findViewById(R.id.tv_nota3);
            tvP = itemView.findViewById(R.id.tv_promedio);
            cardNota = itemView.findViewById(R.id.card_nota);
        }
    }
}
