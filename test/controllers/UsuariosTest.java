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
import models.UsuarioModel;
import org.junit.Test;
import static play.test.Helpers.contentAsString;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;



public class UsuariosTest {
	@Test
    public void login() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
	    	assertNotNull(login);
	    	assertEquals("application/json", login.contentType());
		});
    }
	@Test
    public void logout() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			Result logout = route(fakeRequest(GET, "/logout").header("X-AUTH-TOKEN", token).header("Accept", "application/xml"));
			assertNotNull(logout);
	    	assertEquals("text/plain", logout.contentType());
		});
    }
	@Test
    public void createUserJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			
	    	assertNotNull(create);
	    	assertEquals("text/plain", create.contentType());
		});
    }
	@Test
    public void getUserJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			Result result = route(fakeRequest(GET, "/user").header("X-AUTH-TOKEN", token));
	    	//System.out.println(contentAsString(result));
	    	assertNotNull(result);
	    	assertEquals("application/xml", result.contentType());
		});
    }
	@Test
    public void listaUsuariosJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			Result result = route(fakeRequest(GET, "/users/0").header("X-AUTH-TOKEN", token));
	    	//System.out.println(contentAsString(result));
	    	assertNotNull(result);
	    	assertEquals("application/xml", result.contentType());
		});
    }
	@Test
    public void updateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			UsuarioModel usuarioGuardado = UsuarioModel.findByUsername("mibawldi");
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			UsuarioModel u2= modificarUsuario("mikel",24);
			JsonNode jn3 = play.libs.Json.toJson(u2);	
			Result update = route(fakeRequest(PUT, "/user").header("Content-Type", "application/json").header("X-AUTH-TOKEN", token).bodyJson(jn3));
			UsuarioModel usuarioActualizado = UsuarioModel.findByUsername("mibawldi");
	    	//System.out.println(contentAsString(result));
			assertEquals(usuarioGuardado.getNombre(), usuarioActualizado.getNombre());
			assertEquals(usuarioGuardado.getNombre(), usuarioActualizado.getNombre());
	    	assertNotNull(update);
	    	assertEquals("application/json", update.contentType());
		});
    }
	@Test
    public void deleteJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u= insertUsuario("mibawldi", "12345d67");
			JsonNode jn = play.libs.Json.toJson(u);			
			Result create = route(fakeRequest(POST, "/user").header("Content-Type", "application/json").bodyJson(jn));
			Result login = route(fakeRequest(POST, "/login").header("Content-Type", "application/json").header("Accept", "application/json").bodyJson(jn));
			String texto=contentAsString(login);
			JsonNode jn2 = play.libs.Json.parse(texto);
			String token=jn2.path("TOKEN").asText();
			Result result = route(fakeRequest(DELETE, "/user").header("X-AUTH-TOKEN", token));
			UsuarioModel usuarioActualizado = UsuarioModel.findByUsername("mibawldi");
			assertNull(usuarioActualizado);
	    	//System.out.println(contentAsString(result));
	    	assertNotNull(result);
	    	assertEquals("text/plain", result.contentType());
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
	 private UsuarioModel modificarUsuario(String nombre, Integer edad) {
			UsuarioModel u = new UsuarioModel();
			u.setEdad(edad);
			u.setNombre(nombre);
			
			return u;
	    }
}
