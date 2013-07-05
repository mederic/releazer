package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.mvc.Http;
import play.mvc.Router;

@Entity
public class File extends Model {

    public enum FileType {
        STANDARD,
        IPA
    };
    
    @Required
    public String name;
    
    @Required
    public FileType type;
    public HashMap<String, String> metadata = new HashMap<String, String>();
    
    @Required
    public Blob file;

    public Blob icon;
    
    @ManyToOne
    public Release release;    
    
    @OneToMany(mappedBy="file", cascade=CascadeType.ALL)
    public Set<IpaFileToken> tokens;
    
    @OneToMany(mappedBy="file", cascade=CascadeType.ALL)
    public Set<FileStat> stats;
    
    @Override
    public String toString() {
        return "[" + this.getId() + "] " + name;
    }

    public List<FileStat> getStatistics() {
        List<FileStat> result = FileStat.find("file = ? order by date desc", this).fetch();
        return result;
    }

    public void addStatEntryByUserId(long userId, Http.Request request) {
        User user = User.findById(userId);
        if (user != null) {
            this.addStatEntrybyUser(user, request);
        }
    }

    public void addStatEntrybyUser(User user, Http.Request request) {
        String userAgent = request.headers.get("user-agent").value();
        FileStat stats = new FileStat();
        stats.user = user;
        stats.file = this;
        stats.date = new Date();
        stats.userAgent = userAgent;

        stats.save();
    }
    
    public String getFileTokenFor(User user) {
        IpaFileToken token = IpaFileToken.getFileTokenFor(this, user);
        if (token == null) {
            token = new IpaFileToken(this, user);
            token.save();
        }
        
        if (token != null)
            return token.token;
        else
            return null;
    }

	public HashMap toHashMap(User user) {
		HashMap result = new HashMap<String, Object>();
		result.put("id", this.id);
		result.put("name", this.name);
		result.put("type", this.type);

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("id", this.id);
        args.put("filename", this.name);
        Router.ActionDefinition actionDefinition = Router.reverse("Release.getFile", args);
        result.put("url", actionDefinition.url);
        
        Map<String, Object> argsIpa = new HashMap<String, Object>();
        argsIpa.put("fileToken", this.getFileTokenFor(user));
        argsIpa.put("userId", user.id);  
        Router.ActionDefinition actionDefinitionIpa = Router.reverse("Ipa.getIpaManifest", argsIpa);
        result.put("ipaManifestUrl", actionDefinitionIpa.url);
		
		return result;
	}
}
