package models;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.*;
import javax.validation.Valid;

import play.data.validation.Constraints.Required;
import validators.NumeroTelefono;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class UsuarioModel  extends Model{
	@Id
	public Long id;
	@Required
	public String username;
	public String nombre;
	public int edad;
	@NumeroTelefono
	public String telefono;
	@OneToOne(cascade=CascadeType.ALL)
	@JsonManagedReference
	@Required
	@Valid
	public PasswordModel pass;
	@JsonIgnore
	public String TOKEN="";
	@OneToMany(cascade=CascadeType.ALL,mappedBy="usuario")
	@JsonManagedReference
	@Valid
	public List<EmailModel> emails;
	@ManyToMany(cascade = CascadeType.ALL)
	@JsonManagedReference
	@Valid
	public List<TareaModel> tareasUsuario;
	public UsuarioModel(){}
	public static void create(UsuarioModel usuario) {
		usuario.pass.contrasenia=PasswordModel.hash(usuario.pass.contrasenia);
		usuario.save();
	}
	public String validate(){
		if (!emails.isEmpty()){
			 Map<Integer, EmailModel> mapemails = new HashMap<Integer, EmailModel>(emails.size());
			 for(EmailModel e : emails) {	 
				 mapemails.put(e.hashCode(), e);
			 }
			 emails.clear();
			 for(Entry<Integer, EmailModel> e2 : mapemails.entrySet()) {
				 emails.add(e2.getValue());
			 }
		}
		return null;
	}
	
	public static List<UsuarioModel> listaUserExistentes(String usuarios,UsuarioModel uLogin){
		
		List<UsuarioModel> listaUsuarios=new ArrayList<UsuarioModel>();
		listaUsuarios.add(uLogin);
		List<String> UsuariosNombre=Arrays.asList(usuarios.split("\\s*,\\s*"));
		for (String nombre:UsuariosNombre){
			UsuarioModel u = UsuarioModel.findByUsername(nombre);
			if (u!=null){
				listaUsuarios.add(u);
			}
		}
		return listaUsuarios;
	}

	public static final Find<Long, UsuarioModel> find = new Find<Long, UsuarioModel>() {
	};

	public static UsuarioModel findById(Long id) {
		return find.byId(id);
	}
	public static List<UsuarioModel> findBy(Map<String,String> map,Integer pagina, Integer size){
		ExpressionList<UsuarioModel> listaBuscada=find.where();
		for (String key:map.keySet()){
			listaBuscada.eq(key, map.get(key));
		}
		return listaBuscada.orderBy("id").setMaxRows(size).setFirstRow(pagina*size).findList();
		  
	}
	
	public static UsuarioModel findByUsername(String nombre) {
		if(find.where().eq("username", nombre).findList().size()==0){
			return null;
		}else{
			return find.where().eq("username", nombre).findList().get(0);
		}
		
	}
	public static List<UsuarioModel> all() {
		return find.all();
	}
	public static Boolean existe(String nombre){
		return UsuarioModel.findByUsername(nombre) != null;
	}
	public TareaModel tareaExistente(String nombre){
		TareaModel tarea= TareaModel.findByNombre(nombre);
		
		if(find.where().eq("username", this.username).eq("tareasUsuario",tarea).findList().size()==0){
			return null;
		}else{
			return tarea;
		}
	}
	
	public static Boolean auth(String username, PasswordModel pass){
		UsuarioModel u= findByUsername(username);
		if (u!=null){
			return PasswordModel.comprobar(pass.getContrasenia(),u.pass.getContrasenia());
		}
		return false;
	}
	public void addTarea(TareaModel tarea) {
			tareasUsuario.add(tarea);
		
	}
	public void addEmail(EmailModel email) {
		emails.add(email);
		email.setUsuario(this);
	}
	public boolean changeData(UsuarioModel newData) {
		boolean changed = false;
		
		if (newData.nombre != null) {
			this.nombre = newData.nombre;
			changed = true;
		}
		if (newData.telefono != null) {
			this.telefono = newData.telefono;
			changed = true;
		}
		if (newData.TOKEN != null && !newData.TOKEN.equals(this.TOKEN)) {
			this.TOKEN = newData.TOKEN;
			changed = true;
		}
		if (newData.edad != 0) {
			this.edad = newData.edad;
			changed = true;
		}
		
		return changed;
	}
	public void generarToken(){
		SecureRandom s= new SecureRandom();
		String tok=new BigInteger(130,s).toString(32);
		System.out.print(tok);
		this.TOKEN=tok;
		if (find.where().eq("TOKEN", tok).findList().size()==0){
			UsuarioModel uu= UsuarioModel.findByUsername(this.username);
			if (uu.changeData(this)){
				uu.update();
			}
		}
		else{
			generarToken();
		}
	}
	public void borrarToken(){
		UsuarioModel u= new UsuarioModel();
		u.TOKEN="";
		if (this.changeData(u)){
			this.update();
		}
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	public PasswordModel getPass() {
		return pass;
	}
	public void setPass(PasswordModel pass) {
		this.pass = pass;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username.trim();
	}
	
}
