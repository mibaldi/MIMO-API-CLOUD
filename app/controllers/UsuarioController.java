package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import models.UsuarioModel;
import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import scala.Option;

public class UsuarioController extends Controller {
	
	public Result index() {
		return ok("hola");
	}

	public Result getUser(String nombre) {
		if (UsuarioModel.existe(nombre)) {
			
			UsuarioModel uu = UsuarioModel.findByNombre(nombre);
			if (request().accepts("application/xml")) {
				return ok(views.xml.vistaUsuario.render(uu));
			} else if (request().accepts("application/json")) {
				JsonNode jn = play.libs.Json.toJson(uu);
				return ok(jn);
			} else {
				return badRequest("Unsupported format");
			}

		} else {
			return notFound("usuario no existe");
		}

	}

	public Result listaUsuarios() {
		Map<String,String> map= new HashMap<String,String>();
		String nombre=request().getQueryString("nombre");
		String edad=request().getQueryString("edad");
		if (nombre!=null){
			map.put("nombre", nombre);
		}
		if (edad!=null){
			map.put("edad", edad);
		}
		
		if (request().accepts("application/xml")) {
			return ok(views.xml.vistaUsuarios.render(UsuarioModel.findBy(map)));
		} else if (request().accepts("application/json")) {
			
			
			JsonNode jn = play.libs.Json.toJson(UsuarioModel.findBy(map));
			return ok(jn);
		} else {
			return badRequest("Unsupported format");
		}
	}

	public Result createUser() {
		Form<UsuarioModel> form = Form.form(UsuarioModel.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			System.out.println("erorrrrr");
			return badRequest(form.errorsAsJson());
		}
		UsuarioModel uu = form.get();
		System.out.println(uu.nombre);
		UsuarioModel.create(uu);
		return redirect(routes.UsuarioController.listaUsuarios());
	}

	public Result update(String nombre) {
		UsuarioModel usuario = UsuarioModel.findByNombre(nombre);
		if (usuario == null) {
			return notFound();
		}
		
		Form<UsuarioModel> form = Form.form(UsuarioModel.class).bindFromRequest();

		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
			}

		Result res;

		if (usuario.changeData(form.get())) {
			usuario.save();
			res = ok();
		}
		else {
			res = status(NOT_MODIFIED);
		}
		
		return res;
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
	public Result delete(String nombre){
		UsuarioModel usuario = UsuarioModel.findByNombre(nombre);
		if (usuario == null) {
			return notFound();
		}
		usuario.delete();
		return ok();
	}
}
