package pe.mariaparadodebellido.model;

public class Estudiante {
    private String dni;
    private String nombre;

    public Estudiante() {
    }

    public Estudiante(String dni, String nombre) {
        this.dni = dni;
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override // EL "toString" hace que el spinner se llene con el nombre
    public String toString() {
        return nombre;
    }
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Estudiante that = (Estudiante) o;

        if (dni != null ? !dni.equals(that.dni) : that.dni != null) return false;
        return nombre != null ? nombre.equals(that.nombre) : that.nombre == null;
    }

     */
}
