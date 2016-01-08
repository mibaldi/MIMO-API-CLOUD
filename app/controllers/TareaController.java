package controllers;

import helpers.ControllerHelper;
import helpers.TareaJSON;
import io.swagger.annotations.*;

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
@Api(value = "/tarea",consumes="application/xml,application/json",produces="application/xml,application/json")
public class TareaController extends Controller {

    public Result index() {
        return ok("hola");
    }
    /**
	 * Action method para GET /tareas/<texto>.
	 * 
	 * @param titulo de tarea a recuperar.
	 */
    @ApiOperation(nickname = "getTarea",value = "get tarea",notes = "muestra la tarea del usuario logeado, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN",response=models.TareaModel.class, httpMethod = "GET")
    @ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid format"),
	     @ApiResponse(code = 200, message = "Tarea"),
	     @ApiResponse(code = 401, message = "No existe la tarea para el usuario logeado"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})@Security.Authenticated(secured.class)
    public Result getTarea(@ApiParam(value = "titulo de la tarea") String texto){
    	ControllerHelper.headers(response());
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tt=u.tareaExistente(texto);
    	if (tt!=null){
    		if (request().accepts("application/json")) {
    			TareaJSON result =new TareaJSON(tt);
    			return ok(Json.toJson(result));
    		}
    		else if (request().accepts("application/xml")) {
    			return ok(views.xml.vistaTarea.render(tt));
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
    @ApiOperation(nickname = "listaTareasUsuarios",value = "get lista tareas usuarios",notes = "muestra la lista de tareas de usuarios, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "GET")
    @ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid format"),
	     @ApiResponse(code = 200, message = "Lista Tareas"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(name="users",value="username de los usuarios",required = true, dataType = "array", paramType ="query"),
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result listaTareasUsuario(){
    	ControllerHelper.headers(response());
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
		if (request().accepts("application/json")) {
			List<TareaJSON>lu=TareaJSON.convertir(TareaModel.findByUsuario(listaUsuarios,usuario));
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("numeroTareasUsuario", count);
			result.put("lista", lu);
			JsonNode jn = play.libs.Json.toJson(result);
			return ok(jn);
		}
		else if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByUsuario(listaUsuarios, usuario),count));
		}  else {
			String format=Messages.get("Formato");
			return badRequest(format);
		}
    }
    /**
	 * Action method para GET /tareasTag
	 * @param tags nombre del tag dentro de las tareas.
	 */
    @ApiOperation(nickname = "listaTareasTag",value = "get lista tareas tag",notes = "muestra la lista de tareas con los tags buscados, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "GET")
    @ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid format"),
	     @ApiResponse(code = 200, message = "Lista Tareas por tag"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(name="tags",value="tags",required = true, dataType = "array", paramType ="query"),
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result listaTareasTag(){
    	ControllerHelper.headers(response());
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
		if (request().accepts("application/json")) {
			JsonNode jn = play.libs.Json.toJson(TareaJSON.convertir(TareaModel.findByTag(listaTags,usuario)));
			return ok(jn);
		}
		else if (request().accepts("application/xml")) {
			return ok(views.xml.vistaTareas.render(TareaModel.findByTag(listaTags,usuario),count));
		}  else {
			String format=Messages.get("Formato");
			return badRequest(format);
		}
    }
  
    /**
	 * Action method para POST /tarea.
	 * Se deben pasar los atributos de la tarea en el body de la petición. 
	 * 
	 */
    @ApiOperation(nickname = "createTarea",value = "crear tarea",notes = "crea una tarea y la asocia al usuario logeado, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "POST")
    @ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid tarea"),
	     @ApiResponse(code = 200, message = "Creacion correcta"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(required = true, dataType = "helpers.TareaJSONswagger", paramType ="body"),
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result createTarea() {
    	ControllerHelper.headers(response());
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
    @ApiOperation(nickname = "update",value = "modificar tarea",notes = "modifica una tarea existente del usuario, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "POST")
    @ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid tarea"),
	     @ApiResponse(code = 200, message = "Modificacion realizada"),
	     @ApiResponse(code = 401, message = "No existe la tarea para el usuario logeado"),
	     @ApiResponse(code = 304, message = "Sin modificaciones"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(required = true, dataType = "helpers.TareaJSONswagger", paramType ="body"),
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result updateTarea(@ApiParam(value = "titulo de la tarea")String texto){
    	ControllerHelper.headers(response());
    	UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
    	if (u==null){
    		String UserNoLogeado=Messages.get("UserNoLogeado",u);
			return notFound(UserNoLogeado);
    	}
    	texto=texto.replaceAll("%20", " ");
    	TareaModel tarea=u.tareaExistente(texto);
    	if (tarea==null){
    		return unauthorized("no existe la tarea para el usuario logeado");
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
    @ApiOperation(nickname = "añadirUsuario",value = "añade usuario a tarea",notes = "Añade un usuario a una tarea existente del usuario logeado, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "POST")
    @ApiResponses({
	     @ApiResponse(code = 200, message = "Usuario añadido"),
	     @ApiResponse(code = 401, message = "No existe la tarea para el usuario logeado"),
	     @ApiResponse(code = 404, message = "Usuario no encontrado"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result anadirUsuario(String texto,String usuario){
    	ControllerHelper.headers(response());
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
			return unauthorized("no existe la tarea para el usuario logeado");
		}
		tarea.añadirUsuario(uu);
		return ok("usuario añadido");
       // return redirect(routes.TareaController.getTarea(tarea.titulo));
    }
    /**
	 * Action method para PUT /tareaTags/<texto>/<tag>
	 * @param texto= nombre tarea a modificar, tag= tag del tag a agregar a la tarea
	 */
    @ApiOperation(nickname = "añadirTag",value = "añade tag a tarea",notes = "Añade un tag a una tarea existente del usuario logeado, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "POST")
    @ApiResponses({
	     @ApiResponse(code = 200, message = "Tag añadido"),
	     @ApiResponse(code = 401, message = "No existe la tarea para el usuario logeado"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result anadirTag(String texto,String tag){
    	ControllerHelper.headers(response());
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
			return unauthorized("no existe la tarea para el usuario logeado");
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
    @ApiOperation(nickname = "borrarTarea",value = "borrar tarea",notes = "borra la tarea del usuario logeado, Se necesita estar logeado mediante el token en la cabecera X-AUTH-TOKEN", httpMethod = "POST")
    @ApiResponses({
	     @ApiResponse(code = 200, message = "Usuario añadido"),
	     @ApiResponse(code = 401, message = "No existe la tarea para el usuario logeado"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
    @ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
    @Security.Authenticated(secured.class)
    public Result deleteTarea(@ApiParam(value = "titulo de la tarea")String texto){
    	ControllerHelper.headers(response());
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
