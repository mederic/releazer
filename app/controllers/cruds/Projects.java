package controllers.cruds;

import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import models.Project;
import models.Release;
import models.Role;
import models.RoleType;
import models.User;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

@With(Secure.class)
@Check("isAdmin")
public class Projects extends CRUD {

    public static void show(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);

        Project currentProject = (Project) object;
        List<User> usersNotInProjet = User.findAll();
        if (currentProject.attachedRoles != null) {
            for (Role role : currentProject.attachedRoles) {
                usersNotInProjet.remove(role.user);
            }
        }

        List<Release> releases = Release.find("project = ? order by isArchived, isPublished, date, desc", currentProject).first();
        
        try {
            render(type, object, usersNotInProjet, releases);
        } catch (TemplateNotFoundException e) {
            render("CRUD/show.html", type, object, usersNotInProjet, releases);
        }
    }

    public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);

        Projects.saveRoles((Project) object);

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
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();

        Projects.saveRoles((Project) object);

        flash.success(play.i18n.Messages.get("crud.created", type.modelName));

        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    private static void saveRoles(Project project) {
        // Already added users -------------------
        if (project.attachedRoles != null) {
            List<Role> rolesToDelete = new ArrayList<Role>();
            for (Role role : project.attachedRoles) {
                String newRoleIdStr = params.get("role-" + role.user.id);
                if (newRoleIdStr != null) {
                    long newRoleId = Long.valueOf(newRoleIdStr);
                    if (newRoleId != role.role.id) {
                        if (newRoleId == -1) {
                            // -1 => remove user of this projet...
                            rolesToDelete.add(role);
                        } else {
                            RoleType roleType = RoleType.findById(newRoleId);
                            if (roleType != null) {
                                role.role = roleType;
                                role.save();
                            }
                        }
                    }
                }
            }

            for (Role roleToDelete : rolesToDelete) {
                project.attachedRoles.remove(roleToDelete);
                roleToDelete.delete();
            }
        }

        // New user ------------------------------        
        String newUserIdStr = params.get("new-user-id");
        if (newUserIdStr != null) {
            long newUserId = Long.valueOf(newUserIdStr);
            if (newUserId != -1) {
                User newUser = User.findById(newUserId);
                if (newUser != null) {

                    String newRoleIdStr = params.get("new-user-role-id");
                    if (newRoleIdStr != null) {
                        long newRoleId = Long.valueOf(newRoleIdStr);
                        RoleType roleType = RoleType.findById(newRoleId);
                        if (roleType != null) {
                            Role role = new Role();
                            role.user = newUser;
                            role.project = project;
                            role.role = roleType;
                            role.save();
                        }
                    }
                }
            }
        }
    }
}
