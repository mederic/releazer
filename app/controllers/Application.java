package controllers;

import play.mvc.*;


import models.*;
import play.data.validation.Validation;
import play.libs.Codec;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void myAccount() {
	User user = Security.getCurrentUser();
	render(user);
    }
    
    public static void updateAccount(String email, String password, String repassword) {
	validation.required(email);
	validation.email(email);
	
	if (password != null && !password.isEmpty()) {
	    validation.minSize(password, 8);
	    validation.equals(password, repassword);
	}
	
	if (Validation.hasErrors()) {
	    Validation.keep();
	} else {	    
	    User user = Security.getCurrentUser();
	    user.email = email;	    
	    if (password != null && !password.isEmpty())
		user.password = Codec.hexSHA1(password);
	    
	    user.save();
	    flash.success("account succesfully updated !");
	    flash.keep();
	}
	myAccount();	
    }
}