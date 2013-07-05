package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import controllers.Security;
import controllers.ws.WSSecurity;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Project extends Model implements Comparator<Release> {

    @Required
    public String name;
    
    public Blob logo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    public Set<Role> attachedRoles;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    public Set<Release> releases;

    @Override
    public String toString() {
        return "[" + this.getId() + "] " + name;
    }

    public List<Release> getOrderedReleases() {
        List<Release> result = new ArrayList<Release>(this.releases);
        Collections.sort(result, this);
        return result;
    }

    public List<User> getUsers() {
        List<Role> roles = Role.find("byProject", this).fetch();
        List<User> result = new ArrayList<User>();

        for (Role role : roles) {
            result.add(role.user);
        }

        return result;
    }
    
    public List<User> getUsersToNotify() {
        List<Role> roles = Role.find("byProject", this).fetch();
        List<User> result = new ArrayList<User>();

        for (Role role : roles) {
            if (role.role.isNotifyWhenReleased)
                result.add(role.user);
        }

        return result;
    }

    public boolean hasPublishedRelease() {
        boolean result = false;
        for (Release release : releases) {
            if (release.isPublished) {
                return true;
            }
        }
        return result;
    }

    public boolean hasPlannedRelease() {
        boolean result = false;
        for (Release release : releases) {
            if (!release.isPublished) {
                return true;
            }
        }
        return result;
    }

    public Release getLastRelease() {
        models.Release lastRelease = models.Release.find("project = ? and isPublished = ? order by date desc", this, true).first();
        return lastRelease;
    }
    
    @Override
    public int compare(Release o1, Release o2) {
        return o2.date.compareTo(o1.date);
    }

	public HashMap toHashMap(User user) {
		HashMap result = new HashMap<String, Object>();
		result.put("id", this.id);
		result.put("name", this.name);
		
		if (this.logo.exists())
			result.put("urlLogo", "/project/" + this.id + "/logo");
		
		List<HashMap> releases = new ArrayList<HashMap>();
		for (Release release : this.releases) {
			if (WSSecurity.isAuthorizedFor(user, release) && !release.isArchived) {
				if (release.isPublished) {
					releases.add(release.toHashMap(user));
				} else {
			        Role role = Role.find("byUserAndProject", user, this).first();
			        if (role.role.canReadPlannedRelease)
						releases.add(release.toHashMap(user));
				}
			}
		}
		result.put("releases", releases);
		
		return result;
	}
}
