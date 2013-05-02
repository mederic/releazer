package controllers.cruds;

import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@With(Secure.class)
@Check("isAdmin")
public class Projects extends CRUD {

}
