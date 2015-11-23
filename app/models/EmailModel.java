package models;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;
@Entity
public class EmailModel extends Model{
	@Id
	public Long id;
}
