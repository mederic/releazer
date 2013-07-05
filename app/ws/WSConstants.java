package ws;

public class WSConstants {
	
	public final static int SUCCESS_STATUS = 0;
	public final static String SUCCESS_MESSAGE = "Ok";
	
	public final static String USER_TOKEN_PARAM_NAME = "token";
	public final static int INVALID_TOKEN_STATUS = 1;
	public final static String INVALID_TOKEN_MESSAGE = "Invalid user token";
	
	
	public final static int INCORRECT_USERNAME_PASSWORD_STATUS = 101;
	public final static String INCORRECT_USERNAME_PASSWORD_MESSAGE = "Invalid username or password...";
	public final static int MISSING_PASSWORD_STATUS = 102;
	public final static String MISSING_PASSWORD_MESSAGE = "Missing password...";
	public final static int MISSING_USERNAME_STATUS = 103;
	public final static String MISSING_USERNAME_MESSAGE = "Missing username...";
}
