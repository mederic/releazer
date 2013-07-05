package controllers.ws;

import play.*;
import play.mvc.*;
import ws.WSConstants;
import ws.WSResult;

import java.util.*;

import models.*;

@With(WSSecurity.class)
public class WSProject extends WSController {

    public static void getProjects() {
        User currentUser = getCurrentUser();

        List<Project> projects = currentUser.getProjects();
        List<HashMap> data = new ArrayList<HashMap>();
        for (Project project : projects) {
        	data.add(project.toHashMap(currentUser));
        }
        
        WSResult result = new WSResult(WSConstants.SUCCESS_STATUS, WSConstants.SUCCESS_MESSAGE, data);
        renderJSON(result);
    }
    
}