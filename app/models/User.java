package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.*;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	
    @Required
    public String name;
    
    @Required
    @Email
    public String email;
    
    @Required
    public String password;
    
    @Required
    public boolean isAdmin;
    
    @OneToMany(mappedBy="user", cascade=CascadeType.ALL) 
    public Set<Role> attachedRoles;
    
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + name;
    }

	public List<Project> getProjects() {
		List<Role> roles = Role.find("byUser", this).fetch();
		List<Project> result = new ArrayList<Project>();
		
		for (Role role : roles)
			result.add(role.project);
		
		return result;
	}

	public RoleType getRoleTypeFor(models.Project project) {
		Role currentRole = Role.find("byUserAndProject", this, project).first();
		if (currentRole != null)
			return currentRole.role;
		else
			return null;
	}
}
