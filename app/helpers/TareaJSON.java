package helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.TagsModel;
import models.TareaModel;
import models.UsuarioModel;
public class TareaJSON{
		public Long id;
		public String titulo;
		public String descripcion;
		public List<String> Usuarios = new ArrayList<String>();
		public List<String> Tags = new ArrayList<String>();
		
		public TareaJSON(TareaModel t){
			this.id=t.id;
			this.titulo=t.getTitulo();
			this.descripcion=t.getDescripcion();
			rellenarUsuarios(t.usuarios);
			rellenarTags(t.tagsTarea);
		}
		public void rellenarUsuarios(Set<UsuarioModel> lu){
			for (UsuarioModel u:lu){
				this.Usuarios.add(u.getUsername());
			}
		}
		public void rellenarTags(List<TagsModel> lt){
			for (TagsModel t:lt){
				this.Tags.add(t.tag);
			}
		}
		public static List<TareaJSON> convertir(List<TareaModel>l){
			List<TareaJSON> tJsonArray = new ArrayList<TareaJSON>();
			for (TareaModel t:l){
				TareaJSON tJson= new TareaJSON(t);
				tJsonArray.add(tJson);
			}
			return tJsonArray;
		}
}

