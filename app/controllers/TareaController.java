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
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.*;
import util.secured;

public class TareaController extends Controller {

    public Result index() {
        return ok("hola");
    }
    /**
	 * Action method para GET /tareas/<texto>.
	 * 
	 * @param titulo de tarea a recuperar.
	 */
    @Security.Authenticated(secured.class)
    public Result getTarea(String texto){
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tt=u.tareaExistente(texto);
    	if (tt!=null){
    		if (request().accepts("application/xml")) {
    			return ok(views.xml.vistaTarea.render(tt));
    		}else if (request().accepts("application/json")) {
    			
    			TareaJSON result =new TareaJSON(tt);
    			return ok(Json.toJson(result));
    		}
    		else {
    			String format=Messages.get("Formato");
				return badRequest(format);
    		}
    	}
    		return unauthorized("no existe la tarea para el usuario logeado");
    }
    /**
	 * Action method para GET /tareas
	 */
    @Security.Authenticated(secured.class)
    public Result listaTareasUsuario(){
    	
    	UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			String UserNoLogeado=Messages.get("UserNoLogeado",usuario);
			return notFound(UserNoLogeado);
		}
		List<UsuarioModel> listaUsuarios = new ArrayList<UsuarioModel>();
		String usuarios=request().getQueryString("users");
		if (usuarios!=null){
			listaUsuarios=UsuarioModel.listaUserExistentes(usuarios, usuario);
		}
		Integer count = TareaModel.numeroTareas(usuario);
    	if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByUsuario(listaUsuarios, usuario),count));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaJSON.convertir(TareaModel.findByUsuario(listaUsuarios,usuario)));
			return ok(jn);
		} else {
			String format=Messages.get("Formato");
			return badRequest(format);
		}
    }
    /**
	 * Action method para GET /tareasTag
	 * @param tag nombre del tag dentro de las tareas.
	 */
    @Security.Authenticated(secured.class)
    public Result listaTareasTag(){
    	UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			String UserNoLogeado=Messages.get("UserNoLogeado",usuario);
			return notFound(UserNoLogeado);
		}
		String tags=request().getQueryString("tags");
		List<TagsModel> listaTags=new ArrayList<TagsModel>();
		if (tags!=null){
			listaTags=TagsModel.listaTagsExistentes(tags);
		}
		Integer count = TareaModel.numeroTareas(usuario);
    	if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByTag(listaTags,usuario),count));
		} else if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaJSON.convertir(TareaModel.findByTag(listaTags,usuario)));
			return ok(jn);
		} else {
			String format=Messages.get("Formato");
			return badRequest(format);
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
    		String UserNoLogeado=Messages.get("UserNoLogeado",usuario);
			return notFound(UserNoLogeado);
    	}
    	Form<TareaModel> form = Form.form(TareaModel.class).bindFromRequest();
    	if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_tarea", form.errorsAsJson()));
		}
    	TareaModel tt=form.get();
    	tt.addUsuario(usuario);
    	TareaModel.create(tt);
    	String correcto=Messages.get("CreacionCorrecta");
    	return ok(correcto);
        //return redirect(routes.TareaController.listaTareasUsuario());
    }
    /**
	 * Action method para PUT /tareas/<nombre>
	 * Se deben pasar los atributos a modificar en el body de la petición. 
	 * @param nombre titulo de la tarea a modificar.
	 */
    @Security.Authenticated(secured.class)
    public Result updateTarea(String texto){
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tarea=u.tareaExistente(texto);
    	if (tarea==null){
    		return notFound();
    	}
    	
		Form<TareaModel> form = Form.form(TareaModel.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_tarea", form.errorsAsJson()));
		}
		Result res;
		if (tarea.changeData(form.get())) {
			tarea.update();
			String correcto=Messages.get("ModificacionCorrecta");
			res = ok(correcto);
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
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	UsuarioModel uu = UsuarioModel.findByUsername(usuario);
    	if (uu == null) {
    		String UserNoEncontrado=Messages.get("UserNoEncontrado",uu);
			return notFound(UserNoEncontrado);
		}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tarea=u.tareaExistente(texto);
		if (tarea == null) {
			return notFound("Tarea no valida");
		}
		tarea.añadirUsuario(uu);
		return ok("usuario añadido");
       // return redirect(routes.TareaController.getTarea(tarea.titulo));
    }
    /**
	 * Action method para PUT /tareaTags/<texto>/<tag>
	 * @param texto= nombre tarea a modificar, tag= tag del tag a agregar a la tarea
	 */
    @Security.Authenticated(secured.class)
    public Result anadirTag(String texto,String tag){
    	TagsModel t = TagsModel.findByNombre(tag);
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	if (t == null) {
			t=TagsModel.crearTag(tag);
		}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tarea=u.tareaExistente(texto);
		if (tarea == null) {
			return notFound("Tarea no valida");
		}
		t.añadirTarea(tarea);	
		return ok("tag añadido");
        //return redirect(routes.TareaController.listaTareasUsuario());
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
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tt=u.tareaExistente(texto);
    	if (tt!=null){
    		TareaModel.borrarTarea(u, tt);
    		String correcto=Messages.get("BorradoCorrecta");
    		return ok(correcto);
    	}else{
    		return unauthorized("no existe la tarea para el usuario logeado");
    	}
    }

}
