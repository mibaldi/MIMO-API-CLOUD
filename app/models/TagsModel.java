package models;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Find;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
public class TagsModel extends Model{
	@Id
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
	public TagsModel(String tag) {
		super();
		this.tag = tag;
	}
	public static final Find<Long, TagsModel> find = new Find<Long, TagsModel>() {
	};

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
