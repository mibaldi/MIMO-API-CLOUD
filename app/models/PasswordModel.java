package models;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import util.passwordInter;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
public class PasswordModel extends Model {
	@JsonIgnore
	@Id
	public Long id;
	@passwordInter
	public String contrasenia = "";
	@OneToOne(mappedBy="pass")
	@JsonBackReference
	public UsuarioModel usuario;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
	public static String hash(String contrasenia){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] b = md.digest(contrasenia.getBytes()); 
			int size = b.length; 
			StringBuffer h = new StringBuffer(size); 
			for (int i = 0; i < size; i++) { 
			int u =  b[i] & 255;
			if (u<16){ 
				h.append("0"+Integer.toHexString(u)); 
			}
			else { 
				h.append(Integer.toHexString(u)); 
				} 
			}
			return h.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		} 
	}
	public static Boolean comprobar(String contrasenia,String contraseniaBD){
		if(hash(contrasenia).compareTo(contraseniaBD)==0){
			return true;
		}
		return false;
	}
}
