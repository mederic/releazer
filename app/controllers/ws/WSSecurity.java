package controllers.ws;

import models.File;
import models.Project;
import models.Release;
import models.Role;
import models.User;
import models.ws.WSUserToken;
import play.mvc.Before;
import play.mvc.Controller;
import ws.WSConstants;
import ws.WSResult;

public class WSSecurity extends Controller {
	
	@Before
	static void checkAuthentification() {		
		String candidateToken = params.get(WSConstants.USER_TOKEN_PARAM_NAME);
		
		if (candidateToken == null)
			renderJSON(new WSResult(WSConstants.INVALID_TOKEN_STATUS, WSConstants.INVALID_TOKEN_MESSAGE, null));
		
		WSUserToken userToken = WSUserToken.getUserTokenFor(candidateToken);
		if (userToken == null || userToken.isExpired()) {
			if (userToken != null)
				userToken.delete();
			renderJSON(new WSResult(WSConstants.INVALID_TOKEN_STATUS, WSConstants.INVALID_TOKEN_MESSAGE, null));
		}
	}

	public static boolean isAuthorizedFor(User user, Project project) {
		if (project == null) {
            return false;
        }

        Role role = Role.find("byUserAndProject", user, project).first();
        return role != null;
	}
	
	public static boolean isAuthorizedFor(User user, Release release) {
		if (release == null) {
            return false;
        }

        return isAuthorizedFor(user, release.project);
	}

	public static boolean isAuthorizedFor(User user, File file) {
		if (file == null) {
            return false;
        }

        return isAuthorizedFor(user, file.release);
	}

}
