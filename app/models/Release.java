package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import controllers.Security;
import controllers.ws.WSSecurity;

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

	public HashMap toHashMap(User user) {
		HashMap result = new HashMap<String, Object>();
		result.put("id", this.id);
		result.put("name", this.name);
		result.put("date", this.date.getTime());
		result.put("note", this.note);
		result.put("isPublished", this.isPublished);
		result.put("isArchived", this.isArchived);

		List<HashMap> files = new ArrayList<HashMap>();
		for (File file : this.attachedFiles) {
			if (WSSecurity.isAuthorizedFor(user, file))
				files.add(file.toHashMap(user));
		}
		result.put("files", files);
		
		return result;
	}
}
