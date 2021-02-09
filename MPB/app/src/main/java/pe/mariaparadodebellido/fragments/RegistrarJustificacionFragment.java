package pe.mariaparadodebellido.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import pe.mariaparadodebellido.R;
import pe.mariaparadodebellido.model.Estudiante;
import pe.mariaparadodebellido.model.Justificacion;
import pe.mariaparadodebellido.util.Url;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RegistrarJustificacionFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final int REQUEST_IMAGE_GALLERY = 101;

    // Carpeta de imagenes en Firebase
    private FirebaseStorage storage = FirebaseStorage.getInstance(); // Ubicación base (bucket?)
    private final String RUTA = "justificaciones/";

    // Codigo de confirmación para una nueva imagen
    public final Integer NUEVA_IMAGEN = 1;

    private String dniApoderado, dniEstudiante;

    private Spinner spEstudiantes;
    private ArrayList<Estudiante> estudiantes = new ArrayList<>();

    private LocalDateTime fechaEnvio;

    private EditText txtFechaInasistencia, txtJustificacion, txttitulo;
    private ImageView ivImagen;
    private Button btnEnviar, btnCancelar, btnSeleccionar, btnBorrar;

    RequestQueue Justi_queue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_registrar_justificacion, container, false);

        try {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("info_usuario", Context.MODE_PRIVATE);
            JSONObject eJson = new JSONObject(preferences.getString("usuario", "usuario no existe"));
            dniApoderado = eJson.getString("dniApoderado");
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error al cargar usuario.", Toast.LENGTH_SHORT).show();
        }

        txtFechaInasistencia = viewFragment.findViewById(R.id.txt_FechaInasistencia);
        txtFechaInasistencia.setText(DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(LocalDate.now().plusDays(1)));

        spEstudiantes = viewFragment.findViewById(R.id.sp_rj_estudiantes);
        getEstudiantes();

        txtJustificacion = viewFragment.findViewById(R.id.txt_Justificacion);
        txttitulo = viewFragment.findViewById(R.id.txttitulo);
        ivImagen = viewFragment.findViewById(R.id.iv_imagen);
        btnSeleccionar = viewFragment.findViewById(R.id.btn_seleccionar);
        btnBorrar = viewFragment.findViewById(R.id.btn_borrar);
        btnEnviar = viewFragment.findViewById(R.id.btn_Enviar);
        btnCancelar = viewFragment.findViewById(R.id.btn_Cancelar);
        txtFechaInasistencia.setOnClickListener(this);
        ivImagen.setOnClickListener(this);
        btnSeleccionar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        return viewFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_FechaInasistencia:
                mostrarCalendario();
                break;
            case R.id.btn_borrar:
                borrarImagen();
                break;
            case R.id.btn_Enviar:
                fechaEnvio = LocalDateTime.now();
                if (validar()) {
                    cargarWebServices();
                }
                break;
            case R.id.btn_Cancelar:
                limpiar();
                break;
            default: // btnSeleccionar y ivImagen
                abrirGaleria();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarWebServices() {
        cambiarEstados(false);
        String urlJusti = Url.URL_BASE + "/idat/rest/justificaciones/registrar";
        JSONObject nuevaJustificacion = new JSONObject(capturarDatos());
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST, urlJusti,
                nuevaJustificacion,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        try {
                            if (ivImagen.getTag() == NUEVA_IMAGEN) {
                                Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_LONG).show();
                                Integer justificacionId = respuesta.getInt("justificacion_id");
                                guardarImagen(justificacionId);
                            } else {
                                Toast.makeText(getContext(), "Justificación registrada.", Toast.LENGTH_SHORT).show();
                                cambiarEstados(true);
                                limpiar();
                            }

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
        Justi_queue.add(peticion);
    }

    // Método para capturar nuevos datos ingresados
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Map<String, Object> capturarDatos() {
        Map<String, Object> nJ;
        nJ = new HashMap<>();
        nJ.put("dni_estudiante", dniEstudiante);
        nJ.put("titulo", txttitulo.getText().toString());
        nJ.put("descripcion", txtJustificacion.getText().toString());

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fJustificacion = DateTimeFormatter.ISO_LOCAL_DATE.format(
                LocalDate.parse(txtFechaInasistencia.getText(), formato));
        nJ.put("fecha_justificacion", fJustificacion);

        //2020-10-04 21:57:00.000000
        String fEnvio = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.S").format(fechaEnvio);
        nJ.put("fecha_envio", fEnvio);

        return nJ;
    }

    // Método para abrir el explorador de archivos solo en imagenes
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // solo en imagenes
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY); // 1. se envia REQUEST_IMAGE_GALLERY
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 2. se recibe y compara si es igual al REQUEST_IMAGE_GALLERY
        // y si hay data
        if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
            Uri imagen = data.getData();
            ivImagen.setImageURI(imagen);
            ivImagen.setTag(NUEVA_IMAGEN);
        } else {
            Log.e("TAG", "Resultado: " + resultCode);
            Toast.makeText(getContext(), "No ha seleccionado una foto.", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para enviar la imagen a Firebase Storage
    private void guardarImagen(Integer justificacionId) {
        StorageReference storageRef = storage.getReference();
        StorageReference nuevaImagenRef = storageRef.child(RUTA + LocalDate.from(fechaEnvio) + "_" + justificacionId + ".jpg");

        // Capturar imagen en bytes ----------------------------------------------------------------
        ivImagen.setDrawingCacheEnabled(true);
        ivImagen.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ivImagen.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // -----------------------------------------------------------------------------------------

        UploadTask uploadTask = nuevaImagenRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Justificación registrada.", Toast.LENGTH_SHORT).show();
                cambiarEstados(true);
                limpiar();
            }
        });
    }

    public boolean validar() {
        boolean valido = true;
        String jjustificacion, jtitulo;
        jtitulo = txttitulo.getText().toString();
        jjustificacion = txtJustificacion.getText().toString();

        if (jtitulo.isEmpty()) {
            txttitulo.setError("Este campo no puede quedar vacio.");
            valido = false;
        }
        if (jjustificacion.isEmpty()) {
            txtJustificacion.setError("Este campo no puede quedar vacio.");
            valido = false;
        }

        return valido;
    }

    // Método para limpiar campos de la vista
    private void limpiar() {
        txttitulo.setText("");
        spEstudiantes.setSelection(0);
        txtFechaInasistencia.setText(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now().plusDays(1)));
        txtJustificacion.setText("");
        borrarImagen();
    }

    // Método para habilitar y deshabilitar edición
    private void cambiarEstados(boolean estado) {
        txtFechaInasistencia.setEnabled(estado);
        spEstudiantes.setEnabled(estado);
        txtJustificacion.setEnabled(estado);
        txttitulo.setEnabled(estado);
        ivImagen.setEnabled(estado);
        btnSeleccionar.setEnabled(estado);
        btnBorrar.setEnabled(estado);
        btnEnviar.setEnabled(estado);
        btnCancelar.setEnabled(estado);
    }

    // Método para reemplazar imagen nueva por "seleccionar_imagen.png"
    private void borrarImagen() {
        ivImagen.setImageResource(R.drawable.seleccionar_imagen);
        ivImagen.setTag(R.drawable.seleccionar_imagen);
    }

    // Métodos para el spinner de Estudiantes ---------
    private void getEstudiantes() {
        String url = Url.URL_BASE + "/idat/rest/apoderados/nombre_estudiantes/" + dniApoderado;
        Justi_queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject objjson = jsonArray.getJSONObject(i);
                                estudiantes.add(new Estudiante(
                                        objjson.getString("dniEstudiante"),
                                        objjson.getString("nombre")
                                ));
                            }
                            llenarSpinner();
                        } catch (JSONException ex) {
                            Log.e("ErrorRequest", ex.getMessage());
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar estudiantes.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(getContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
        Justi_queue.add(peticion);
    }
    private void llenarSpinner() {
        /*
         *  Elspinner se llena solo con el "nombre" porque al model Estudiante se le está sobreescribiendo
         *  el método "toString()" que indica que solo se llene con ese, sucede algo como esto:
         *
         *                 ArrayAdapter<Estudiante> = ArrayAdapter<Estudiante.toString()>
         */
        spEstudiantes.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, estudiantes));

        // Listener
        spEstudiantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override // Lo que pasa cuando selecciona un item del spinner
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Estudiante e = (Estudiante) adapterView.getSelectedItem();

                // Obtener el DNI del estudiante seleccionado y cambiar el dni que se envía a la consulta al WebService
                dniEstudiante = e.getDniEstudiante();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    // ------------------------------------------------

    // Método para funcionalidad del Calendario de "txtFechaInasistencia"
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mostrarCalendario() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(txtFechaInasistencia.getText(), formato);

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

                txtFechaInasistencia.setText(diaConcatenado + "/" + mesConcatenado + "/" + anioSeleccionado);
            }
        }, anio, mes, dia);


        // Fechha mínima
        LocalDate fechaMañana = LocalDate.now().plusDays(1);
        Calendar c = Calendar.getInstance();
        c.set(fechaMañana.getYear(), fechaMañana.getMonthValue(), fechaMañana.getDayOfMonth());
        dtpCalendario.getDatePicker().setMinDate(c.getTimeInMillis());

        // Fechha máxima
        LocalDate fechaMax = LocalDate.now().plusDays((Justificacion.FECHA_MAX));
        c.set(fechaMax.getYear(), fechaMax.getMonthValue(), fechaMax.getDayOfMonth());
        dtpCalendario.getDatePicker().setMaxDate(c.getTimeInMillis());

        dtpCalendario.show();
    }
}