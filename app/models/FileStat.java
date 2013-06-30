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
public class FileStat extends Model {

	@Required
    @ManyToOne
    public User user;
	
	@Required
    @ManyToOne
    public File file;
	
	@Required
    public String userAgent;

	@Required
    public Date date;
	
    @Override
    public String toString() {
    	return "[" + this.getId() + "] " + user.username + " : " + file.name + ". Time : " + date.getTime();
    }
}
