package controllers.ws;

import controllers.Security;
import models.User;
import models.ws.WSUserToken;
import play.mvc.Controller;
import play.mvc.With;
import ws.WSConstants;
import ws.WSResult;

public class WSController extends Controller {

	public static void getUserToken(String username, String password) {
		
		if (username == null) {
			renderJSON(new WSResult(WSConstants.MISSING_USERNAME_STATUS, WSConstants.MISSING_USERNAME_MESSAGE, null));
		}

		if (password == null) {
			renderJSON(new WSResult(WSConstants.MISSING_PASSWORD_STATUS, WSConstants.MISSING_PASSWORD_MESSAGE, null));
		}
		
		boolean isValid = Security.authenticate(username, password);		
		if (isValid) {
			User currentUser = User.find("byUsername", username).first();
			WSUserToken token = WSUserToken.createUserTokenFor(currentUser);
			renderJSON(new WSResult(WSConstants.SUCCESS_STATUS, WSConstants.SUCCESS_MESSAGE, token.token));	
		} else {
			renderJSON(new WSResult(WSConstants.INCORRECT_USERNAME_PASSWORD_STATUS, WSConstants.INCORRECT_USERNAME_PASSWORD_MESSAGE, null));			
		}
	}
	
	protected static User getCurrentUser() {
		String candidateToken = params.get(WSConstants.USER_TOKEN_PARAM_NAME);
		WSUserToken userToken = WSUserToken.getUserTokenFor(candidateToken);
		if (userToken != null)
			return userToken.user;
		else
			return null;
	}
}
