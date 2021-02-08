package pe.mariaparadodebellido.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Distrito;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.util.Url;

import static android.content.Context.MODE_PRIVATE;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PerfilEstudianteFragment extends Fragment implements View.OnClickListener {

    private String dniEstudiante = "";
    private String estudianteTag = "";

    // Almacenará en formato JSON los datos en sesión
    private JSONObject estudianteJson;

    private TextInputEditText etNombres, etApellidos, etApoderado, etDNI, etFNacimiento,
            etDireccion, etCelular, etCorreo;
    private Button btnEditar, btnGuardar;

    private Spinner spDistritos;
    private ArrayList<Distrito> distritos = new ArrayList<>();
    private RequestQueue colaPeticiones;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewFragment = inflater.inflate(R.layout.fragment_perfil_estudiante, container, false);

        spDistritos = viewFragment.findViewById(R.id.sp_e_distrito);
        spDistritos.setEnabled(false);

        btnEditar = viewFragment.findViewById(R.id.btn_e_editar);
        btnGuardar = viewFragment.findViewById(R.id.btn_e_guardar);
        etNombres = viewFragment.findViewById(R.id.et_e_nombres);
        etApellidos = viewFragment.findViewById(R.id.et_e_apellidos);
        etApoderado = viewFragment.findViewById(R.id.et_e_apoderado);
        etDNI = viewFragment.findViewById(R.id.et_e_dni);
        etFNacimiento = viewFragment.findViewById(R.id.et_e_fnacimiento);
        etDireccion = viewFragment.findViewById(R.id.et_e_direccion);
        etCelular = viewFragment.findViewById(R.id.et_e_celular);
        etCorreo = viewFragment.findViewById(R.id.et_e_correo);

        btnEditar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        etFNacimiento.setOnClickListener(this);

        capturarDatosEnSesion();

        return viewFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_e_editar:
                cambiarEstados(true);
                break;
            case R.id.btn_e_guardar:
                if (validar()) {
                    cambiarEstados(false);
                    actualizarEstudiante();
                }
                break;
            case R.id.et_e_fnacimiento:
                mostrarCalendario();
                break;
        }
    }

    // Método para actualizar estudiante en la BD
    private void actualizarEstudiante() {
        colaPeticiones = Volley.newRequestQueue(getContext());
        String url = Url.URL_BASE + "/idat/rest/estudiante/editar_perfil/" + dniEstudiante;
        JSONObject parametroJson = new JSONObject(capturarDatosActualizados());
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.PUT, url,
                parametroJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject datosRespuesta) {
                        try {
                            // Actualizar datos en sesión
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE).edit();
                            editor.putString(estudianteTag, datosRespuesta.toString());
                            editor.apply();

                            Toast.makeText(getContext(), "Se actualizó la información correctamente.", Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "Error al actualizar datos.", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );
        colaPeticiones.add(peticion);
    }

    // Método para cargar en los EditText y spinner los datos de la sesión actual
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void capturarDatosEnSesion() {
        // Obtener usuario en sessión
        SharedPreferences preferences = getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
        try {
            switch (preferences.getString("tipo", "")) {
                // Cuando un estudiante esté logueado
                case "estudiante":
                    estudianteTag = "usuario";
                    estudianteJson = new JSONObject(preferences.getString(estudianteTag, ""));
                    // Se carga esa información a la vista
                    cargarEnCampos();
                    break;
                case "apoderado": // Cuando un apoderado esté logueado
                    estudianteTag = "estudiante";
                    buscarGuardarEstudiante(preferences.getString("dniEstudiante", ""));
                    break;
            }

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar datos en sesión.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("error preference", e.getMessage());
        }
    }

    // Método para cargar datos a los campos de la vista (EditText y spinner)
    private void cargarEnCampos() {
        try {
            dniEstudiante = estudianteJson.getString("dniEstudiante");
            etDNI.setText(dniEstudiante);
            etNombres.setText(estudianteJson.getString("nombre"));
            etApellidos.setText(estudianteJson.getString("apellido"));
            etApoderado.setText(estudianteJson.getString("apoderado"));
            etDireccion.setText(estudianteJson.getString("direccion"));
            etCorreo.setText(estudianteJson.getString("correo"));
            etCelular.setText(estudianteJson.getString("celular"));
            etFNacimiento.setText(DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .format(LocalDate.parse(estudianteJson.getString("fNacimiento"))));

            getDistritos();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Metodo para obtener datos del estudiante y guardarlos en otra parte de la sesión (cuando sea apoderado)
    private void buscarGuardarEstudiante(String dniEstudiante) {
        colaPeticiones = Volley.newRequestQueue(getContext());
        String url = Url.URL_BASE + "/idat/rest/estudiante/buscar_estudiante/" + dniEstudiante;
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Editar sesión y agregar el nuevo put/tag a la sesión
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("info_usuario", MODE_PRIVATE).edit();
                        editor.putString(estudianteTag, response.toString());
                        editor.apply();

                        try { // Almacenar inforamción de ese tag y cargarlos a la vista
                            SharedPreferences preferences = getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
                            estudianteJson = new JSONObject(preferences.getString(estudianteTag, ""));
                            cargarEnCampos();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        colaPeticiones.add(peticion);
    }

    // Método para capturar nuevos datos ingresados
    private Map<String, Object> capturarDatosActualizados() {
        Map<String, Object> nuevoEstudianteJson;
        nuevoEstudianteJson = new HashMap<>();
        nuevoEstudianteJson.put("dni_estudiante", dniEstudiante);
        nuevoEstudianteJson.put("nombre", etNombres.getText().toString());
        nuevoEstudianteJson.put("apellido", etApellidos.getText().toString());
        nuevoEstudianteJson.put("celular", etCelular.getText().toString());
        nuevoEstudianteJson.put("correo", etCorreo.getText().toString());
        nuevoEstudianteJson.put("direccion", etDireccion.getText().toString());
        nuevoEstudianteJson.put("apoderado", etApoderado.getText().toString());
        nuevoEstudianteJson.put("distrito_id", spDistritos.getSelectedItemId() + 1);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fNacimiento = DateTimeFormatter.ISO_LOCAL_DATE.format(
                LocalDate.parse(etFNacimiento.getText(), formato));
        nuevoEstudianteJson.put("fnacimiento", fNacimiento);

        return nuevoEstudianteJson;
    }

    // Método para funcionalidad del Calendario de "etFnacimiento"
    private void mostrarCalendario() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(etFNacimiento.getText(), formato);

        int anio = fecha.getYear();
        int mes = fecha.getMonthValue() - 1;
        int dia = fecha.getDayOfMonth();

        DatePickerDialog dtpCalendario = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {
                mesSeleccionado++;
                // Esta parte esporque el mes y día vienen sin el 0 a la izquierda
                String diaConcatenado = String.valueOf(diaSeleccionado), mesConcatenado = String.valueOf(mesSeleccionado);
                if (diaSeleccionado < 10) diaConcatenado = "0" + diaSeleccionado;
                if (mesSeleccionado < 10) mesConcatenado = "0" + mesSeleccionado;

                System.out.println("3: " + diaConcatenado + "/" + mesConcatenado + "/" + anioSeleccionado);
                etFNacimiento.setText(diaConcatenado + "/" + mesConcatenado + "/" + anioSeleccionado);
            }
        }, anio, mes, dia);

        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR) - Estudiante.EDAD_MIN, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dtpCalendario.getDatePicker().setMaxDate(c.getTimeInMillis());

        c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR) - Estudiante.EDAD_MAX, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dtpCalendario.getDatePicker().setMinDate(c.getTimeInMillis());

        dtpCalendario.show();
    }

    // Método para habilitar y deshabilitar edición
    private void cambiarEstados(boolean estado) {
        if (estado) {
            btnEditar.setVisibility(View.INVISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
        } else {
            btnEditar.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.INVISIBLE);
        }

        etNombres.setEnabled(estado);
        etApellidos.setEnabled(estado);
        etFNacimiento.setEnabled(estado);
        etDireccion.setEnabled(estado);
        etCelular.setEnabled(estado);
        etCorreo.setEnabled(estado);
        spDistritos.setEnabled(estado);
    }

    // Método para validar campos
    private boolean validar() {
        boolean valido = true;
        String nombres = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String direccion = etDireccion.getText().toString();
        String correo = etCorreo.getText().toString();
        String celular = etCelular.getText().toString();

        if (nombres.isEmpty()) {
            etNombres.setError("El campo es obligatorio.");
            valido = false;
        }
        if (apellidos.isEmpty()) {
            etApellidos.setError("El campo es obligatorio.");
            valido = false;
        }
        if (direccion.isEmpty()) {
            etDireccion.setError("El campo es obligatorio.");
            valido = false;
        }
        if (celular.isEmpty()) {
            etCelular.setError("El campo es obligatorio.");
            valido = false;
        }
        if (celular.length() < 9) {
            etCelular.setError("Número celular no valido.");
            valido = false;
        }
        if (correo.isEmpty()) {
            etCorreo.setError("El campo es obligatorio.");
            valido = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Ingrese un correo valido.");
            valido = false;
        }

        return valido;
    }

    // Método para solicitar distritos de la BD
    private void getDistritos() {
        String url = Url.URL_BASE + "/idat/rest/distritos/listar_distritos";
        colaPeticiones = Volley.newRequestQueue(getContext());
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                distritos.add(new Distrito(
                                        objjson.getInt("distrito_id"),
                                        objjson.getString("nombre")
                                ));
                            }
                            cargarSeleccionarDistrito();
                        } catch (JSONException ex) {
                            Toast.makeText(getContext(), "Error al cargar distritos.", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        colaPeticiones.add(peticion);
    }

    // Método para llenar spinner + capturar y seleccionar en el spinner el distrito de la sesión
    private void cargarSeleccionarDistrito() {
        // Llenar spinner
        spDistritos.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, distritos));

        // Seleccionar en el spinner
        try {
            spDistritos.setSelection(estudianteJson.getInt("distritoId") - 1);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al establecer distrito.", Toast.LENGTH_SHORT).show();
        }
    }
}