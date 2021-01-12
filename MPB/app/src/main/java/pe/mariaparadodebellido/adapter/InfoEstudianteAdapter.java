package pe.mariaparadodebellido.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.mariaparadodebellido.Login;
import pe.mariaparadodebellido.MenuApoderado;
import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.fragments.ConsultarNotasFragment;
import pe.mariaparadodebellido.model.Estudiante;

import static android.content.Context.MODE_PRIVATE;

public class InfoEstudianteAdapter extends RecyclerView.Adapter<InfoEstudianteAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Estudiante> listaInfoEstudiantes;

    public InfoEstudianteAdapter(Context context) {
        this.context = context;
        listaInfoEstudiantes = new ArrayList<>();
    }

    @NonNull
    @Override
    public InfoEstudianteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_info_estudiante,parent,false);
        return new InfoEstudianteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoEstudianteAdapter.ViewHolder holder, int position) {
        Estudiante estudiante = listaInfoEstudiantes.get(position);

        holder.tvNombres.setText(estudiante.getNombre());
        holder.tvApellidos.setText(estudiante.getApellido());

        holder.btnConsultarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirige al otro fragmente a traves de un "action", este action está en res/navegation/mobile_navegation.xml
                Navigation.findNavController(view).navigate(R.id.action_nav_estudiantes_to_nav_consultar_notas);
                agregarDni(estudiante.getDniEstudiante());
            }
        });

        holder.btnCnsultarAsistencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_estudiantes_to_nav_consultar_asistencias);
                agregarDni(estudiante.getDniEstudiante());
            }
        });

        holder.btnConsultarHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Pendiente", Toast.LENGTH_SHORT).show();
                //Navigation.findNavController(view).navigate(R.id.);
                agregarDni(estudiante.getDniEstudiante());
            }
        });

        holder.btnListarDocentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Listar docentes (Sprint 4)", Toast.LENGTH_SHORT).show();
                //Navigation.findNavController(view).navigate(R.id.);
                agregarDni(estudiante.getDniEstudiante());
            }
        });

        holder.btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Pendiente", Toast.LENGTH_SHORT).show();
                //Navigation.findNavController(view).navigate(R.id.);
                agregarDni(estudiante.getDniEstudiante());
            }
        });
    }

    // Almacenar el dni en otra parte de la sesión
    private void agregarDni(String dni) {
        SharedPreferences.Editor editor = context.getSharedPreferences("info_usuario", MODE_PRIVATE).edit();
        editor.putString("dniEstudiante", dni);
        editor.apply();
    }


    @Override
    public int getItemCount() {
        return listaInfoEstudiantes.size();
    }

    public void agregarInfoEstudiantes(ArrayList<Estudiante> lista){
        listaInfoEstudiantes.clear();
        listaInfoEstudiantes.addAll(lista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombres, tvApellidos;
        Button btnConsultarNotas, btnCnsultarAsistencias, btnConsultarHorario,
            btnListarDocentes, btnPerfil;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombres = itemView.findViewById(R.id.tv_info_nombres);
            tvApellidos = itemView.findViewById(R.id.tv_info_apellidos);
            btnConsultarNotas = itemView.findViewById(R.id.btn_consultar_notas);
            btnCnsultarAsistencias = itemView.findViewById(R.id.btn_consultar_asistencias);
            btnConsultarHorario = itemView.findViewById(R.id.btn_consultar_horario);
            btnListarDocentes = itemView.findViewById(R.id.btn_listar_docentes);
            btnPerfil = itemView.findViewById(R.id.btn_perfil);
        }
    }
}
