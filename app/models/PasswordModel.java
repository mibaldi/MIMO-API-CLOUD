package models;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import util.passwordInter;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
public class PasswordModel extends Model {
	@Id
	public Long id;
	@passwordInter
	public String contrasenia = "";
	@OneToOne(mappedBy="pass")
	@JsonBackReference
	public UsuarioModel usuario;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
}
