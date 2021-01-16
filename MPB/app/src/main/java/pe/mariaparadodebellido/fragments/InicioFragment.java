package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pe.mariaparadodebellido.R;

public class InicioFragment extends Fragment {

    TextView tvNombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_inicio, container, false);

        tvNombre = fragmentView.findViewById(R.id.tv_ini_nombre);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "usuario no existe"));
            String nombre = "Bienvenida (o) " + eJson.getString("nombre") + " " + eJson.getString("apellido");
            tvNombre.setText(nombre);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        return fragmentView;
    }
}