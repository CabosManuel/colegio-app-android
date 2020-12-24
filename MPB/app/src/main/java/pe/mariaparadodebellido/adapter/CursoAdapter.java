package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.mariaparadodebellido.ConsultarAsistenciasActivity;
import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Curso;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Curso> listaCursos;

    public CursoAdapter(Context context) {
        this.context = context;
        listaCursos = new ArrayList<>();
    }

    @NonNull
    @Override
    public CursoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso,parent,false);
        return new CursoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoAdapter.ViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);

        holder.tvCurso.setText(curso.getNombre());

        switch (curso.getNombre()) {
            case "ARTE":
                break;
            case "ARTE Y CULTURA":
                break;
            case "CIENCIA TECNOLOGÍA Y AMBIENTE":
                break;
            case "CIENCIA Y TECNOLOGÍA":
                break;
            case "COMPUTACIÓN":
                break;
            case "COMUNICACIÓN":
                break;
            case "DESCUBRIMIENTO DEL MUNDO":
                break;
            case "EDUCACIÓN FÍSICA":
                break;
            case "EDUCACIÓN PARA EL TRABAJO":
                break;
            case "FORMACIÓN CIUDADANA":
                break;
            case "HISTORIA":
                break;
            case "INGLÉS":
                break;
            case "MATEMÁTICA":
                holder.ivIcono.setImageResource(R.drawable.ic_curso_matematica);
                break;
            case "PERSONAL SOCIAL":
                break;
            case "PSICOMOTRIZ":
                break;
            case "RELACIONES HUMANAS":
                break;
            default:
                holder.ivIcono.setImageResource(R.drawable.ic_curso_default);
                break;
        }


            holder.cardCurso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent asistencias = new Intent(context, ConsultarAsistenciasActivity.class);
                    asistencias.putExtra("curso_id", curso.getIdCurso());
                    asistencias.putExtra("descripcion", curso.getNombre());
                    asistencias.putExtra("icono", (Parcelable) holder.ivIcono.getDrawable());

                    context.startActivity(asistencias);
                }
            }
        );
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public void agregarCurso(ArrayList<Curso> lista){
        listaCursos.clear();
        listaCursos.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurso;
        ImageView ivIcono;
        CardView cardCurso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurso = itemView.findViewById(R.id.tv_curso);
            ivIcono = itemView.findViewById(R.id.iv_icono);
            cardCurso = itemView.findViewById(R.id.card_curso);
        }
    }
}
