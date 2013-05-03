package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;

import models.File;
import models.File.FileType;
import models.RoleType;
import models.User;

import org.apache.commons.configuration.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import play.db.jpa.Blob;
import play.libs.Codec;
import play.libs.MimeTypes;
import play.libs.XML;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Release extends Controller {

    public static void show(long id) {    	
		models.Release release = models.Release.findById(id);
    	User currentUser = Security.getCurrentUser();
    	RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

		if (!Security.isAuthorizedFor(release) || (!release.isPublished && !currentUserRoleType.canSeePlannedRelease)) {
			notFound();
		}

		render(release, currentUserRoleType);
	}

	public static void edit(long id, String name, Date date, String note, String action) {	
		models.Release release = models.Release.findById(id);
		User currentUser = Security.getCurrentUser();
    	RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

		if (!Security.isAuthorizedFor(release) || (!currentUserRoleType.canEditPlannedRelease)) {
			notFound();
		}
		
		if ("delete".equalsIgnoreCase(action)) {
			if (currentUserRoleType.canDeletePlannedRelease) {
				long projectId = release.project.id;
				
				for (File file : release.attachedFiles) {
					file.delete();
				}
				
				release.delete();
				Project.show(projectId);
			} else {
				notFound();
			}
		} else if ("publish".equalsIgnoreCase(action)) {
			if (currentUserRoleType.canPublishPlannedRelease) {
				release.isPublished = true;
				release.save();
			} else {
				notFound();
			}
		} else {
			if (name != null && !name.isEmpty())
				release.name = name;
			
			release.date = date;
			release.note = note;
			release.save();
		}
		
		show(release.id);		
	}

	public static void addFile(long id, java.io.File file) throws FileNotFoundException, ConfigurationException {
		models.Release release = models.Release.findById(id);
		User currentUser = Security.getCurrentUser();
    	RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

		if (!Security.isAuthorizedFor(release) || (!currentUserRoleType.canEditPlannedRelease)) {
			notFound();
		}
	
		if (file != null) {
			File newFile = new models.File();
			newFile.release = release;
			newFile.name = file.getName();
			newFile.file = new Blob();
			newFile.file.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
			
			int lastPointIndex = file.getName().lastIndexOf(".");
			if (lastPointIndex != -1)
			{
				if ("ipa".equalsIgnoreCase(file.getName().substring(lastPointIndex + 1))) {
					HashMap<String, String> ipaMetadata = Ipa.getDataFromIpa(file);
					
					renderXml(ipaMetadata);
					// ipa, we need more data...
					newFile.type = FileType.IPA;
					newFile.fileCode = Codec.hexSHA1(file.getName());
					newFile.metadata.put("bundle-identifier", "com.O12S.PlanetSushi");
					newFile.metadata.put("bundle-version", "1.0");
					newFile.metadata.put("kind", "software");
					newFile.metadata.put("title", "PlanetSushi");
				}
			}
			newFile.save();
		}
		
		show(release.id);
	}
	
	private static void getDataFromIpa(java.io.File file) {
		// TODO Auto-generated method stub
		
	}

	public static void deleteFile(long id) {
		final models.File file = models.File.findById(id);
		models.Release release = file.release;
		User currentUser = Security.getCurrentUser();
    	RoleType currentUserRoleType = currentUser.getRoleTypeFor(release.project);

		if (!Security.isAuthorizedFor(release) || (!currentUserRoleType.canEditPlannedRelease)) {
			notFound();
		}
		
		file.file.getFile().delete();
		file.delete();

		show(release.id);
	}
	
	public static void getFile(long id, String filename) {
		final models.File file = models.File.findById(id);

		if (!Security.isAuthorizedFor(file) || !file.name.equals(filename)) {
			notFound();
		}

		notFoundIfNull(file);
		response.setContentTypeIfNotSet(file.file.type());
		renderBinary(file.file.get());
	}
	
}