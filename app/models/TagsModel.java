package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Find;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class TagsModel extends Model {
	@Id
	@JsonIgnore
	public Long id;
	public String tag;
	@ManyToMany(mappedBy = "tagsTarea")
	@JsonBackReference
	public Set<TareaModel> tareas;

	public static void create(TagsModel tags) {
		tags.save();
	}

	public void addTarea(TareaModel tarea) {
		tareas.add(tarea);
		tarea.addTag(this);
	}

	public void añadirTarea(TareaModel tarea) {
		this.addTarea(tarea);
		this.update();
	}

	public TagsModel(String tag) {
		super();
		this.tag = tag;
	}

	public static TagsModel crearTag(String tag) {
		TagsModel t = new TagsModel(tag);
		t.save();
		return t;
	}

	public static final Find<Long, TagsModel> find = new Find<Long, TagsModel>() {
	};

	public static List<TagsModel> listaTagsExistentes(String tags) {
		List<TagsModel> listaTags = new ArrayList<TagsModel>();
		List<String> TagsNombre=Arrays.asList(tags.split("\\s*,\\s*"));
		for (String nombre:TagsNombre){
			TagsModel tag = TagsModel.findByNombre(nombre);
			if (tag!=null){
				listaTags.add(tag);
			}
		}
		return listaTags;
	}

	public static TagsModel findById(Long id) {
		return find.byId(id);
	}

	public static TagsModel findByNombre(String texto) {
		if (find.where().eq("tag", texto).findList().size() == 0) {
			return null;
		} else {
			return find.where().eq("tag", texto).findList().get(0);
		}
	}

	public static List<TagsModel> all() {
		return find.all();
	}

	public static Boolean existe(String texto) {
		return TagsModel.findByNombre(texto) != null;
	}
}
