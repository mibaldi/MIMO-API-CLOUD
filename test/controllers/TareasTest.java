package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.PUT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.route;
import static play.test.Helpers.running;
import models.PasswordModel;
import models.TareaModel;
import models.UsuarioModel;

import org.junit.Test;

import static play.test.Helpers.contentAsString;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Result;



public class TareasTest {
	@Test
    public void createTareaJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			//TareaModel t= crearTarea("tarea1","descripcion tarea 1");
			JsonNode jnt = crearTarea("tarea1","descripcion tarea 1");
			//System.out.println(jnt);
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			//System.out.println(contentAsString(createTarea));
	    	assertNotNull(createTarea);
	    	//assertEquals("application/xml", createTarea.contentType());
		});
    }
	@Test
    public void getTareaJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1","descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			Result result = route(fakeRequest(GET, "/tareas/tarea1").header("X-AUTH-TOKEN", token));
	    	//System.out.println(contentAsString(result));
			
	    	assertNotNull(result);
	    	assertEquals("application/xml", result.contentType());
		});
    }
	
	@Test
    public void listaTareasJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1","descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			Result result = route(fakeRequest(GET, "/tareas").header("X-AUTH-TOKEN", token));
	    	//System.out.println(contentAsString(result));
	    	assertNotNull(result);
	    	assertEquals("application/xml", result.contentType());
		});
    }
	@Test
    public void listaTareasTagsJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1","descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			Result result = route(fakeRequest(GET, "/tareasTag").header("X-AUTH-TOKEN", token));
	    	//System.out.println(contentAsString(result));
	    	assertNotNull(result);
	    	assertEquals("application/xml", result.contentType());
		});
    }
	
	@Test
    public void updateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			UsuarioModel usuarioGuardado = UsuarioModel.findByUsername("mibaldi");
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1", "descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			JsonNode jntModificada = crearTarea("tarea1", "descripcion tarea 2");
			Result updateTarea = route(fakeRequest(PUT, "/tareas/tarea1").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jntModificada));
				
			//System.out.println(contentAsString(result));
			//assertEquals(usuarioGuardado.getNombre(), usuarioActualizado.getNombre());
			//assertEquals(usuarioGuardado.getNombre(), usuarioActualizado.getNombre());
	    	assertNotNull(updateTarea);
	    	assertEquals("application/json", updateTarea.contentType());
		});
    }
	@Test
    public void anadirUsuarioaTareaJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			UsuarioModel u2= insertUsuario("mibaldi2", "12345d67");
			jn = play.libs.Json.toJson(u2);	
			route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1", "descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			Result anadirUsuario = route(fakeRequest(PUT, "/tareaUsuario/tarea1/mibaldi2").header("X-AUTH-TOKEN", token));			
			System.out.println(contentAsString(anadirUsuario));
	    	assertNotNull(anadirUsuario);
		});
    }
	@Test
    public void anadirTagaTareaJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1", "descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			Result añadirTag = route(fakeRequest(PUT, "/tareaTags/tarea1/tagCompras").header("X-AUTH-TOKEN", token));	
			Result result = route(fakeRequest(GET, "/tareas").header("X-AUTH-TOKEN", token));
			System.out.println(contentAsString(result));
	    	assertNotNull(añadirTag);
		});
    }
	
	@Test
    public void deleteJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibaldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			JsonNode jnt = crearTarea("tarea1", "descripcion tarea 1");
			Result createTarea = route(fakeRequest(POST, "/tarea").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json").bodyJson(jnt));
			Result deleteTarea = route(fakeRequest(DELETE, "/tareas/tarea1").header("X-AUTH-TOKEN", token).header("Content-Type", "application/json"));
	    	//System.out.println(contentAsString(result));
	    	assertNotNull(deleteTarea);
	    	assertEquals("text/plain", deleteTarea.contentType());
		});
    }
	
	
	 private UsuarioModel insertUsuario(String username, String pass) {
			UsuarioModel u = new UsuarioModel();
			u.setUsername(username);
			PasswordModel p= new PasswordModel();
			p.setContrasenia(pass);
			u.setPass(p);
			
			return u;
	    }
	 private JsonNode crearTarea(String titulo, String descripcion) {
		 JsonNode jnt = play.libs.Json.parse("{\"titulo\":\""+titulo+"\",\"descripcion\":\""+descripcion+"\"}");
			return jnt;
	    }
	 /*private TareaModel modificarTarea(String descripcion) {
			TareaModel t= new TareaModel();
			t.setDescripcion(descripcion);
			return t;
	    }*/
	 private UsuarioModel modificarUsuario(String nombre, Integer edad) {
			UsuarioModel u = new UsuarioModel();
			u.setEdad(edad);
			u.setNombre(nombre);
			
			return u;
	    }
}
