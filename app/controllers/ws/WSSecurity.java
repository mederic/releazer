package controllers.ws;

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
		if (userToken.isExpired()) {
			userToken.delete();
			renderJSON(new WSResult(WSConstants.INVALID_TOKEN_STATUS, WSConstants.INVALID_TOKEN_MESSAGE, null));
		}
	}

}
