package models;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.jpa.Model;
import play.libs.Codec;

@Entity
@Table(uniqueConstraints =
        @UniqueConstraint(columnNames = "token"))
public class IpaFileToken extends Model {

    // expiration time in ms for a file token
    private final static long TOKEN_EXPIRATION_DURATION = 2 * 60 * 60 * 1000; // = 2 hours 
    
    @ManyToOne
    public File file;
    @ManyToOne
    public User user;
    public String token;
    public Date creationDate;

    public IpaFileToken(File file, User user) {
        this.file = file;
        this.user = user;

        this.creationDate = new Date();
        this.token = Codec.hexSHA1(this.creationDate.getTime() + file.id + file.name + user.id);
    }

    @Override
    public String toString() {
        return "[" + this.getId() + "] " + file.name + " for " + user.username + " : " + token + ". creation date : " + creationDate.toString();
    }

    public static void cleanTokens() {
        List<IpaFileToken> tokens = IpaFileToken.findAll();
        for (IpaFileToken token : tokens) {
            if (token.isExpired()) {
                token.delete();
            }
        }
    }

    private static IpaFileToken cleanedToken(IpaFileToken tokenEntry) {
        if (tokenEntry != null) {
            if (tokenEntry.isExpired()) {
                // this token is expired...
                tokenEntry.delete();
                return null;
            } else {
                return tokenEntry;
            }
        } else {
            return null;
        }
    }

    public static IpaFileToken getFileTokenFor(String fileToken) {
        IpaFileToken tokenEntry = IpaFileToken.find("byToken", fileToken).first();
        return IpaFileToken.cleanedToken(tokenEntry);
    }

    public static IpaFileToken getFileTokenFor(File file, User user) {
        IpaFileToken tokenEntry = IpaFileToken.find("byFileAndUser", file, user).first();
        return IpaFileToken.cleanedToken(tokenEntry);
    }

    public static File getFileForToken(String fileToken) {
        IpaFileToken token = IpaFileToken.getFileTokenFor(fileToken);
        if (token != null) {
            return token.file;
        } else {
            return null;
        }
    }

    private boolean isExpired() {
        return (new Date().getTime() - this.creationDate.getTime() > TOKEN_EXPIRATION_DURATION);
    }
}
