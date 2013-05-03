package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Release extends Model {

    @Required
    public String name;    

    @Required
    public boolean isPublished;    

    @Required
    @ManyToOne
    public Project project;
    
    @Required
    public Date date;

    @Lob
    public String note;

    @OneToMany(mappedBy="release")
    public Set<File> attachedFiles;

    @Override
    public String toString() {
    	return "[" + this.getId() + "] (" + project.name + ") "+ name;
    }
}
