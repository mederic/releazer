package controllers;

import play.mvc.Before;
import play.mvc.Controller;

public class DeviceHelper extends Controller {
    
    public enum DeviceType {
	STANDARD,
	ANDROID,
	IOS
    }
    
    @Before
    static void checkUserAgent() {
	renderArgs.put("deviceType", getDeviceType());
    }
    
    private static DeviceType getDeviceType() {
	String userAgent = request.headers.get("user-agent").value();
	if (userAgent.contains("iPod") || userAgent.contains("iPhone") || userAgent.contains("iPad")) {
	    return DeviceType.IOS;
	} else if(userAgent.contains("android")) {
	    return DeviceType.ANDROID;
	} else {
	    return DeviceType.STANDARD;
	}
    }
}
