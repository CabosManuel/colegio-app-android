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
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.HorarioDetalle;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HorarioDetalle> horario;

    public HorarioAdapter(Context context) {
        this.context = context;
        horario = new ArrayList<>();
    }

    @NonNull
    @Override
    public HorarioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HorarioAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_horario, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HorarioAdapter.ViewHolder holder, int position) {
        HorarioDetalle detalle = horario.get(position);

        // cuando no sea lunes y el día sea diferente al anterior
        if (position > 0 && detalle.getDia().equals(horario.get(position - 1).getDia())) {
            holder.tvDia.setVisibility(View.INVISIBLE);
            holder.tvFecha.setVisibility(View.INVISIBLE);
        } else {
            holder.tvDia.setText(detalle.getDia());
            holder.tvFecha.setText(DateTimeFormatter.ofPattern("dd MMM").format(calcularFecha(detalle.getDia())));
        }

        holder.tvCurso.setText(detalle.getNombreCurso());

        String hInicio = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(detalle.gethInicio()));
        String hFin = DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(detalle.gethFin()));
        holder.tvHora.setText(hInicio + " - " + hFin);
    }

    @Override
    public int getItemCount() {
        return horario.size();
    }

    public void agregarHorario(ArrayList<HorarioDetalle> horario) {
        this.horario.clear();
        this.horario.addAll(horario);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvFecha, tvCurso, tvHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tv_h_dia);
            tvFecha = itemView.findViewById(R.id.tv_h_fecha);
            tvCurso = itemView.findViewById(R.id.tv_h_curso);
            tvHora = itemView.findViewById(R.id.tv_h_hora);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private TemporalAccessor calcularFecha(String dia) {
        // siempre calcular cuando que fecha a partir de hoy es lunes
        LocalDate lunes = null;
        for (int i = 0; LocalDate.now().getDayOfWeek().getValue() - i > 0 ; i++) {
            lunes = LocalDate.now().minusDays(i);
        }

        // switch para aumentar días al lunes calculado
        int adelanto = 0;
        switch (dia) {
            case "MARTES":
                adelanto = 1;
                break;
            case "MIÉRCOLES":
                adelanto = 2;
                break;
            case "JUEVES":
                adelanto = 3;
                break;
            case "VIERNES":
                adelanto = 4;
                break;
        }

        return lunes.plusDays(adelanto);
    }
}
