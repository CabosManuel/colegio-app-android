package pe.mariaparadodebellido.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import pe.mariaparadodebellido.Login;
import pe.mariaparadodebellido.MenuApoderado;
import pe.mariaparadodebellido.MenuEstudiante;
import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.adapter.NotificacionAdapter;
import pe.mariaparadodebellido.model.Distrito;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Notificacion;
import pe.mariaparadodebellido.util.Url;

public class PerfilApoderadoFragment extends Fragment implements View.OnClickListener {

    private String dniApoderado = "";

    private JSONObject eJson;
    private Map<String, Object> newApoderadoJson;

    private TextView tvNEstudiantes, tvEstudiantes;
    private TextInputEditText etNombres, etApellidos, etDNI,
            etDireccion, etCelular, etCorreo;
    private Button btnEditar, btnGuardar;

    private Spinner spDistritos;
    private ArrayList<Distrito> distritos = new ArrayList<>();
    private RequestQueue queue;

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
        getNumeroEstudiantes();

        tvNEstudiantes.setFocusable(true);

        return viewFragment;
    }

    private void getNumeroEstudiantes(){
        //idat/rest/apoderados/n_estudiantes?dniApoderado=
        Integer nEstudiantes = 0;

        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/apoderados/n_estudiantes?dniApoderado="+dniApoderado+"&?wsdl";
        queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvNEstudiantes.setText(String.valueOf(response.getInt("nE")));
                            if(response.getInt("nE")>1){
                                tvEstudiantes.setText("Estudiantes");
                            }else{
                                tvEstudiantes.setText("Estudiante");
                            }

                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                //Log.e("ErrorVolley", volleyError.getMessage());
                Toast.makeText(getContext(), "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }

    private void cargarDatosEnSesion() {
        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            eJson = new JSONObject(preferences.getString("usuario", "usuario no existe"));
            dniApoderado = eJson.getString("dniApoderado");

            etDNI.setText(dniApoderado);
            etNombres.setText(eJson.getString("nombre"));
            etApellidos.setText(eJson.getString("apellido"));
            etDireccion.setText(eJson.getString("direccion"));
            etCorreo.setText(eJson.getString("correo"));
            etCelular.setText(eJson.getString("celular"));

            spDistritos.setSelection(eJson.getJSONObject("distrito").getInt("distritoId")-1);

            //Toast.makeText(getContext(), "distrito set: "+spDistritos.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
            System.err.println("ERROR:"+e.getMessage());
        }
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

    private boolean validar() {
        boolean valido = true;
        String nombres = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String direccion = etDireccion.getText().toString();
        String correo = etCorreo.getText().toString();
        String celular = etCelular.getText().toString();
        String dni = etDNI.getText().toString();

        if (dni.isEmpty()) {
            etDNI.setError("El campo es obligatorio");
            valido = false;
        }if (dni.length()<8) {
            etDNI.setError("Número DNI incorrecto");
            valido = false;
        }if (nombres.isEmpty()){
            etNombres.setError("El campo es obligatorio");
            valido = false;
        }if (apellidos.isEmpty()){
            etApellidos.setError("El campo es obligatorio");
            valido = false;
        }if (direccion.isEmpty()){
            etDireccion.setError("El campo es obligatorio");
            valido = false;
        }if (celular.isEmpty()) {
            etCelular.setError("El campo es obligatorio");
            valido = false;
        }if (celular.length()<9){
            etCelular.setError("Número celular incorrecto");
            valido = false;
        }if (correo.isEmpty()) {
            etCorreo.setError("El campo es obligatorio");
            valido = false;
        }

        return valido;
    }

    private void actualizarApoderado() {
        queue = Volley.newRequestQueue(getContext());
        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/apoderados/editar_perfil/"+dniApoderado;

        newApoderadoJson = new HashMap<>();
        newApoderadoJson.put("dniApoderado", etDNI.getText().toString());
        newApoderadoJson.put("nombres", etNombres.getText().toString());
        newApoderadoJson.put("apellidos", etApellidos.getText().toString());
        newApoderadoJson.put("celular", etCelular.getText().toString());
        newApoderadoJson.put("correo", etCorreo.getText().toString());
        newApoderadoJson.put("direccion", etDireccion.getText().toString());

        //Toast.makeText(getContext(), "distrito id: "+spDistritos.getSelectedItemId(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), "distrito item: "+spDistritos.getSelectedItem(), Toast.LENGTH_SHORT).show();
        newApoderadoJson.put("distritoId", spDistritos.getSelectedItemId()+1);

        JSONObject parametroJson = new JSONObject(newApoderadoJson);

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST, url,
                parametroJson,
                new Response.Listener<JSONObject>() {

                    String msj = "";

                    @Override
                    public void onResponse(JSONObject datosApoderado) {
                        try {
                            if (datosApoderado.getBoolean("rpta")) {
                                msj = datosApoderado.getString("msj");

                                JSONObject usuarioJson = datosApoderado.getJSONObject("apoderado");

                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE).edit();
                                editor.putString("usuario",usuarioJson.toString());
                                editor.commit();

                                Toast.makeText(getContext(), "Se actualizó la información correctamente.", Toast.LENGTH_SHORT).show();
                                
                                //actualizarSesion(usuarioJson.toString());
                            } else {
                                msj = datosApoderado.getString("msj");
                                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "Error inesperado.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
                            //Log.e("Error Conexion Voley", ex.getMessage());
                            //System.err.println("Error Conexion Voley: " + );
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
                        //Log.e("Error Conexion Voley", error.getMessage());
                        System.err.println("Error Conexion Voley: " + error.getMessage());
                    }
                }
        );
        queue.add(peticion);
    }

    private void getDistritos() {
        String url = "http://" + Url.IP + ":" + Url.PUERTO + "/idat/rest/distritos/listar?wsdl";
        queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                distritos.add(new Distrito(
                                        objjson.getInt("distritoId"),
                                        objjson.getString("nombre")
                                ));
                            }

                            llenarSpinner();

                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error de en el servidor?", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e("ErrorVolley", volleyError.getMessage());
                Toast.makeText(getContext(), "Error de conexión?", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(peticion);
    }

    private void llenarSpinner() {
        spDistritos.setAdapter(new ArrayAdapter<Distrito>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, distritos));

        // Listener
        spDistritos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // Lo que pasa cuando selecciona un item del spinner
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    Distrito d = (Distrito) adapterView.getSelectedItem();
                    //Toast.makeText(getContext(), "distrito seleccionado: "+d.getDistritoId(), Toast.LENGTH_SHORT).show();

                    // Obtener el id seleccionado y cambiar el dni que se envía a la consulta al WebService
                    newApoderadoJson.put("distritoId", d.getDistritoId());
                }catch (Exception e){
                    //Toast.makeText(getContext(), "entro al listener y fallo", Toast.LENGTH_SHORT).show();
                    System.err.println("ERROR: "+e.getMessage());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        try {
            spDistritos.setSelection(eJson.getJSONObject("distrito").getInt("distritoId")-1);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al establecer distrito.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cambiarEstados(boolean estado) {

        tvNEstudiantes.setFocusable(true); // para ocultar el teclado

        if(estado) {
            btnEditar.setVisibility(View.INVISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);
        }else{
            btnEditar.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.INVISIBLE);
        }

        // enabled
        etNombres.setEnabled(estado);
        etApellidos.setEnabled(estado);
        etDNI.setEnabled(estado);
        etDireccion.setEnabled(estado);
        etCelular.setEnabled(estado);
        etCorreo.setEnabled(estado);

        //spDistritos.setEnabled(estado);
        spDistritos.setEnabled(estado);
    }
}