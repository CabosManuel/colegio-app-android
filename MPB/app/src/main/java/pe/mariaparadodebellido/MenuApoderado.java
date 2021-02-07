package pe.mariaparadodebellido;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import pe.mariaparadodebellido.model.Apoderado;

public class MenuApoderado extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView tvNombreApellidos, tvDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_apoderado);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_apoderado);
        NavigationView navigationView = findViewById(R.id.nav_view_apoderado);
        View menuNavView = navigationView.getHeaderView(0);

        tvNombreApellidos = menuNavView.findViewById(R.id.tv_menu_nom_ape);
        tvDni = menuNavView.findViewById(R.id.tv_menu_dni);

        // Para asigar acceso directo al menú (el ícono de 3 rallas ≡ en ves de una flecha ←)
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio
                ,R.id.nav_perfil_apoderado
                ,R.id.nav_estudiantes
                ,R.id.nav_bandeja_entrada
                ,R.id.nav_registrar_justificacion
                ,R.id.nav_listar_justificaciones
                ).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Recibir datos del esutiante que está en sesión
        SharedPreferences preferences = getSharedPreferences("info_usuario", MODE_PRIVATE);
        Apoderado apoderado = new Apoderado();
        try {
            JSONObject eJson = new JSONObject(preferences.getString("usuario", ""));
            apoderado.setNombre(eJson.getString("nombre"));
            apoderado.setApellido(eJson.getString("apellido"));
            apoderado.setDniApoderado(eJson.getString("dniApoderado"));

            tvNombreApellidos.setText(apoderado.getNombreApellido());
            tvDni.setText(apoderado.getDniApoderado());
        } catch (JSONException e) {
            Toast.makeText(this, "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
            System.err.println("ERROR: "+ e.getMessage());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
