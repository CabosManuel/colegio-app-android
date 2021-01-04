package pe.mariaparadodebellido;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
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

public class MenuEstudiante extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView tvNombreApellidos, tvCerrarSesion;

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
        tvCerrarSesion = menuNavView.findViewById(R.id.tv_menu_cerrar_sesion);
        tvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("info_usuario",MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(MenuEstudiante.this, Login.class));
                finish();
            }
        });

        //navigationView.setNavigationItemSelectedListener(this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio,
                R.id.nav_asistencia
                ,R.id.nav_horario
                ,R.id.nav_notas
                ,R.id.nav_perfil
                //,R.id.nav_cerrar
                ,R.id.nav_docentes)

                .setDrawerLayout(drawer).build();
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
            tvNombreApellidos.setText(estudiante.getNombreApellido());
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

    /*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_asistencia:
                startActivity(new Intent(MenuEstudiante.this, ConsultarAsistenciasCusosActivity.class));
                break;
            default:
                Toast.makeText(this, "Item no mapeado.", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

     */
}