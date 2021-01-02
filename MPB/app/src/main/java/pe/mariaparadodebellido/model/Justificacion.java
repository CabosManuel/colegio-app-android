package pe.mariaparadodebellido.model;

public class Justificacion {
    private Integer justificacionId;
    private String titulo;
    private String fechaEnvio;
    private String fechaJustificacion;
    private String dniEstudiante;
    private String descripcion;

    public Justificacion() {
    }

    public Justificacion(Integer justificacionId, String titulo, String fechaEnvio, String fechaJustificacion, String dniEstudiante, String descripcion) {
        this.justificacionId = justificacionId;
        this.titulo = titulo;
        this.fechaEnvio = fechaEnvio;
        this.fechaJustificacion = fechaJustificacion;
        this.dniEstudiante = dniEstudiante;
        this.descripcion = descripcion;
    }

    public Integer getJustificacionId() {
        return justificacionId;
    }

    public void setJustificacionId(Integer justificacionId) {
        this.justificacionId = justificacionId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getFechaJustificacion() {
        return fechaJustificacion;
    }

    public void setFechaJustificacion(String fechaJustificacion) {
        this.fechaJustificacion = fechaJustificacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDniEstudiante() {
        return dniEstudiante;
    }

    public void setDniEstudiante(String dniEstudiante) {
        this.dniEstudiante = dniEstudiante;
    }
}

