package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.content.Intent;
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

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Curso> listaCursos;

    public CursoAdapter(Context context) {
        this.context = context;
        listaCursos = new ArrayList<>();
    }

    @NonNull
    @Override
    public CursoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false);
        return new CursoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoAdapter.ViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);
        holder.tvCurso.setText(curso.getNombre());

        colocarIcono(curso.getNombre(), holder.ivIcono);

        // Cuando toque en una tarjeta:
        holder.cardCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent con putExtras que se leerán en ConsultarAsistenciasActivity
                Intent asistencias = new Intent(context, ConsultarAsistenciasActivity.class);
                asistencias.putExtra("curso_id", curso.getIdCurso());
                asistencias.putExtra("nombre", curso.getNombre());
                asistencias.putExtra("icono", (Integer) holder.ivIcono.getTag());

                context.startActivity(asistencias);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    public void agregarCurso(ArrayList<Curso> lista) {
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

    private void colocarIcono(String curso, ImageView icono) {
        // switch con todos los cursos, desde PRIMARIA a SECUNDARIA
        switch (curso) {
            case "ARTE":
                icono.setImageResource(R.drawable.ic_curso_arte);
                icono.setTag(R.drawable.ic_curso_arte);
                break;
            case "ARTE Y CULTURA":
                icono.setImageResource(R.drawable.ic_curso_arte);
                icono.setTag(R.drawable.ic_curso_arte);
                break;
            case "CIENCIA TECNOLOGÍA Y AMBIENTE":
                icono.setImageResource(R.drawable.ic_curso_ciencia);
                icono.setTag(R.drawable.ic_curso_ciencia);
                break;
            case "CIENCIA Y TECNOLOGÍA":
                icono.setImageResource(R.drawable.ic_curso_ciencia);
                icono.setTag(R.drawable.ic_curso_ciencia);
                break;
            case "COMPUTACIÓN":
                icono.setImageResource(R.drawable.ic_curso_computacion);
                icono.setTag(R.drawable.ic_curso_computacion);
                break;
            case "COMUNICACIÓN":
                icono.setImageResource(R.drawable.ic_curso_comunicacion);
                icono.setTag(R.drawable.ic_curso_comunicacion);
                break;
            case "DESCUBRIMIENTO DEL MUNDO":
                icono.setImageResource(R.drawable.ic_curso_descubrimiento);
                icono.setTag(R.drawable.ic_curso_descubrimiento);
                break;
            case "EDUCACIÓN FÍSICA":
                icono.setImageResource(R.drawable.ic_curso_efisica);
                icono.setTag(R.drawable.ic_curso_efisica);
                break;
            case "EDUCACIÓN PARA EL TRABAJO":
                icono.setImageResource(R.drawable.ic_curso_ept);
                icono.setTag(R.drawable.ic_curso_ept);
                break;
            case "FORMACIÓN CIUDADANA":
                icono.setImageResource(R.drawable.ic_curso_fc);
                icono.setTag(R.drawable.ic_curso_fc);
                break;
            case "HISTORIA":
                icono.setImageResource(R.drawable.ic_curso_historia);
                icono.setTag(R.drawable.ic_curso_historia);
                break;
            case "INGLÉS":
                icono.setImageResource(R.drawable.ic_curso_ingles);
                icono.setTag(R.drawable.ic_curso_ingles);
                break;
            case "MATEMÁTICA":
                icono.setImageResource(R.drawable.ic_curso_matematica);
                icono.setTag(R.drawable.ic_curso_matematica);
                break;
            case "PERSONAL SOCIAL":
                icono.setImageResource(R.drawable.ic_curso_fc);
                icono.setTag(R.drawable.ic_curso_fc);
                break;
            case "PSICOMOTRIZ":
                icono.setImageResource(R.drawable.ic_curso_psicomotriz);
                icono.setTag(R.drawable.ic_curso_psicomotriz);
                break;
            case "RELACIONES HUMANAS":
                icono.setImageResource(R.drawable.ic_curso_rh);
                icono.setTag(R.drawable.ic_curso_rh);
                break;
            case "RELIGIÓN":
                icono.setImageResource(R.drawable.ic_curso_religion);
                icono.setTag(R.drawable.ic_curso_religion);
                break;
            default:
                icono.setImageResource(R.drawable.ic_curso_default);
                break;
        }
    }
}
