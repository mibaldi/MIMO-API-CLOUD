package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.ValidateWith;
import validators.TareaValidator;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Find;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class TareaModel extends Model {
	@Id
	public Long id;
	@ValidateWith(value = TareaValidator.class, message = "tarea ya creada")
	@Required
	public String titulo;
	public String descripcion;
	@Column(columnDefinition = "datetime")
	public Timestamp fechacreacion= new Timestamp(new Date().getTime());
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Timestamp fechafin;
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
		if (find.where().eq("titulo", texto).findList().size() == 0) {
			return null;
		} else {
			return find.where().eq("titulo", texto).findList().get(0);
		}
	}

	public static List<TareaModel> findByUsuario(List<UsuarioModel>listaUsuarios,UsuarioModel usuario) {
		List<TareaModel> lista = find.where().eq("usuarios", usuario)
				.findList();
		for (UsuarioModel u : listaUsuarios){
			List<TareaModel> listaAux = find.where().eq("usuarios", u).findList();
			lista.retainAll(listaAux);
		}
		return lista;
	}

	public static List<TareaModel> findBy(Map<String, String> map) {
		ExpressionList<TareaModel> listaBuscada = find.where();
		for (String key : map.keySet()) {
			listaBuscada.eq(key, map.get(key));
		}
		return listaBuscada.findList();

	}

	public static List<TareaModel> findByTag(List<TagsModel> tags,
			UsuarioModel u) {
		/*if (tags.isEmpty()) {
			return find.where().eq("usuarios", u).findList();
		}*/
		return find.where().eq("usuarios", u).in("tagsTarea", tags).findList();

	}

	public static List<TareaModel> all() {
		return find.all();
	}
	public static Integer numeroTareas(UsuarioModel u) {
		return find.where().eq("usuarios",u).findRowCount();
	}

	public static Boolean existe(String texto) {
		return TareaModel.findByNombre(texto) != null;
	}

	public void addUsuario(UsuarioModel usuario) {

		usuarios.add(usuario);
		usuario.addTarea(this);
	}
	public void añadirUsuario(UsuarioModel user){
		this.addUsuario(user);;
    	this.update();	
	}

	public void addTag(TagsModel tag) {
		tagsTarea.add(tag);
	}

	public boolean changeData(TareaModel newData) {
		boolean changed = false;

		if (newData.descripcion != null) {
			this.descripcion = newData.descripcion;
			changed = true;
		}
		if (newData.fechafin !=null){
			this.fechafin = newData.fechafin;
			changed=true;
		}

		return changed;
	}
	
	public static void borrarTarea(UsuarioModel u, TareaModel tt){
		u.tareasUsuario.remove(tt);
		u.update();
		if (tt.usuarios.size() >= 1){
			tt.usuarios.remove(u);
		}else{
			tt.delete();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public Date getFechacreacion() {
		return fechacreacion;
	}

	public void setFechacreacion(Date fechacreacion) {
		
		this.fechacreacion = new Timestamp(fechacreacion.getTime());
	}

	public Date getFechafin() {
		return fechafin;
	}

	public void setFechafin(Date fechafin) {
		this.fechafin = new Timestamp(fechafin.getTime());
	}

}
