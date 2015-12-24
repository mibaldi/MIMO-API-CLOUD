package models;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import org.junit.Test;

import play.data.Form;
import play.libs.Json;

public class TareaTest {
	@Test
	public void tituloEsRequerido() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			System.out.println("titulo es requerido");
			TareaModel tarea = new TareaModel();
			tarea.setDescripcion("descripcion tarea");
			
    			Form<TareaModel> form = Form.form(TareaModel.class).bind(Json.toJson(tarea));
    		
    			assertTrue(form.hasErrors());
    			assertEquals(1, form.field("titulo").errors().size());
    			assertTrue(form.field("descripcion").errors().isEmpty());
		});
    	}
	@Test
    public void guardarTarea() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			System.out.println("GuardarTarea");
			TareaModel tarea = insertTarea("el nombre");
			
			TareaModel tareaGuardada = TareaModel.find.byId(tarea.getId());
			
			assertEquals(tarea.getTitulo(), tareaGuardada.getTitulo());
		});
    	}
	
    
	@Test
    public void actualizaTarea() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			System.out.println("actualizarTarea");
			TareaModel tarea = insertTarea2("el nombre");

			TareaModel tareaGuardada = TareaModel.find.byId(tarea.getId());
			tareaGuardada.setDescripcion("el nombre 2");
			boolean changed = tarea.changeData(tareaGuardada);
			tarea.save();
			
			assertTrue(changed);
			
			TareaModel tareaActualizada = TareaModel.find.byId(tarea.getId());
			assertEquals(tareaGuardada.getTitulo(), tareaActualizada.getTitulo());
		});
    	}
	@Test
    public void existeTarea() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			System.out.println("existeTarea");
			TareaModel tarea = insertTarea("tarea1");
			boolean existe= TareaModel.existe("tarea1");
			boolean existe2= TareaModel.existe("tarea12");
			assertTrue(existe);
			assertFalse(existe2);
		});
    	}
	@Test
    public void borarTarea() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			System.out.println("borrar tarea");
			TareaModel tarea = insertTarea("el nombre");
			
			TareaModel tareaGuardada = TareaModel.find.byId(tarea.getId());
			tareaGuardada.delete();
			
			assertNull(TareaModel.find.byId(tarea.getId()));
		});
    	}
	private TareaModel insertTarea(String titulo) {
		TareaModel t = new TareaModel();
		t.setTitulo(titulo);
		t.save();
		
		return t;
    }
	private TareaModel insertTarea2(String descripcion) {
		TareaModel t = new TareaModel();
		t.setDescripcion(descripcion);
		t.save();
		
		return t;
    }
}
