package pe.mariaparadodebellido;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;


public class PerfilApoderado extends AppCompatActivity {

    private TextInputEditText tNombres, tApellidos, tDNI,
            tDistrito, tDireccion, tCelular, tCorreo;

    private Button btnEditar, btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_apoderado);

        }
    }
