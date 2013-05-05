package controllers;

import static controllers.Secure.Security.connected;
import play.libs.Codec;
import models.File;
import models.Role;
import models.User;

public class Security extends Secure.Security {

    final static String ADMIN_NAME = "admin";
    final static String ADMIN_PASSWORD = "admin";
    final static String ADMIN_EMAIL = "admin@admin.com";
    
    
    static boolean authenticate(String username, String password) {
        if (User.count() == 0) {
            // if no user, admin/admin is the default account...
            if (username.equals(ADMIN_NAME) && password.equals(ADMIN_PASSWORD)) {
                User admin = new User();
                admin.isAdmin = true;
                admin.name = ADMIN_NAME;
		admin.email = ADMIN_EMAIL;
                admin.password = Codec.hexSHA1(ADMIN_PASSWORD);
                admin.save();
                return true;
            } else {
                return false;
            }
        } else {
            // otherwise, check rights in db...
            User user = User.find("byName", username).first();
            String hashPassword = Codec.hexSHA1(password);
            return user != null && user.password.equals(hashPassword);
        }
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
    
    public static String getCurrentUserEmail() {
        return getCurrentUser().email;
    }

    public static boolean isAuthorizedFor(models.Project project) {
        if (project == null) {
            return false;
        }

        Role role = Role.find("byUserAndProject", getCurrentUser(), project).first();
        return role != null;
    }

    public static boolean isAuthorizedFor(models.Release release) {
        if (release == null) {
            return false;
        }

        return isAuthorizedFor(release.project);
    }

    public static boolean isAuthorizedFor(File file) {
        if (file == null) {
            return false;
        }

        return isAuthorizedFor(file.release);
    }
    
    static void onDisconnected() {
	flash.keep();
        Application.index();
    }

    static void onAuthenticated() {  
	flash.keep();      
        Project.index();
    }
}
