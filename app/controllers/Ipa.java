package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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

    public static HashMap<String, String> getDataFromIpa(java.io.File file) {
        HashMap<String, String> result = new HashMap<String, String>();
        ZipFile ipaZipFile;
        try {
            ipaZipFile = new ZipFile(file);

            final Enumeration<? extends ZipEntry> entries = ipaZipFile.entries();

            java.io.File plistFile = new java.io.File(file.getAbsolutePath() + ".plist");
            boolean ok = false;
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.getName().endsWith("Info.plist")) {
                    InputStream input = ipaZipFile.getInputStream(zipEntry);
                    IO.write(input, plistFile);
                    System.out.println(IO.readContentAsString(plistFile));
                    ok = true;
                    break;
                }
            }

            if (ok) {
                //PropertyListConfiguration plist = new PropertyListConfiguration(plistFile);
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}