package pe.mariaparadodebellido.model;

public class HorarioDetalle {
    private String dia;
    private String hInicio;
    private String hFin;
    private String nombreCurso;

    public HorarioDetalle() {
    }

    public HorarioDetalle(String dia, String hInicio, String hFin, String nombreCurso) {
        this.dia = dia;
        this.hInicio = hInicio;
        this.hFin = hFin;
        this.nombreCurso = nombreCurso;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String gethInicio() {
        return hInicio;
    }

    public void sethInicio(String hInicio) {
        this.hInicio = hInicio;
    }

    public String gethFin() {
        return hFin;
    }

    public void sethFin(String hFin) {
        this.hFin = hFin;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }
}
