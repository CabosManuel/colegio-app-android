package pe.mariaparadodebellido;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import pe.mariaparadodebellido.model.Estudiante;

public class MenuEstudiante extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView tvNombreApellidos, tvDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_estudiante);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View menuNavView = navigationView.getHeaderView(0);

        tvNombreApellidos = menuNavView.findViewById(R.id.tv_menu_nom_ape);
        tvDni = menuNavView.findViewById(R.id.tv_menu_dni);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio
                ,R.id.nav_perfil_estudiante
                ,R.id.nav_consultar_asistencias
                ,R.id.nav_consultar_notas
                ,R.id.nav_horario
                ,R.id.nav_docentes).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Recibir datos del esutiante que está en sesión
        SharedPreferences preferences = getSharedPreferences("info_usuario",MODE_PRIVATE);
        Estudiante estudiante = new Estudiante();
        try {
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "cliente no existe"));
            estudiante.setNombre(eJson.getString("nombre"));
            estudiante.setApellido(eJson.getString("apellido"));
            estudiante.setDniEstudiante(eJson.getString("dniEstudiante"));

            tvNombreApellidos.setText(estudiante.getNombreApellido());
            tvDni.setText(estudiante.getDniEstudiante());
        } catch (JSONException e) {
            Toast.makeText(this, "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}