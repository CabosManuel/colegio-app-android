package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pe.mariaparadodebellido.Login;

public class CerrarSesionFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(this.getActivity(), Login.class));
        this.getActivity().finish();
    }
}