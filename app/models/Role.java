package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Role extends Model {
	
	public enum RoleType {
	    DEVELOPER,
	    CUSTOMER
	}

	@Required
    public RoleType role;

	@Required
    @ManyToOne(cascade=CascadeType.ALL)
    public Project project;

	@Required
    @ManyToOne(cascade=CascadeType.ALL)
    public User user;
	
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + user.name + " : " + role.toString() + " for " + project.name;
    }
}
