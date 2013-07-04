package models.ws;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import models.IpaFileToken;
import models.User;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Codec;
import play.mvc.Http;

@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = "token"))
public class WSUserToken extends Model {

    // expiration time in ms for a file token
    private final static long TOKEN_EXPIRATION_DURATION = 2 * 60 * 60 * 1000; // = 5 hours 
    
    @ManyToOne
    public User user;  
    
    public String token;    
    public Date lastUsedDate;
    
    public static WSUserToken createUserTokenFor(User user) {
    	WSUserToken newToken = new WSUserToken();
    	newToken.user = user;
    	newToken.lastUsedDate = new Date();
    	newToken.token = Codec.hexSHA1(Codec.UUID() + newToken.lastUsedDate.getTime() + user.id);
    	newToken.save();
    	
    	return newToken;
    }
    
    public static WSUserToken getUserTokenFor(String token) {
    	WSUserToken userToken = WSUserToken.find("byToken", token).first();
    	if ((userToken != null) && (!userToken.isExpired()))
    		return userToken;
    	else
    		return null;
    }
    
    public static void cleanWSUserTokens() {
        List<WSUserToken> tokens = WSUserToken.findAll();
        for (WSUserToken token : tokens) {
            if (token.isExpired()) {
                token.delete();
            }
        }
    }

    public boolean isExpired() {
        return (new Date().getTime() - this.lastUsedDate.getTime() > TOKEN_EXPIRATION_DURATION);
    }
}
