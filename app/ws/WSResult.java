/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

/**
 *
 * @author mederic
 */
public class WSResult {
	
    public int status;
    public String message;
    
    public Object data;
    
    public WSResult(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
