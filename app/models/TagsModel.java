package models;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;
@Entity
public class TagsModel extends Model{
	@Id
	public Long id;
}
