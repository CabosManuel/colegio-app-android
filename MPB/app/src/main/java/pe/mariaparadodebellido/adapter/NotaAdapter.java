package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Nota;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Nota> listaNotas;

    public NotaAdapter(Context context) {
        this.context = context;
        listaNotas = new ArrayList<>();
    }

    @NonNull
    @Override
    public NotaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nota, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaAdapter.ViewHolder holder, int position) {
        final Nota nota = listaNotas.get(position);

        Integer promedio = (int) Math.ceil((nota.getNota1() + nota.getNota2() + nota.getNota3()) / 3);

        holder.tvCurso.setText(nota.getCurso());

        TextView[] tvs = {holder.tvN1, holder.tvN2, holder.tvN3};
        Double[] notas = {nota.getNota1(), nota.getNota2(), nota.getNota3()};
        coloerarNotas(tvs, notas);

        holder.tvP.setText(promedio.toString());
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public void agregarNota(ArrayList<Nota> lista) {
        listaNotas.clear();
        listaNotas.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurso, tvN1, tvN2, tvN3, tvP;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurso = itemView.findViewById(R.id.tv_curso);
            tvN1 = itemView.findViewById(R.id.tv_nota1);
            tvN2 = itemView.findViewById(R.id.tv_nota2);
            tvN3 = itemView.findViewById(R.id.tv_nota3);
            tvP = itemView.findViewById(R.id.tv_promedio);
        }
    }

    // Métodos para colorear si es nota aprobatoria o no
    private void coloerarNotas(TextView[] tvs, Double[] notas) {
        for (int i = 0; i < tvs.length; i++) {
            tvs[i].setText(String.valueOf(Math.round(notas[i])));
            if (notas[i] > 13)
                tvs[i].setTextColor(ContextCompat.getColor(context, R.color.nota_aprobada));
            else
                tvs[i].setTextColor(ContextCompat.getColor(context, R.color.nota_desaprobada));
        }
    }
}
