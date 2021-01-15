package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Distrito;
import pe.mariaparadodebellido.util.Url;

public class PerfilApoderadoFragment extends Fragment implements View.OnClickListener {

    private String dniApoderado = "";

    // Almacenará en formato JSON los datos en sesión
    private JSONObject apoderadoJson;

    private TextView tvNEstudiantes, tvEstudiantes;
    private TextInputEditText etNombres, etApellidos, etDNI,
            etDireccion, etCelular, etCorreo;
    private Button btnEditar, btnGuardar;

    private Spinner spDistritos;
    private ArrayList<Distrito> distritos = new ArrayList<>();
    private RequestQueue colaPeticiones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_perfil_apoderado, container, false);

        spDistritos = viewFragment.findViewById(R.id.sp_a_distrito);
        spDistritos.setEnabled(false);
        getDistritos();

        tvEstudiantes = viewFragment.findViewById(R.id.tv_estudiantes);
        tvNEstudiantes = viewFragment.findViewById(R.id.cantidadEstudiantes);

        btnEditar = viewFragment.findViewById(R.id.btnAEditar);
        btnGuardar = viewFragment.findViewById(R.id.btn_a_guardar);
        btnEditar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);

        etNombres = viewFragment.findViewById(R.id.txtANombres);
        etApellidos = viewFragment.findViewById(R.id.txtAApellidos);
        etDNI = viewFragment.findViewById(R.id.txtADNI);
        etDireccion = viewFragment.findViewById(R.id.txtADireccion);
        etCelular = viewFragment.findViewById(R.id.txtACelular);
        etCorreo = viewFragment.findViewById(R.id.txtACorreo);

        cargarDatosEnSesion();

        return viewFragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAEditar:
                cambiarEstados(true);
                break;
            case R.id.btn_a_guardar:
                if (validar()) {
                    actualizarApoderado();
                    cambiarEstados(false);
                }
                break;
        }
    }

    // Método para cargar en los EditText y spinner los datos de la sesión actual
    private void cargarDatosEnSesion() {
        // Obtener usuario en sessión
        SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
        String usuarioSesion = preferences.getString("usuario", "Error al cargar usuario.");

        try {
            apoderadoJson = new JSONObject(usuarioSesion);

            dniApoderado = apoderadoJson.getString("dniApoderado");

            Integer nEstudiantes = apoderadoJson.getJSONArray("estudiantes").length();
            tvNEstudiantes.setText(nEstudiantes.toString());

            etDNI.setText(dniApoderado);
            etNombres.setText(apoderadoJson.getString("nombre"));
            etApellidos.setText(apoderadoJson.getString("apellido"));
            etDireccion.setText(apoderadoJson.getString("direccion"));
            etCorreo.setText(apoderadoJson.getString("correo"));
            etCelular.setText(apoderadoJson.getString("celular"));
        } catch (JSONException e) {
            Toast.makeText(getContext(), usuarioSesion, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Método para actualizar apoderado en la BD
    private void actualizarApoderado() {
        colaPeticiones = Volley.newRequestQueue(getContext());
        String url = Url.URL_BASE + "/idat/rest/apoderados/editar_perfil/" + dniApoderado;
        JSONObject parametroJson = new JSONObject(capturarDatosActualizados());
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.PUT, url,
                parametroJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject datosRespuesta) {
                        try {
                            JSONObject usuarioJson = datosRespuesta.getJSONObject("apoderado");
                            System.out.println("usuario act: " + usuarioJson);

                            // Actualizar datos en sesión
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE).edit();
                            editor.putString("usuario", usuarioJson.toString());
                            editor.commit();

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

    // Método para capturar nuevos datos ingresados
    private Map<String, Object> capturarDatosActualizados() {
        Map<String, Object> nuevoApoderadoJson;
        nuevoApoderadoJson = new HashMap<>();
        nuevoApoderadoJson.put("dni_apoderado", dniApoderado);
        nuevoApoderadoJson.put("nombre", etNombres.getText().toString());
        nuevoApoderadoJson.put("apellido", etApellidos.getText().toString());
        nuevoApoderadoJson.put("celular", etCelular.getText().toString());
        nuevoApoderadoJson.put("correo", etCorreo.getText().toString());
        nuevoApoderadoJson.put("direccion", etDireccion.getText().toString());
        nuevoApoderadoJson.put("distrito_id", spDistritos.getSelectedItemId() + 1);

        return nuevoApoderadoJson;
    }

    // Método para habilitar y deshabilitar edición
    private void cambiarEstados(boolean estado) {
        tvNEstudiantes.setFocusable(true); // para ocultar el teclado

        if (estado) {
            btnEditar.setVisibility(View.INVISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
        } else {
            btnEditar.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.INVISIBLE);
        }

        etNombres.setEnabled(estado);
        etApellidos.setEnabled(estado);
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
            etNombres.setError("El campo es obligatorio");
            valido = false;
        }
        if (apellidos.isEmpty()) {
            etApellidos.setError("El campo es obligatorio");
            valido = false;
        }
        if (direccion.isEmpty()) {
            etDireccion.setError("El campo es obligatorio");
            valido = false;
        }
        if (celular.isEmpty()) {
            etCelular.setError("El campo es obligatorio");
            valido = false;
        }
        if (celular.length() < 9) {
            etCelular.setError("Número celular no valido");
            valido = false;
        }
        if (correo.isEmpty()) {
            etCorreo.setError("El campo es obligatorio");
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
        spDistritos.setAdapter(new ArrayAdapter</*Distrito*/>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, distritos));

        // Seleccionar en el spinner
        try {
            spDistritos.setSelection(apoderadoJson.getInt("distritoId") - 1);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al establecer distrito.", Toast.LENGTH_SHORT).show();
        }
    }
}