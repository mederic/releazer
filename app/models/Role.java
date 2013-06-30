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

	@Required
    @ManyToOne
    public User user;

	@Required
    @ManyToOne
    public RoleType role;

	@Required
    @ManyToOne
    public Project project;

    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + user.name + " : " + role.toString() + " for " + project.name;
    }
}