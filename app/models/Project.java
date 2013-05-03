package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Project extends Model {
    
	@Required
    public String name;
    
    @ManyToMany(mappedBy="attachedProjects") 
    public Set<User> attachedUsers; 

    @OneToMany(mappedBy="project", cascade=CascadeType.ALL)
    public Set<Release> releases;
    
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + name;
    }
}
