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
public class File extends Model {
    
	@Required
    public String name;

	@Required
    public Blob file;

    @ManyToOne(cascade=CascadeType.ALL)
    public Release release;
    
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + name;
    }
}
