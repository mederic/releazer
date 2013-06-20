package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import models.File;
import models.File.FileType;
import models.RoleType;
import models.User;


import play.db.jpa.Blob;
import play.libs.Codec;
import play.libs.MimeTypes;
import play.mvc.Controller;
import play.mvc.With;

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

				flash.success("Release succesfully deleted !");
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

				flash.success("Release succesfully deleted !");
			} else {		
				flash.success("Release succesfully updated !");
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
					newFile.fileCode = Codec.hexSHA1(new Date().getTime() + file.getName());
					newFile.metadata = ipaMetadata;
				}
			}
			newFile.save();
		}

		flash.success("File succesfully added !");
		show(release.id);
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

		flash.success("File succesfully deleted !");
		show(release.id);
	}

	public static void getFile(long id, String filename) {
		final models.File file = models.File.findById(id);
		models.Release release = file.release;
		User currentUser = Security.getCurrentUser();
		RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

		if (!Security.isAuthorizedFor(file) || !file.name.equals(filename)) {
			notFound();
		}

		notFoundIfNull(file);
		response.setContentTypeIfNotSet(file.file.type());
		renderBinary(file.file.get());
	}
}