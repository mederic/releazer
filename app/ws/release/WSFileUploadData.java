/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.release;

import models.File;
import models.File.FileType;

/**
 *
 * @author mederic
 */
public class WSFileUploadData {
    
    public String filename;
    public long id;
    public FileType filetype;
    public String url;
    public String ipaManifestUrl;
    
    
    public WSFileUploadData(File file, String url, String ipaManifestUrl) {
        filename = file.name;
        id = file.id;
        filetype = file.type;
        this.url = url;
        this.ipaManifestUrl = ipaManifestUrl;
    }
}
