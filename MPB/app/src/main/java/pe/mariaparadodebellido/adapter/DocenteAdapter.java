package pe.mariaparadodebellido.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import pe.mariaparadodebellido.model.Docente;
import pe.mariaparadodebellido.model.Justificacion;

public class DocenteAdapter extends RecyclerView.Adapter<DocenteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Docente> listaDocente;

    public DocenteAdapter(Context context) {
        this.context = context;
        listaDocente = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return listaDocente.size();
    }

    @NonNull
    @Override
    public DocenteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_docente, parent, false);
        return new DocenteAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DocenteAdapter.ViewHolder holder, int position) {
        Docente docente = listaDocente.get(position);

        // Descripcion pasar a string
        holder.Nombres.setText(docente.getNombres());
        holder.Apellidos.setText(docente.getApellido());
        holder.Celular.setText(docente.getCelular());
        holder.Correo.setText(docente.getCorreo());
        holder.tvCurso.setText(docente.getCurso());

        cambiarDiseñoTarjeta(docente.getSexo(), holder.ivDocente, holder.clFondo);
    }

    // Método para cambiar diseño del cardView dependiendo del sexo del docente
    private void cambiarDiseñoTarjeta(String sexo, ImageView ivIcono, ConstraintLayout clFondo) {
        int color = 0;
        int iconoId = 0;

        // Condicional para diferenciar por sexo
        if (sexo != null || sexo.equals("")) {
            if (sexo.equals("F")) { // Femenino
                color = R.color.chip_amarillo;
                iconoId = R.drawable.profesora;
            } else if (sexo.equals("M")) { // Masculino
                color = R.color.azul_claro;
                iconoId = R.drawable.profesor;
            }
        } else {
            Toast.makeText(context, "sexo null", Toast.LENGTH_SHORT).show();
        }

        clFondo.setBackgroundColor(ContextCompat.getColor(context, color));
        ivIcono.setImageResource(iconoId);
    }

    public void agregarDocente(ArrayList<Docente> lista) {
        listaDocente.clear();
        listaDocente.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Nombres, Apellidos, Celular, Correo, tvCurso;
        ConstraintLayout clFondo;
        ImageView ivDocente;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clFondo = itemView.findViewById(R.id.cl_fondo_docente);
            ivDocente = itemView.findViewById(R.id.iv_iconodoc);
            Nombres = itemView.findViewById(R.id.txtNombres);
            Apellidos = itemView.findViewById(R.id.txtApellidos);
            Celular = itemView.findViewById(R.id.txtCelular);
            Correo = itemView.findViewById(R.id.txtCorreo);
            tvCurso = itemView.findViewById(R.id.txtCurso);
        }
    }
}

