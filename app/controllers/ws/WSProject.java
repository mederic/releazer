package controllers.ws;

import play.*;
import play.mvc.*;
import ws.WSConstants;
import ws.WSResult;

import java.util.*;

import models.*;

@With(WSSecurity.class)
public class WSProject extends WSController {

    public static void get() {
        User currentUser = getCurrentUser();

        List<Project> projects = currentUser.getProjects();

        WSResult result = new WSResult(WSConstants.SUCCESS_STATUS, WSConstants.SUCCESS_MESSAGE, projects);
        renderJSON(result);
    }
    
}