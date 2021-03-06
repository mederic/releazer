package controllers.jobs;

import models.IpaFileToken;
import models.ws.WSUserToken;
import play.jobs.*;
  
@Every("12h")
public class Bootstrap extends Job {
    
    @Override
    public void doJob() {
        // every 12 hous, we clean token...
        IpaFileToken.cleanTokens();
        WSUserToken.cleanWSUserTokens();
    }
    
}