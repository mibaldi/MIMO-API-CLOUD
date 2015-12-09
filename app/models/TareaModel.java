package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import models.TareaModel.TareaJSON;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Find;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class TareaModel extends Model {
	@Id
	public Long id;
	public String texto;
	
	@ManyToMany(mappedBy = "tareasUsuario")
	@JsonBackReference
	public Set<UsuarioModel> usuarios;
	@ManyToMany(cascade = CascadeType.ALL)
	@JsonManagedReference
	public List<TagsModel> tagsTarea;

	public TareaModel() {
		super();
	}
	
	public static void create(TareaModel tarea) {
		tarea.save();
	}

	public static final Find<Long, TareaModel> find = new Find<Long, TareaModel>() {
	};

	public static TareaModel findById(Long id) {
		return find.byId(id);
	}

	public static TareaModel findByNombre(String texto) {
		if (find.where().eq("texto", texto).findList().size() == 0) {
			return null;
		} else {
			return find.where().eq("texto", texto).findList().get(0);
		}
	}
	public static List<TareaModel> findByUsuario(UsuarioModel usuario) {
		
		if (find.where().eq("usuarios", usuario).findList().size() == 0) {
			return null;
		} else {
			return find.where().eq("usuarios", usuario).findList();
		}
	}
	public static List<TareaModel> findByTag(TagsModel tag) {
		
		if (find.where().eq("tagsTarea", tag).findList().size() == 0) {
			return null;
		} else {
			return find.where().eq("tagsTarea", tag).findList();
		}
	}

	public static List<TareaModel> all() {
		return find.all();
	}

	public static Boolean existe(String texto) {
		return TareaModel.findByNombre(texto) != null;
	}

	public void addUsuario(UsuarioModel usuario) {
		usuarios.add(usuario);
		usuario.addTarea(this);
	}
	public void addTag(TagsModel tag) {
		tagsTarea.add(tag);
	}
	public boolean changeData(TareaModel newData) {
		boolean changed = false;
		
		if (newData.texto != null) {
			this.texto = newData.texto;
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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Set<UsuarioModel> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<UsuarioModel> usuarios) {
		this.usuarios = usuarios;
	}

	public List<TagsModel> getTagsTarea() {
		return tagsTarea;
	}

	public void setTagsTarea(List<TagsModel> tagsTarea) {
		this.tagsTarea = tagsTarea;
	}

	public static Find<Long, TareaModel> getFind() {
		return find;
	}
	public static class TareaJSON{
		public String texto;
		public List<String> Tags;
		public List<String> Usuarios;
		public TareaJSON(String texto, List<String> tags , List<String> usuarios){
			this.texto=texto;
			this.Usuarios=usuarios;
			this.Tags=tags;
		}
	}
}

