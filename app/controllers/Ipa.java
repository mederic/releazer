package controllers;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import models.File;
import models.FileStat;
import models.IpaFileToken;

import models.User;
import play.db.jpa.Blob;
import play.db.jpa.GenericModel;

import play.libs.MimeTypes;
import play.libs.XML;
import play.mvc.Controller;

public class Ipa extends Controller {

    public static void getIpaFile(String fileToken) {
        final models.File file = IpaFileToken.getFileForToken(fileToken);
        
        notFoundIfNull(file);
        response.setContentTypeIfNotSet(file.file.type());
        renderBinary(file.file.get());
    }

    public static void renderIcon(long id) {
    	// Doesn't work for now... 
    	notFound();
    	
        File ipaFile = File.findById(id);

        if (!Security.isAuthorizedFor(ipaFile) || !ipaFile.icon.exists()) {
        	notFound();
        }
        
        response.setContentTypeIfNotSet(ipaFile.icon.type());
        renderBinary(ipaFile.icon.get());
    }
    
    public static void getIpaManifest(String fileToken, long userId) {
        final models.File file = IpaFileToken.getFileForToken(fileToken);

        notFoundIfNull(file);
        
        file.addStatEntryByUserId(userId, request);
    	
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
        textResponse += "<string>" + play.Play.configuration.getProperty("application.baseUrl") + "/ipa/" + fileToken + ".ipa</string>";

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

    public static HashMap<String, String> getDataFromIpa(java.io.File file) throws Exception {
        HashMap<String, String> result = new HashMap<String, String>();
        ZipFile ipaZipFile;
        try {
            ipaZipFile = new ZipFile(file);
            final Enumeration<? extends ZipEntry> entries = ipaZipFile.entries();

            InputStream input = null;
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.getName().endsWith("app/Info.plist")) {
                    input = ipaZipFile.getInputStream(zipEntry);
                    break;
                }
            }

            
            if (input != null) {
                NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(input);
                result.put("bundle-identifier", rootDict.objectForKey("CFBundleIdentifier").toString());
                result.put("bundle-version", rootDict.objectForKey("CFBundleVersion").toString());
                result.put("kind", "software");
                result.put("title", rootDict.objectForKey("CFBundleName").toString()); 
                
                if (rootDict.objectForKey("MinimumOSVersion") != null)
                	result.put("minimum-os-version", rootDict.objectForKey("MinimumOSVersion").toString());
                
                String iconFilePath = getIconFilePath(rootDict);
                if (iconFilePath != null) {
                	result.put("icon-path", iconFilePath);
                }                
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

	public static Blob getIconBlob(java.io.File file, String iconFilePath) {
		ZipFile ipaZipFile;
        try {
            ipaZipFile = new ZipFile(file);
            final Enumeration<? extends ZipEntry> entries = ipaZipFile.entries();

            InputStream input = null;
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.getName().endsWith("app/" + iconFilePath)) {
                    input = ipaZipFile.getInputStream(zipEntry);
                	break;
                }
            }

            
            if (input != null) {                
                Blob result = new Blob();                
                result.set(input, MimeTypes.getContentType(iconFilePath));
                return result;
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}

	private static String getIconFilePath(NSDictionary rootDict) {
		NSDictionary iconsDico = (NSDictionary) rootDict.objectForKey("CFBundleIcons");
		if (iconsDico == null)
			return null;
		
		NSDictionary primaryIconsDico = (NSDictionary) iconsDico.objectForKey("CFBundlePrimaryIcon");
		if (primaryIconsDico == null)
			return null;
		

		NSArray primaryIconsFilesDico = (NSArray) primaryIconsDico.objectForKey("CFBundleIconFiles");
		if (primaryIconsFilesDico == null || primaryIconsFilesDico.count() == 0)
			return null;
		
		return primaryIconsFilesDico.lastObject().toString();
		
	}
}