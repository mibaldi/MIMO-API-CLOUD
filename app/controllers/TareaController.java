package controllers;

import helpers.ControllerHelper;
import helpers.TareaJSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import models.TagsModel;
import models.TareaModel;
import models.UsuarioModel;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import util.secured;

public class TareaController extends Controller {

    public Result index() {
        return ok("hola");
    }
    /**
	 * Action method para GET /tarea/<texto>.
	 * 
	 * @param titulo de tarea a recuperar.
	 */
    @Security.Authenticated(secured.class)
    public Result getTarea(String texto){
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	TareaModel tt=u.tareaExistente(texto);
    	if (tt!=null){
    		if (request().accepts("application/xml")) {
    			return ok(views.xml.vistaTarea.render(tt));
    		}else if (request().accepts("application/json")) {
    			
    			TareaJSON result =new TareaJSON(tt);
    			return ok(Json.toJson(result));
    		}
    		else {
    			return badRequest("Unsupported format");
    		}
    	}
    	else {
    		return unauthorized("no existe la tarea para el usuario logeado");
    	}
    }
    /**
	 * Action method para GET /tareas
	 */
    @Security.Authenticated(secured.class)
    public Result listaTareasUsuario(){
    	UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			return notFound();
		}
		String usuarios=request().getQueryString("users");
		List<UsuarioModel> listaUsuarios=new ArrayList<UsuarioModel>();
		listaUsuarios.add(usuario);
		if (usuarios!=null){
			List<String> UsuariosNombre=Arrays.asList(usuarios.split("\\s*,\\s*"));
			for (String nombre:UsuariosNombre){
				UsuarioModel u = UsuarioModel.findByUsername(nombre);
				if (u == null) {
					return notFound("usuario "+ nombre +" no encontrado");
				}
				listaUsuarios.add(u);
			}
		}
		Integer count = TareaModel.numeroTareas(usuario);
    	if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByUsuario(listaUsuarios, usuario),count));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaJSON.convertir(TareaModel.findByUsuario(listaUsuarios,usuario)));
			return ok(jn);
		} else {
			return badRequest("Unsupported format");
		}
    }
    /**
	 * Action method para GET /tareas/tag
	 * @param tag nombre del tag dentro de las tareas.
	 */
    @Security.Authenticated(secured.class)
    public Result listaTareasTag(){
    	UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			return notFound();
		}
		String tags=request().getQueryString("tags");
		List<TagsModel> listaTags=new ArrayList<TagsModel>();
		if (tags!=null){
			List<String> TagsNombre=Arrays.asList(tags.split("\\s*,\\s*"));
			for (String nombre:TagsNombre){
				TagsModel tag = TagsModel.findByNombre(nombre);
				if (tag == null) {
					return notFound("tag "+ nombre +" no encontrado");
				}
				listaTags.add(tag);
			}
		}
		Integer count = TareaModel.numeroTareas(usuario);
    	if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByTag(listaTags,usuario),count));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaJSON.convertir(TareaModel.findByTag(listaTags,usuario)));
			return ok(jn);
		} else {
			return badRequest("Unsupported format");
		}
    }
  
    /**
	 * Action method para POST /tarea.
	 * Se deben pasar los atributos de la tarea en el body de la petición. 
	 * 
	 */
    @Security.Authenticated(secured.class)
    public Result createTarea() {
    	UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (usuario==null){
    		return Results.notFound("usuario no logeado");
    	}
    	Form<TareaModel> form = Form.form(TareaModel.class).bindFromRequest();
    	if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_tarea", form.errorsAsJson()));
		}
    	TareaModel tt=form.get();
    	System.out.println(tt.titulo);
    	tt.addUsuario(usuario);
    	TareaModel.create(tt);		
        return redirect(routes.TareaController.listaTareasUsuario());
    }
    /**
	 * Action method para PUT /tareas/<nombre>
	 * Se deben pasar los atributos a modificar en el body de la petición. 
	 * @param nombre titulo de la tarea a modificar.
	 */
    public Result updateTarea(String texto){
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	TareaModel tarea=u.tareaExistente(texto);
    	if (tarea!=null){
    		return notFound();
    	}
    	
		Form<TareaModel> form = Form.form(TareaModel.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_tarea", form.errorsAsJson()));
		}
		Result res;
		if (tarea.changeData(form.get())) {
			tarea.update();
			res = ok("tarea modificada");
		}
		else {
			res = status(NOT_MODIFIED);
		}
		return res;
    }
    /**
	 * Action method para PUT /tareaUsuario/<texto>/<nombreUsuario>
	 * @param texto= nombre tarea a modificar, nombreUsuario= username del usuario a agregar a la tarea
	 */
    @Security.Authenticated(secured.class)
    public Result anadirUsuario(String texto,String usuario){
    	UsuarioModel uu = UsuarioModel.findByUsername(usuario);
    	if (uu == null) {
			return notFound("Usuario no valido");
		}
    	TareaModel tarea = TareaModel.findByNombre(texto);
		if (tarea == null) {
			return notFound("Tarea no valida");
		}
		tarea.addUsuario(uu);
    	TareaModel.create(tarea);		
        return redirect(routes.TareaController.getTarea(tarea.titulo));
    }
    /**
	 * Action method para PUT /tareaTags/<texto>/<tag>
	 * @param texto= nombre tarea a modificar, tag= tag del tag a agregar a la tarea
	 */
    public Result anadirTag(String texto,String tag){
    	TagsModel t = TagsModel.findByNombre(tag);
    	if (t == null) {
			t=new TagsModel(tag);
			t.save();
		}
    	TareaModel tarea = TareaModel.findByNombre(texto);
		if (tarea == null) {
			return notFound();
		}
		t.addTarea(tarea);
    	TagsModel.create(t);		
        return redirect(routes.TareaController.listaTareasUsuario());
    }
    /**
	 * Action method para DELETE /tareas/:texto
	 * Se deben pasar los atributos a modificar en el body de la petición. 
	 * 
	 * @param titulo de la tarea
	 */
    @Security.Authenticated(secured.class)
    public Result deleteTarea(String texto){
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	TareaModel tt=u.tareaExistente(texto);
    	if (tt!=null){
    		u.tareasUsuario.remove(tt);
    		u.save();
    		tt.delete();
    		return ok("Tarea Borrada");
    	}else{
    		return unauthorized("no existe la tarea para el usuario logeado");
    	}
    }
    /*  public Result listaTareas(){
	Map<String,String> map= new HashMap<String,String>();
	String nombre=request().getQueryString("texto");
	if (nombre!=null){
		map.put("texto", nombre);
	}
	if (request().accepts("application/xml")) {
		return ok(views.xml.vistaTareas.render(TareaModel.findBy(map)));
	} else if (request().accepts("application/json")) {
		List<TareaJSON> tJsonArray = new ArrayList<TareaJSON>();
		for (TareaModel t:TareaModel.findBy(map)){
			TareaJSON tJson= new TareaJSON(t);
			tJsonArray.add(tJson);
		}
		JsonNode jn = play.libs.Json.toJson(tJsonArray);
		return ok(jn);
	} else {
		return badRequest("Unsupported format");
	}
}*/
    /*public Result anadirUsuarios(String texto){
	String name;
	JsonNode json = request().body().asJson();
	if (json == null) {
		return badRequest("Expecting Json data");
	} else {
		name = json.findPath("usuario").textValue();
	}
	if (name==null){
		
	}else{
		
	}
	UsuarioModel uu = UsuarioModel.findByNombre(usuario);
	if (usuario == null) {
		return notFound();
	}
	TareaModel tarea = TareaModel.findByNombre(texto);
	if (tarea == null) {
		return notFound();
	}
	tarea.addUsuario(uu);
	TareaModel.create(tarea);		
    return redirect(routes.TareaController.listaTareas());
}*/

}
