package pe.mariaparadodebellido.model;

public class Nota {
    private Integer idNota;
    private String curso;
    private Double nota1;
    private Double nota2;
    private Double nota3;
    private String fecha;

    public Nota() {
    }

    public Nota(Integer idNota, String curso, Double nota1, Double nota2, Double nota3, String fecha) {
        this.idNota = idNota;
        this.curso = curso;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.fecha = fecha;
    }

    public Nota(String curso, Double nota1, Double nota2, Double nota3, String fecha) {
        this.curso = curso;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.fecha = fecha;
    }

    public Nota(String curso, Double nota1, Double nota2, Double nota3) {
        this.curso = curso;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
    }

    public Integer getIdNota() {
        return idNota;
    }

    public void setIdNota(Integer idNota) {
        this.idNota = idNota;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Double getNota1() {
        return nota1;
    }

    public void setNota1(Double nota1) {
        this.nota1 = nota1;
    }

    public Double getNota2() {
        return nota2;
    }

    public void setNota2(Double nota2) {
        this.nota2 = nota2;
    }

    public Double getNota3() {
        return nota3;
    }

    public void setNota3(Double nota3) {
        this.nota3 = nota3;
    }
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
