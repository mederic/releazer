package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class Project extends Controller {

    public static void index() {
    	User currentUser = Security.getCurrentUser();
    	
    	List<models.Project> projects = currentUser.getProjects();
    	
        render(projects);
    }
    
    public static void show(long id) {
    	models.Project project = models.Project.findById(id);
    	if (! Security.isAuthorizedFor(project)) {
    		notFound();
    	}
        render(project);
    }

    public static void showLastRelease(long id) {
    	models.Project project = models.Project.findById(id);    	
    	models.Release lastRelease = models.Release.find("project = ? order by date desc", project).first();
    	Release.show(lastRelease.id);
    }
    
}