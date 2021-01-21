package pe.mariaparadodebellido.model;

import java.util.Date;

public class Estudiante {
    private String dniEstudiante;
    private String nombre;
    private String apellido;
    private String fNacimiento;
    private String celular;
    private String correo;
    private String direccion;
    private String pass;
    private Boolean estado;
    private String condicion;
    private Distrito distrito;
    private Apoderado apoderado;

    public static final int EDAD_MIN = 10;
    public static final int EDAD_MAX = 23;

    public String getNombreApellido(){
        return this.nombre + " " + this.apellido;
    }

    public Estudiante() {
    }

    public Estudiante(String dniEstudiante, String nombre) {
        this.dniEstudiante = dniEstudiante;
        this.nombre = nombre;
    }

    public Estudiante(String dniEstudiante, String nombre, String apellido, String fNacimiento, String celular, String correo, String direccion, String pass, Boolean estado, String condicion) {
        this.dniEstudiante = dniEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fNacimiento = fNacimiento;
        this.celular = celular;
        this.correo = correo;
        this.direccion = direccion;
        this.pass = pass;
        this.estado = estado;
        this.condicion = condicion;
    }

    public Estudiante(String dniEstudiante, String nombre, String apellido, String fNacimiento, String celular, String correo, String direccion, String pass, Boolean estado, String condicion, Distrito distrito, Apoderado apoderado) {
        this.dniEstudiante = dniEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fNacimiento = fNacimiento;
        this.celular = celular;
        this.correo = correo;
        this.direccion = direccion;
        this.pass = pass;
        this.estado = estado;
        this.condicion = condicion;
        this.distrito = distrito;
        this.apoderado = apoderado;
    }

    public String getDniEstudiante() {
        return dniEstudiante;
    }

    public void setDniEstudiante(String dniEstudiante) {
        this.dniEstudiante = dniEstudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getfNacimiento() {
        return fNacimiento;
    }

    public void setfNacimiento(String fNacimiento) {
        this.fNacimiento = fNacimiento;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public Apoderado getApoderado() {
        return apoderado;
    }

    public void setApoderado(Apoderado apoderado) {
        this.apoderado = apoderado;
    }

    @Override // EL "toString" hace que el spinner se llene con el nombre
    public String toString() {
        return nombre;
    }
}
