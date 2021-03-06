package models;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.ValidateWith;
import validators.EmailValidator;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
public class EmailModel extends Model{
	@Id
	@JsonIgnore
	public Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public UsuarioModel getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioModel usuario) {
		this.usuario = usuario;
	}
	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof EmailModel) && (((EmailModel) obj).getEmail() == this.getEmail())) {
			return true;
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		int hash = 7;
		int value=this.email.hashCode();
		hash = (int) (97 * hash + value);
		return hash;
	}
	@ValidateWith(value=EmailValidator.class, message="email existente o con formato erroneo")
	public String email;
	@ManyToOne
	@JsonBackReference
	public UsuarioModel usuario;
	public static Boolean existe(String texto) {
		return EmailModel.findByNombre(texto) != null;
	}
	public static final Find<Long, EmailModel> find = new Find<Long, EmailModel>() {
	};

	public static EmailModel findByNombre(String texto) {
		if (find.where().eq("email", texto).findList().size() == 0) {
			return null;
		} else {
			return find.where().eq("email", texto).findList().get(0);
		}
	}
}
