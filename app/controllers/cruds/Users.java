package controllers.cruds;


import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import models.User;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.libs.Codec;

@With(Secure.class)
@Check("isAdmin")
public class Users extends CRUD {
    
    public static void save(String id) throws Exception {
        sha1Password(id);
       
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
    
    public static void create() throws Exception {
        sha1Password(null);
        
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            params.remove("object.password");
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    private static void sha1Password(String userId) {
        User currentUser = null;
        if (userId != null) {
            long longUserId = Long.valueOf(userId);
            currentUser = User.findById(longUserId);
        }
        
        String newPassword = params.get("object.password");
        if (newPassword == null || newPassword.isEmpty()) {
            if (currentUser != null)
                params.put("object.password", currentUser.password);
        } else {
            params.put("object.password", Codec.hexSHA1(newPassword));
        }
    }

}
