package pe.mariaparadodebellido.model;

public class Docente {
    private Integer DocenteId;
    private String Nombres;
    private String Apellido;
    private String Celular;
    private String Correo;
    private String Sexo;

    private String curso;

    public Docente(){
    }
    public Docente(Integer DocenteId, String Nombres, String Apellido, String Celular, String Correo, String Numero, String Sexo) {
        this.DocenteId = DocenteId;
        this.Nombres = Nombres;
        this.Apellido = Apellido;
        this.Celular = Celular;
        this.Correo = Correo;
        this.Sexo = Sexo;
    }

    public Integer getDocenteId() {
        return DocenteId;
    }

    public void setDocenteId(Integer docenteId) {
        DocenteId = docenteId;
    }

    public String getNombres() {
        return Nombres;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String celular) {
        Celular = celular;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}


