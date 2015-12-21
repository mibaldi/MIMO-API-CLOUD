package models;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	@Valid
	@JsonManagedReference
	@Required
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
		usuario.save();
	}
	/*public String validate() {
		int i=0;
		ArrayList<String> lista=new ArrayList<String>();
		if (!emails.isEmpty()){
			String regex="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			for (EmailModel e:emails){
				if (!e.email.matches(regex)){
					i++;
					lista.add(e.email + " formato incorrecto");
				}else{
					if (EmailModel.existe(e.email)) {
						i++;
						lista.add(e.email + " email existente");
					}
				}
			}
		}
		if (!tareasUsuario.isEmpty()){
			for (TareaModel tarea:tareasUsuario){
				if (TareaModel.existe(tarea.getTitulo())) {
					i++;
					lista.add(tarea.titulo + " formato incorrecto");
		        }
			}
		}
		if (i==0){
			return null;
		}
		else return ("hay "+ Integer.toString(i) +" campos insertados incorrectamente : " + lista);
    }*/
	
	

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
			if (u.pass.getContrasenia().compareTo(pass.getContrasenia())==0){
				return true;
			}
			return false;
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
		
		if (newData.pass != null && !newData.pass.getContrasenia().equals(this.pass.getContrasenia())) {
			this.pass = newData.pass;
			changed = true;
		}
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
