package pe.mariaparadodebellido.model;

public class Asistencia {
    private String fecha;
    private Boolean estado;

    public Asistencia() {
    }

    public Asistencia(String fecha, Boolean estado) {
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
