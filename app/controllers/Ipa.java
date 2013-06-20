package controllers;

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

import models.IpaStat;
import models.User;

import play.libs.IO;
import play.libs.XML;
import play.mvc.Controller;

public class Ipa extends Controller {

    public static void getIpaFile(String fileCode) {
        final models.File file = models.File.find("byFileCode", fileCode).first();

        notFoundIfNull(file);
        response.setContentTypeIfNotSet(file.file.type());
        renderBinary(file.file.get());
    }

    public static void getIpaManifest(String fileCode, long userId) {
        final models.File file = models.File.find("byFileCode", fileCode).first();

        User user = User.findById(userId);
        if (user != null)
        {
        	String userAgent = request.headers.get("user-agent").value();
        	IpaStat stats = new IpaStat();
        	stats.user = user;
        	stats.file = file;
        	stats.date = new Date();
        	stats.userAgent = userAgent;
        	
        	stats.save();
        }
    	
    	
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
        textResponse += "<string>" + play.Play.configuration.getProperty("application.baseUrl") + "/ipa/" + file.fileCode + ".ipa</string>";

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

            java.io.File plistFile = new java.io.File(file.getAbsolutePath() + ".plist");
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
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}