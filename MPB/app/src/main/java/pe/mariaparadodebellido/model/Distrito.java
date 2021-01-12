package pe.mariaparadodebellido.model;

public class Distrito {

	private Integer distritoId;
	private String nombre;

	public Distrito() {
	}

	public Distrito(Integer distritoId, String nombre) {
		this.distritoId = distritoId;
		this.nombre = nombre;
	}

	public Integer getDistritoId() {
		return distritoId;
	}

	public void setDistritoId(Integer distritoId) {
		this.distritoId = distritoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return nombre;
	}
}
