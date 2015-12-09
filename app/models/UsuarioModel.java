package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.Valid;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class UsuarioModel  extends Model{
	@Id
	public Long id;
	public String nombre;
	public int edad;
	@OneToOne(cascade=CascadeType.ALL)
	@Valid
	@JsonManagedReference
	public PasswordModel pass;
	@OneToMany(cascade=CascadeType.ALL,mappedBy="usuario")
	@JsonManagedReference
	public List<EmailModel> emails;
	@ManyToMany(cascade = CascadeType.ALL)
	@JsonManagedReference
	public List<TareaModel> tareasUsuario;
	public UsuarioModel(){}
	public static void create(UsuarioModel usuario) {
		usuario.save();
	}


	public static final Find<Long, UsuarioModel> find = new Find<Long, UsuarioModel>() {
	};

	public static UsuarioModel findById(Long id) {
		return find.byId(id);
	}
	public static List<UsuarioModel> findBy(Map<String,String> map){
		ExpressionList<UsuarioModel> listaBuscada=find.where();
		for (String key:map.keySet()){
			listaBuscada.eq(key, map.get(key));
		}
		return listaBuscada.findList();
		  
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
	public void addTarea(TareaModel tarea) {
		tareasUsuario.add(tarea);
	}
	public void addEmail(EmailModel email) {
		emails.add(email);
		email.setUsuario(this);
	}
	public boolean changeData(UsuarioModel newData) {
		boolean changed = false;
		
		if (newData.nombre != null) {
			this.nombre = newData.nombre;
			changed = true;
		}
		if (newData.pass != null) {
			this.pass = newData.pass;
			changed = true;
		}
		if (newData.edad != 0) {
			this.edad = newData.edad;
			changed = true;
		}
		
		return changed;
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
