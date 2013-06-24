package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.mvc.Http;

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
}
