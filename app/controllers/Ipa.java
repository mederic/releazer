package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import models.File;
import models.File.FileType;
import models.RoleType;
import models.User;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import play.db.jpa.Blob;
import play.libs.MimeTypes;
import play.libs.XML;
import play.mvc.Controller;
import play.mvc.With;

public class Ipa extends Controller {

	public static void getIpaFile(String fileCode) {
		final models.File file = models.File.find("byFileCode", fileCode).first();

		notFoundIfNull(file);
		response.setContentTypeIfNotSet(file.file.type());
		renderBinary(file.file.get());
	}
	
	public static void getIpaManifest(String fileCode) {
		final models.File file = models.File.find("byFileCode", fileCode).first();
		
		String textResponse = "";
		textResponse += "<plist version=\"1.0\">";
		textResponse += "<dict>";

		textResponse += "<key>items</key>";
		textResponse += "<array>";
		textResponse += "<dict>";

		textResponse += "<key>assets</key>";
		textResponse += "<array>";
		textResponse += "<dict>";
		textResponse += "<key>kind</key>";
		textResponse += "<string>software-package</string>";
		textResponse += "<key>url</key>";
		textResponse += "<string>" + play.Play.configuration.getProperty("application.baseUrl") + "/ipa/" + file.fileCode  + ".ipa</string>";
		
		textResponse += "</dict>";		
		textResponse += "</array>";

		textResponse += "<key>metadata</key>";
		textResponse += "<dict>";
		
		textResponse += "<key>bundle-identifier</key>";
		textResponse += "<string>" + file.metadata.get("bundle-identifier") + "</string>";
		textResponse += "<key>bundle-version</key>";
		textResponse += "<string>" + file.metadata.get("bundle-version") + "</string>";
		textResponse += "<key>kind</key>";
		textResponse += "<string>" + file.metadata.get("kind") + "</string>";
		textResponse += "<key>title</key>";
		textResponse += "<string>" + file.metadata.get("title") + "</string>";
		
		textResponse += "</dict>";
		
		textResponse += "</dict>";
		textResponse += "</array>";
		textResponse += "</dict>";
		textResponse += "</plist>";
		
		renderXml(XML.getDocument(textResponse));
	}
	
}