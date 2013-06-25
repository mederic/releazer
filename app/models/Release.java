package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
@Table(name="ProjectRelease")
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
    @Column(columnDefinition = "text")
    public String note;

    @OneToMany(mappedBy="release", cascade=CascadeType.ALL)
    public Set<File> attachedFiles;

    @Required
    public boolean isArchived; 
    
    @Override
    public String toString() {
    	return "[" + this.getId() + "] (" + project.name + ") "+ name;
    }
}
