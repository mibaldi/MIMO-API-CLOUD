package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import play.data.Form;
import play.libs.Json;

public class UsuarioTest {
	@Test
    public void usernameEsRequerido() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel usuario = new UsuarioModel();
			usuario.setNombre("losapellidos");
			
    			Form<UsuarioModel> form = Form.form(UsuarioModel.class).bind(Json.toJson(usuario));
    		
    			assertTrue(form.hasErrors());
    			assertEquals(1, form.field("username").errors().size());
    			assertTrue(form.field("nombre").errors().isEmpty());
		});
    	}
	@Test
    public void passwordEsRequerido() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel usuario = new UsuarioModel();
			usuario.setNombre("el nombre");
			
    			Form<UsuarioModel> form = Form.form(UsuarioModel.class).bind(Json.toJson(usuario));

    			assertTrue(form.hasErrors());
    			form.field("nombre").errors().size();
    			assertEquals(1,form.field("pass").errors().size());
		});
    	}
    
    @Test
    public void guardaUsuario() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel usuario = insertUsuario("el nombre", "12345678");
			
			UsuarioModel usuarioGuardado = UsuarioModel.find.byId(usuario.getId());
			
			assertEquals(usuario.getUsername(), usuarioGuardado.getUsername());
			assertEquals(usuario.getPass().getContrasenia(), usuarioGuardado.getPass().getContrasenia());
		});
    	}
    
    @Test
    public void borraUsuario() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel usuario = insertUsuario("el nombre", "12345678");
			
			UsuarioModel usuarioGuardado = UsuarioModel.find.byId(usuario.getId());
			usuarioGuardado.delete();
			
			assertNull(UsuarioModel.find.byId(usuario.getId()));
		});
    	}

    @Test
    public void actualizaUsuario() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel usuario = insertUsuario("el nombre", "12345678");

			UsuarioModel usuarioGuardado = UsuarioModel.find.byId(usuario.getId());
			usuarioGuardado.setEdad(25);
			usuarioGuardado.setNombre("mikel");
			boolean changed = usuario.changeData(usuarioGuardado);
			usuario.save();
			
			assertTrue(changed);
			
			UsuarioModel usuarioActualizado = UsuarioModel.find.byId(usuario.getId());
			assertEquals(usuarioGuardado.getNombre(), usuarioActualizado.getNombre());
			assertEquals(usuarioGuardado.getEdad(), usuarioActualizado.getEdad());
		});
    	}

    @Test
    public void findPagina() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			UsuarioModel u1 = insertUsuario("n1", "n2");
			UsuarioModel u2 = insertUsuario("n2", "n2");
			UsuarioModel u3 = insertUsuario("n3", "n3");
			Map<String,String> map= new HashMap<String,String>();
			// Primera página de 1 registro
				List<UsuarioModel> pagina = UsuarioModel.findBy(map, 0, 1);
				assertEquals(1, pagina.size());
				assertEquals(u1.getId(), pagina.get(0).getId());
 
				// Primera página de 2 registros 
			pagina = UsuarioModel.findBy(map,0, 2);
			assertEquals(2, pagina.size());
			assertEquals(u1.getId(), pagina.get(0).getId());
			assertEquals(u2.getId(), pagina.get(1).getId());

			// Segunda página de 2 registros
			pagina = UsuarioModel.findBy(map,1, 2);
			assertEquals(1, pagina.size());
			assertEquals(u3.getId(), pagina.get(0).getId());
		});
    	}
    
    
    private UsuarioModel insertUsuario(String username, String pass) {
		UsuarioModel u = new UsuarioModel();
		u.setUsername(username);
		PasswordModel p= new PasswordModel();
		p.setContrasenia(pass);
		u.setPass(p);
		u.save();
		
		return u;
    }
}
