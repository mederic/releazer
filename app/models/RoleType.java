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
public class RoleType extends Model {	

	@Required
    public String name;

	@Required
    public boolean canCreatePlannedRelease;

	@Required
    public boolean canReadPlannedRelease;
	
	@Required
    public boolean canWritePlannedRelease;

	@Required
    public boolean canDeletePlannedRelease;
	
	@Required
    public boolean canPublishPlannedRelease;

	@Required
    public boolean isNotifyWhenReleased;
	
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + name;
    }
}
