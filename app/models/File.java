package models;

import java.util.Date;
import java.util.HashMap;
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
    
	public enum FileType {
		UNKNOWN,
		IPA
	};
	
	
	@Required
    public String name;
	
	@Required
	public FileType type;

	public HashMap<String, String> metadata = new HashMap<String, String>();
	
	@Required
    public Blob file;

    @ManyToOne
    public Release release;

	public String fileCode;
    
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + name;
    }
}
