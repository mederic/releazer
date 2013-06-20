package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Project extends Model implements Comparator<Release> {
    
	@Required
    public String name;
    
    @ManyToMany(mappedBy="attachedProjects", cascade=CascadeType.ALL) 
    public Set<User> attachedUsers; 

    @OneToMany(mappedBy="project", cascade=CascadeType.ALL)
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
    
    public boolean hasPublishedRelease() {
    	boolean result = false;
    	for (Release release : releases) {
    		if (release.isPublished) 
    			return true;
    	}
    	return result;
    }
    
    public boolean hasPlannedRelease() {
    	boolean result = false;
    	for (Release release : releases) {
    		if (!release.isPublished) 
    			return true;
    	}
    	return result;
    }

	@Override
	public int compare(Release o1, Release o2) {
		return o2.date.compareTo(o1.date);
	}
}
