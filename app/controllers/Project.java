package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class Project extends Controller {

    public static void index() {
        User currentUser = Security.getCurrentUser();

        List<models.Project> projects = currentUser.getProjects();

        render(projects);
    }

    public static void show(long id) {
        models.Project project = models.Project.findById(id);
        User currentUser = Security.getCurrentUser();
        RoleType currentUserRoleType = currentUser.getRoleTypeFor(project);

        if (!Security.isAuthorizedFor(project)) {
            notFound();
        }
        render(project, currentUserRoleType);
    }

    public static void newRelease(long id, String releaseName) {
        models.Project project = models.Project.findById(id);
        User currentUser = Security.getCurrentUser();

        RoleType currentUserRoleType = currentUser.getRoleTypeFor(project);

        if (!Security.isAuthorizedFor(project) || !currentUserRoleType.canAddPlannedRelease) {
            notFound();
        }

        models.Release newRelease = new models.Release();
        newRelease.project = project;
        newRelease.isPublished = false;
        newRelease.date = new Date();

        if (releaseName == null || releaseName.isEmpty()) {
            newRelease.name = "new planned release...";
        } else {
            newRelease.name = releaseName;
        }

        newRelease.save();

        Release.show(newRelease.id);
    }

    public static void showLastRelease(long id) {
        models.Project project = models.Project.findById(id);
        models.Release lastRelease = models.Release.find("project = ? and isPublished = ? order by date desc", project, true).first();
        Release.show(lastRelease.id);
    }
}