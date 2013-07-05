package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.File;
import models.File.FileType;
import models.FileStat;
import models.IpaFileToken;
import models.RoleType;
import models.User;
import notifiers.Mails;


import play.db.jpa.Blob;
import play.libs.Codec;
import play.libs.MimeTypes;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;
import ws.WSResult;
import ws.release.WSFileUploadData;

@With({Secure.class, DeviceHelper.class})
public class Release extends Controller {

    public static void show(long id) {
        models.Release release = models.Release.findById(id);
        User currentUser = Security.getCurrentUser();
        RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

        if (!Security.isAuthorizedFor(release) || (!release.isPublished && !currentUserRoleType.canReadPlannedRelease)) {
            notFound();
        }

        render(release, currentUserRoleType);
    }

    public static void edit(long id, String name, String note, String action) {
        Date date = new Date();
        models.Release release = models.Release.findById(id);
        User currentUser = Security.getCurrentUser();
        RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

        if (!Security.isAuthorizedFor(release) || (!currentUserRoleType.canWritePlannedRelease)) {
            notFound();
        }

        if ("delete".equalsIgnoreCase(action)) {
            if (currentUserRoleType.canDeletePlannedRelease) {
                long projectId = release.project.id;

                release.delete();

                flash.success("Release successfully deleted !");
                flash.keep();

                Project.show(projectId);
            } else {
                notFound();
            }
        } else {
            if ("publish".equalsIgnoreCase(action)) {
                if (currentUserRoleType.canPublishPlannedRelease) {
                    release.isPublished = true;
                } else {
                    notFound();
                }

                for (User user : release.project.getUsersToNotify()) {
                    Mails.releasePublished(user, release);
                }

                flash.success("Release successfully published !");
            } else {
                flash.success("Release successfully updated !");
            }
            flash.keep();

            if (name != null && !name.isEmpty()) {
                release.name = name;
            }

            release.date = date;
            release.note = note;

            release.save();
        }
        show(release.id);
    }

    public static void addFile(long id, java.io.File file) throws FileNotFoundException, Exception {
        models.Release release = models.Release.findById(id);
        User currentUser = Security.getCurrentUser();
        RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

        if (!Security.isAuthorizedFor(release) || (!currentUserRoleType.canWritePlannedRelease)) {
            notFound();
        }

        if (file != null) {

            File newFile = new models.File();
            newFile.release = release;
            newFile.name = file.getName();
            newFile.file = new Blob();
            newFile.file.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));

            newFile.type = FileType.STANDARD;

            int lastPointIndex = file.getName().lastIndexOf(".");
            if (lastPointIndex != -1) {
                if ("ipa".equalsIgnoreCase(file.getName().substring(lastPointIndex + 1))) {
                    HashMap<String, String> ipaMetadata = Ipa.getDataFromIpa(file);
                    // ipa, we need more data...

                    newFile.type = FileType.IPA;
                    newFile.metadata = ipaMetadata;
                    
                    if (newFile.metadata.containsKey("icon-path")) {
                        Blob iconBlob = Ipa.getIconBlob(file, newFile.metadata.get("icon-path"));
                        if (iconBlob != null) {
                        	newFile.icon = iconBlob;
                        }
                    }
                }
            }
            newFile.save();

            Map<String, Object> args = new HashMap<String, Object>();
            args.put("id", newFile.id);
            args.put("filename", newFile.name);
            Router.ActionDefinition actionDefinition = Router.reverse("Release.getFile", args);
            
            String ipaUrlManifest = null;
            if (newFile.type == FileType.IPA) {
                Map<String, Object> argsIpa = new HashMap<String, Object>();
                argsIpa.put("fileToken", newFile.getFileTokenFor(currentUser));
                argsIpa.put("userId", currentUser.id);                
                ipaUrlManifest = Router.getFullUrl("Ipa.getIpaManifest", argsIpa);
            }
            
            WSFileUploadData data = new WSFileUploadData(newFile, actionDefinition.url, ipaUrlManifest);
            WSResult result = new WSResult(0, "ok", data);
            renderJSON(result);
        }

        WSResult result = new WSResult(1, "no file sent...", null);
        renderJSON(result);
    }

    public static void deleteFile(long id) {
        final models.File file = models.File.findById(id);
        models.Release release = file.release;
        User currentUser = Security.getCurrentUser();
        RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

        if (!Security.isAuthorizedFor(release) || (!currentUserRoleType.canWritePlannedRelease)) {
            notFound();
        }

        file.file.getFile().delete();
        file.delete();
        
        WSResult result = new WSResult(0, "ok", null);
        renderJSON(result);
    }

    public static void getFile(long id, String filename) {
        final models.File file = models.File.findById(id);
        models.Release release = file.release;
        User currentUser = Security.getCurrentUser();

        if (!Security.isAuthorizedFor(file) || !file.name.equals(filename)) {
            notFound();
        }

        notFoundIfNull(file);

        file.addStatEntrybyUser(currentUser, request);

        response.setContentTypeIfNotSet(file.file.type());
        renderBinary(file.file.get());
    }
}