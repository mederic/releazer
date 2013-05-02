package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class Release extends Controller {

    public static void show(long id) {    	
    	models.Release release = models.Release.findById(id);
    	
    	if (! Security.isAuthorizedFor(release)) {
    		notFound();
    	}
    	
        render(release);
    }

    public static void getFile(long id) {
	   final models.File file = models.File.findById(id);

	   	if (! Security.isAuthorizedFor(file)) {
	   		notFound();
	   	}
   	
	   notFoundIfNull(file);
	   response.setContentTypeIfNotSet(file.file.type());
	   renderBinary(file.file.get());
	}
    
}