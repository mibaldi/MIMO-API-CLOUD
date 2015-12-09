package controllers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.TagsModel;
import models.TareaModel;
import models.UsuarioModel;
import play.*;
import play.data.Form;
import play.mvc.*;

public class TareaController extends Controller {

    public Result index() {
        return ok("hola");
    }
    public Result getTarea(String texto){
    	if(TareaModel.existe(texto)){
    		TareaModel tt =TareaModel.findByNombre(texto);
    		if (request().accepts("application/xml")) {
    			return ok(views.xml.vistaTarea.render(tt));
    		}else if (request().accepts("application/json")) {
    			List<String>lista=new ArrayList<String>();
    			List<String>lista2=new ArrayList<String>();
    			for (UsuarioModel user:tt.usuarios){
    				lista.add(user.getNombre());
    			}
    			for (TagsModel t:tt.tagsTarea){
    				lista2.add(t.tag);
    			}
    			TareaModel.TareaJSON tj=new TareaModel.TareaJSON(tt.texto,lista2, lista);
    			JsonNode jn = play.libs.Json.toJson(tj);
    			return ok(jn);
    		}
    		else {
    			return badRequest("Unsupported format");
    		}
        	
    	}else{
    		return notFound("usuario no existe");
    	}
    	
    }
    public Result listaTareasUsuario(String nombre){
    	UsuarioModel usuario = UsuarioModel.findByNombre(nombre);
		if (usuario == null) {
			return notFound();
		}
    	if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByUsuario(usuario)));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaModel.findByUsuario(usuario));
			return ok(jn);
		} else {
			return badRequest("Unsupported format");
		}
    }
    public Result listaTareasTag(String nombre){
    	TagsModel tag = TagsModel.findByNombre(nombre);
		if (tag == null) {
			return notFound();
		}
    	if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByTag(tag)));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaModel.findByTag(tag));
			return ok(jn);
		} else {
			return badRequest("Unsupported format");
		}
    }
    public Result listaTareas(){
    	
		if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.all()));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaModel.all());
			return ok(jn);
		} else {
			return badRequest("Unsupported format");
		}
    }
    public Result createTarea(String nombre) {
    	
    	Form<TareaModel> form = Form.form(TareaModel.class).bindFromRequest();
    	if (form.hasErrors()) {
			System.out.println("erorrrrr");
			return badRequest(form.errorsAsJson());
		}
    	UsuarioModel usuario = UsuarioModel.findByNombre(nombre);
		if (usuario == null) {
			return notFound();
		}
    	TareaModel tt=form.get();
    	System.out.println(tt.texto);
    	tt.addUsuario(usuario);
    	TareaModel.create(tt);		
        return redirect(routes.TareaController.listaTareas());
    }
    public Result updateTarea(String texto){
    	TareaModel tarea = TareaModel.findByNombre(texto);
		if (tarea == null) {
			return notFound();
		}
		
		Form<TareaModel> form = Form.form(TareaModel.class).bindFromRequest();

		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}

		Result res;

		if (tarea.changeData(form.get())) {
			TareaModel.create(tarea);
			res = ok();
		}
		else {
			res = status(NOT_MODIFIED);
		}
		
		return res;
    }
    public Result anadirUsuario(String texto,String usuario){
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
    }
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
        return redirect(routes.TareaController.listaTareas());
    }
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
    public Result deleteTarea(String texto){
    	System.out.println("borrado "+texto);
    	return redirect(routes.Application.index());
    }

}
