package pe.mariaparadodebellido.model;

public class Permiso {

    private Integer idPermiso;
    private String tipo;
    private String fechaEnvio;
    private String fechaLimite;
    private String titulo;
    private String descripcion;
    private Character estado;
    private String dniEstudiante;

    private Integer iconoEstado;
    private Integer color;
    private String estadoCompleto;

    public Permiso() {
    }

    public Permiso(Integer idPermiso, String tipo, String fechaEnvio, String fechaLimite,
                   String titulo, String descripcion, Character estado, String dniEstudiante,
                   Integer color, String estadoCompleto, Integer iconoEstado) {
        this.idPermiso = idPermiso;
        this.tipo = tipo;
        this.fechaEnvio = fechaEnvio;
        this.fechaLimite = fechaLimite;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.dniEstudiante = dniEstudiante;
        this.color = color;
        this.estadoCompleto = estadoCompleto;
        this.iconoEstado = iconoEstado;
    }

    public Integer getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Integer idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Character getEstado() {
        return estado;
    }

    public void setEstado(Character estado) {
        this.estado = estado;
    }

    public String getDniEstudiante() {
        return dniEstudiante;
    }

    public void setDniEstudiante(String dniEstudiante) {
        this.dniEstudiante = dniEstudiante;
    }

    public Integer getIconoEstado() {
        return iconoEstado;
    }

    public void setIconoEstado(Integer iconoEstado) {
        this.iconoEstado = iconoEstado;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getEstadoCompleto() {
        return estadoCompleto;
    }

    public void setEstadoCompleto(String estadoCompleto) {
        this.estadoCompleto = estadoCompleto;
    }
}
