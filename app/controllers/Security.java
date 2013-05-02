package controllers;

import java.util.List;

import play.libs.Codec;
import models.File;
import models.Role;
import models.User;

public class Security extends Secure.Security {
	
	static boolean authenticate(String username, String password) {
        User user = User.find("byName", username).first();        
        String hashPassword = Codec.hexSHA1(password);        
        return user != null && user.password.equals(hashPassword);
    }
	
	static boolean check(String profile) {
        User user = getCurrentUser();
        if ("isAdmin".equals(profile)) {
            return user.isAdmin;
        } else {
            return false;
        }
    }

	public static User getCurrentUser() {
		String username = connected();
		return User.find("byName", username).first();  
	}

	public static boolean isAuthorizedFor(models.Project project) {
		if (project == null)
			return false;		

		Role role = Role.find("byUserAndProject", getCurrentUser(), project).first();
		return role != null;
	}

	public static boolean isAuthorizedFor(models.Release release) {
		if (release == null)
			return false;
		
		return isAuthorizedFor(release.project);
	}

	public static boolean isAuthorizedFor(File file) {
		if (file == null)
			return false;
					
		return isAuthorizedFor(file.release);
	} 
}
