package models;

import java.util.List;

import javax.persistence.*;

import com.avaje.ebean.Model;

@Entity
public class UsuarioModel  extends Model{
	@Id
	public Long id;
	public String nombre;
	public int edad;
	public UsuarioModel(){}
	public static void create(UsuarioModel usuario) {
		usuario.save();
	}


	public static final Find<Long, UsuarioModel> find = new Find<Long, UsuarioModel>() {
	};

	public static UsuarioModel findById(Long id) {
		return find.byId(id);
	}

	public static UsuarioModel findByNombre(String nombre) {
		if(find.where().eq("nombre", nombre).findList().size()==0){
			return null;
		}else{
			return find.where().eq("nombre", nombre).findList().get(0);
		}
		
	}
	public static List<UsuarioModel> all() {
		return find.all();
	}
	public static Boolean existe(String nombre){
		return UsuarioModel.findByNombre(nombre) != null;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
}
