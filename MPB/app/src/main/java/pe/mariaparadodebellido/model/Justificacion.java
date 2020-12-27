package pe.mariaparadodebellido.model;

public class Justificacion {
    private String Descripcion;
            private String fecha;
            private String Titulo;

    public Justificacion() {
    }

    public Justificacion(String descripcion, String fecha, String titulo) {
        Descripcion = descripcion;
        this.fecha = fecha;
        Titulo = titulo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }
}

