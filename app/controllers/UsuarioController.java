package controllers;

import helpers.ControllerHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.EmailModel;
import models.PasswordModel;
import models.TareaModel;
import models.UsuarioModel;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import util.secured;
@Api(value = "/user",consumes="application/xml,application/json",produces="application/xml,application/json")
public class UsuarioController extends Controller {
	
	public Result index() {
		return redirect(routes.UsuarioController.login());
	}
	/**
	 * Action method para POST /login.
	 * Se deben pasar nombre y un objeto pass en el body de la petición. 
	 * 
	 */
	@ApiOperation(nickname = "login",value = "logearse",notes = "Returns a token", httpMethod = "POST")
	@ApiResponses({
		     @ApiResponse(code = 400, message = "Invalid format"),
		     @ApiResponse(code = 200, message = "TOKEN"),
		     @ApiResponse(code = 404, message = "Usuario o password incorrecto")})
	@ApiImplicitParams({
		@ApiImplicitParam(required = true, dataType = "helpers.UsuarioJSON", paramType ="body")
	})
	public Result login(){
		ControllerHelper.headers(response());
		Form<UsuarioModel> form = Form.form(UsuarioModel.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		UsuarioModel uu = form.get();
		if (UsuarioModel.auth(uu.username, uu.getPass())){
			uu.generarToken();
			if (request().accepts("application/json")) {
				ObjectNode jn = play.libs.Json.newObject();
				jn.put("TOKEN", uu.TOKEN);
				
				return ok(jn);
			} 
			else if (request().accepts("application/xml")) {
				return ok(views.xml.vistaToken.render(uu));
			} else {
				String format=Messages.get("Formato");
				return badRequest(format);
			}
			
		}
		String auth=Messages.get("Login");
		return notFound(auth);
		//return redirect(routes.UsuarioController.login());
	}
	/**
	 * Action method para GET /logout.
	 * 
	 */
	@ApiOperation(nickname = "logout",value = "desconectarse",notes = "Borra el token del usuario", httpMethod = "GET")
	@ApiResponses({
	     @ApiResponse(code = 200, message = "Desconectado")})
	@ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
	@Security.Authenticated(secured.class)
	public Result logout(){
		ControllerHelper.headers(response());
		UsuarioModel ubd=(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		ubd.borrarToken();
		String logout=Messages.get("Logout");
		return ok(logout);
	}
	
	/**
	 * Action method para GET /user.
	 * 
	 */
	@ApiOperation(nickname = "getUser",value = "get usuario",notes = "muestra el usuario logeado",response=models.UsuarioModel.class, httpMethod = "GET")
	@ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid format"),
	     @ApiResponse(code = 200, message = "Usuario"),
	     @ApiResponse(code = 404, message = "Usuario no logeado ")})
	@ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
	@Security.Authenticated(secured.class)
	public Result getUser() {
			ControllerHelper.headers(response());
			UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
			if(u !=null){
				if (request().accepts("application/json")) {
					
					JsonNode jn = play.libs.Json.toJson(u);
					return ok(jn);
				}
				else if (request().accepts("application/xml")) {
				return ok(views.xml.vistaUsuario.render(u));
				}
				else {
				String format=Messages.get("Formato");
				return badRequest(format);
			}

		} else {
			String UserNoEncontrado=Messages.get("UserNoEncontrado",u);
			return notFound(UserNoEncontrado);
		}

	}
	
	
	//@Security.Authenticated(secured.class)
	/**
	 * Action method para GET /users/<pag>.
	 * Opcionalmente se puede pasar el parámetro size para indicar el tamaño de página.
	 * Opcionalmente se pueden pasar los parametros de nombre y edad para filtrar por esos valores
	 * Si no se ponen realizara una busqueda completa
	 * Si no se pasa tamaño de página, se usará 10
	 * 
	 * @param pag número de página a recuperar.
	 */
	@ApiOperation(nickname = "listaUsuarios",value = "get lista usuario",notes = "muestra la lista usuarios", httpMethod = "GET")
	@ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid format"),
	     @ApiResponse(code = 200, message = "Lista Usuarios")}) 
	@ApiImplicitParams({
			@ApiImplicitParam(name="nombre",value="nombre de los usuarios",required = true, dataType = "string", paramType ="query"),
			@ApiImplicitParam(name="edad",value="edad de los usuarios",required = true, dataType = "string", paramType ="query")
		})
	public Result listaUsuarios(Integer pag) {
		ControllerHelper.headers(response());
		String paginaSize = request().getQueryString("size");
		if (paginaSize == null) {
			paginaSize = "10";
		}
			  Map<String,String> map= new HashMap<String,String>();
				String nombre=request().getQueryString("nombre");
				String edad=request().getQueryString("edad");
				if (nombre!=null){
					map.put("nombre", nombre);
				}
				if (edad!=null){
					map.put("edad", edad);
				}
				Integer count = UsuarioModel.find.findRowCount();
				Integer size= Integer.valueOf(paginaSize);
				List<UsuarioModel> lu=UsuarioModel.findBy(map,pag,size);
				if (request().accepts("application/json")) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("count", count);
					result.put("lista", lu);
					return ok(Json.toJson(result));
				}
				else if (request().accepts("application/xml")) {
					return ok(views.xml.vistaUsuarios.render(lu,count));
				}   else {
					String format=Messages.get("Formato");
					return badRequest(format);
				}
	}
	/**
	 * Action method para POST /user.
	 * Se deben pasar los atributos del usuario en el body de la petición. 
	 * 
	 */
	@ApiOperation(nickname = "createUser",value = "crear usuario",notes = "crea un usuario", httpMethod = "POST")
	@ApiResponses({
	     @ApiResponse(code = 200, message = "Creacion correcta"),
	     @ApiResponse(code = 409, message = "Usuario ya existente")})
	@ApiImplicitParams({
		@ApiImplicitParam(required = true, dataType = "models.UsuarioModel", paramType ="body"),
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
	public Result createUser() {
		ControllerHelper.headers(response());
		Form<UsuarioModel> form = Form.form(UsuarioModel.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_user", form.errorsAsJson()));
		}
		
		UsuarioModel uu = form.get();
		if (!UsuarioModel.existe(uu.username)) {
			
			UsuarioModel.create(uu);
			String correcto=Messages.get("CreacionCorrecta");
			return ok(correcto);
		} else {
			return status(CONFLICT, "Usuario ya existente");
		}
	}
	/**
	 * Action method para PUT /user
	 * Se deben pasar los atributos a modificar en el body de la petición. 
	 * agregar emails nuevos, modificar pass,telefono y edad
	 * 
	 */
	@ApiOperation(nickname = "update",value = "actualiza usuario",notes = "actualiza el usuario logeado", httpMethod = "PUT")
	@ApiResponses({
	     @ApiResponse(code = 400, message = "Invalid format"),
	     @ApiResponse(code = 200, message = "Modificacion realizada"),
	     @ApiResponse(code = 304, message = "Sin modificaciones"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
	@ApiImplicitParams({
		@ApiImplicitParam(required = true, dataType = "models.UsuarioModel", paramType ="body"),
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
	@Security.Authenticated(secured.class)
	public Result update() {
		ControllerHelper.headers(response());
		UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			String UserNoLogeado=Messages.get("UserNoLogeado",usuario);
			return notFound(UserNoLogeado);
		}
		
		Form<UsuarioModel> form = Form.form(UsuarioModel.class).bindFromRequest();

		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
			}

		Result res;
		boolean email=false;
		UsuarioModel u=form.get();
		u.TOKEN=usuario.TOKEN;
		if (!u.emails.isEmpty()){
			for (EmailModel e:u.emails){
				if (!EmailModel.existe(e.email)){
					usuario.addEmail(e);
					email=true;
					
				}
			}
		}
		if (usuario.changeData(u) || email ) {
			usuario.update();
			String correcto=Messages.get("ModificacionCorrecta");
			res = ok(correcto);
		}
		else {
			res = status(NOT_MODIFIED);
		}
		
		return res;
	}
	/**
	 * Action method para DELETE /user
	 * 
	 */
	@ApiOperation(nickname = "delete",value = "borra usuario",notes = "borra el usuario logeado", httpMethod = "DELETE")
	@ApiResponses({
	     @ApiResponse(code = 200, message = "Borrado realizado"),
	     @ApiResponse(code = 404, message = "Usuario no logeado")})
	@ApiImplicitParams({
		@ApiImplicitParam(name="X-AUTH-TOKEN",value="token de logeo",required = true, dataType = "string", paramType ="header")
	})
	@Security.Authenticated(secured.class)
	public Result delete(){
		ControllerHelper.headers(response());
		UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			String UserNoLogeado=Messages.get("UserNoLogeado",usuario);
			return notFound(UserNoLogeado);
		}
		List<UsuarioModel>lista= new ArrayList<UsuarioModel>();
		List<TareaModel> listaTareas=TareaModel.findByUsuario(lista,usuario);
		String correcto=Messages.get("BorradoCorrecta");
		if (listaTareas!=null){
			List<TareaModel> tareasEliminar=new ArrayList<TareaModel>();
			List<String> tareasEliminarTexto=new ArrayList<String>();
			for (TareaModel t:listaTareas){
				if (t.usuarios.size()==1){
					tareasEliminar.add(t);
					tareasEliminarTexto.add(t.titulo);
				}else{
					t.usuarios.remove(usuario);
				}
			}
			usuario.delete();
			for (TareaModel tEliminar:tareasEliminar){
				tEliminar.delete();
			}
			
			return ok(correcto +" junto con las siguientes tareas " + tareasEliminarTexto);
		}else{
			usuario.delete();
		}
		return ok(correcto);
	}
}
