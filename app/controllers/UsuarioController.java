package controllers;

import helpers.ControllerHelper;

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
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import scala.Option;
import util.secured;

public class UsuarioController extends Controller {
	
	public Result index() {
		return redirect(routes.UsuarioController.login());
	}
	/**
	 * Action method para POST /login.
	 * Se deben pasar nombre y un objeto pass en el body de la petición. 
	 * 
	 */
	public Result login(){
		Form<UsuarioModel> form = Form.form(UsuarioModel.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			System.out.println("erorrrrr");
			return badRequest(form.errorsAsJson());
		}
		UsuarioModel uu = form.get();
		if (UsuarioModel.auth(uu.username, uu.getPass())){
			uu.generarToken();
			if (request().accepts("application/xml")) {
				return ok(views.xml.vistaToken.render(uu));
			} else if (request().accepts("application/json")) {
				ObjectNode jn = play.libs.Json.newObject();
				jn.put("TOKEN", uu.TOKEN);
				return ok(jn);
			} else {
				return badRequest("Unsupported format");
			}
			
		}
		return notFound("No se encuentra un usuario con ese username o password");
		//return redirect(routes.UsuarioController.login());
	}
	/**
	 * Action method para GET /logout.
	 * 
	 */
	@Security.Authenticated(secured.class)
	public Result logout(){
		UsuarioModel ubd=(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		ubd.borrarToken();
		return ok("desconectado");
	}
	
	/**
	 * Action method para GET /user.
	 * 
	 */
	@Security.Authenticated(secured.class)
	public Result getUser() {
		
			UsuarioModel u=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
			if(u !=null){
			if (request().accepts("application/xml")) {
				return ok(views.xml.vistaUsuario.render(u));
			} else if (request().accepts("application/json")) {
				
				JsonNode jn = play.libs.Json.toJson(u);
				return ok(jn);
			} else {
				return badRequest("Unsupported format");
			}

		} else {
			return notFound("usuario no existe");
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
	public Result listaUsuarios(Integer pag) {
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
				
				if (request().accepts("application/xml")) {
					return ok(views.xml.vistaUsuarios.render(lu,count));
				} else if (request().accepts("application/json")) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("count", count);
					result.put("lista", lu);
					return ok(Json.toJson(result));
				} else {
					return badRequest("Unsupported format");
				}
	}
	/**
	 * Action method para POST /user.
	 * Se deben pasar los atributos del usuario en el body de la petición. 
	 * 
	 */
	public Result createUser() {
		Form<UsuarioModel> form = Form.form(UsuarioModel.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "invalid_user", form.errorsAsJson()));
		}
		
		UsuarioModel uu = form.get();
		if (!UsuarioModel.existe(uu.username)) {
			
			UsuarioModel.create(uu);
			return ok("correcto");
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
	@Security.Authenticated(secured.class)
	public Result update() {
		UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			return notFound();
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
			res = ok("Modificacion realizada");
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
	@Security.Authenticated(secured.class)
	public Result delete(){
		UsuarioModel usuario=	(UsuarioModel) Http.Context.current().args.get("usuario_logado");
		if (usuario == null) {
			return notFound();
		}
		List<UsuarioModel>lista= new ArrayList<UsuarioModel>();
		List<TareaModel> listaTareas=TareaModel.findByUsuario(lista,usuario);
		if (listaTareas!=null){
			List<TareaModel> tareasEliminar=new ArrayList<TareaModel>();
			for (TareaModel t:listaTareas){
				if (t.usuarios.size()==1){
					tareasEliminar.add(t);
				}else{
					t.usuarios.remove(usuario);
				}
			}
			usuario.delete();
			for (TareaModel tEliminar:tareasEliminar){
				tEliminar.delete();
			}
			return ok("Usuario Borrado junto con las siguientes tareas " + tareasEliminar);
		}else{
			usuario.delete();
		}
		
		
		
		return ok("Usuario Borrado");
	}
	
	/*public Result delete(String nombre,String format) {
	RequestBody body = request().body();
	System.out.println(format);
	if (format.compareTo("plain") == 0) {
		String textBody = body.asText();
		 if(textBody != null) {
			   return ok("Got: " + textBody);
			  } else {
			    return badRequest("Expecting text/plain request body");
			  }
	} else if (format.compareTo("json") == 0) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data");
		} else {
			String name = json.findPath("S").textValue();
			if (name == null) {
				return badRequest("Missing parameter [name]");
			} else {
				return ok("Hello " + name);
			}
		}
	}else{
		return badRequest("Unsupported format: use format=plain or format=json");
	}

	// System.out.println("borrado "+nombre);
	// return redirect(routes.Application.index());
}*/
}
