package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.*;
import play.db.jpa.Model;

@Entity
public class User extends Model {

    @Required
    @CheckWith(UniqueUsernameCheck.class)
    @Column(name = "name", unique = true)
    public String username;
    
    @Required
    @Email
    public String email;
    @Required
    public String password;
    @Required
    public boolean isAdmin;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public Set<Role> attachedRoles;

    @Override
    public String toString() {
        return "[" + this.getId() + "] " + username;
    }

    public List<Project> getProjects() {
        List<Role> roles = Role.find("byUser", this).fetch();
        List<Project> result = new ArrayList<Project>();

        for (Role role : roles) {
            result.add(role.project);
        }

        return result;
    }

    public RoleType getRoleTypeFor(models.Project project) {
        Role currentRole = Role.find("byUserAndProject", this, project).first();
        if (currentRole != null) {
            return currentRole.role;
        } else {
            return null;
        }
    }

    static class UniqueUsernameCheck extends Check {

        public boolean isSatisfied(Object userObject, Object username) {
            setMessage("Username already exists...");
            User user = (User) userObject;
            User existingUser = User.find("byUsername", username).first();
            return ((existingUser == null) || (existingUser.id == user.id));
        }
    }
}
