package action;

import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.struts2.interceptor.SessionAware;
import org.json.*;
import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;

import uc.sd.apis.FacebookApi2;

public class FacebookAction extends ActionSupport implements SessionAware{
/**
	 * 
	 */
	private static final long serialVersionUID = -7014565432319532405L;
  private static final String NETWORK_NAME = "Facebook";
  private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
  private static final Token EMPTY_TOKEN = null;
  private String apiKey = "176392666280433";
  private String apiSecret = "e54722eb61ffc23184814505a0cc26c7";
  private String code;	
  private Map<String, Object> session;

  public String execute() {
	// Replace these with your own api key and secret

	    OAuthService service = new ServiceBuilder()
	                                  .provider(FacebookApi2.class)
	                                  .apiKey(apiKey)
	                                  .apiSecret(apiSecret)
	                                  .callback("http://localhost:8080/ivotaswebserver/facebook") // Do not change this.
	                                  .build();
	    Scanner in = new Scanner(System.in);

	    if(code != null && !code.equals("")) {
	    		System.out.print("\n"+code+"\n");
	    		Verifier verifier = new Verifier(code);
		    System.out.println();
		    
		    // Trade the Request Token and Verfier for the Access Token
		    //System.out.println("Verifier " + verifier.getValue() + " Request Token " + EMPTY_TOKEN);
		    System.out.println("Trading the Request Token for an Access Token...");
		    Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		    System.out.println("Got the Access Token!");
		    System.out.println("(if your curious it looks like this: " + accessToken + " )");
		    System.out.println();
	
		    // Now let's go and ask for a protected resource!
		    System.out.println("Now we're going to access a protected resource...");
		    OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
		    service.signRequest(accessToken, request);
		    Response response = request.send();
		    System.out.println("Got it! Lets see what we found...");
		    System.out.println();
		    System.out.println(response.getCode());
		    System.out.println(response.getBody());
	
		    
		    System.out.println();
		    System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
	    } else {
		    System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
		    System.out.println();
	
		    // Obtain the Authorization URL
		    System.out.println("Fetching the Authorization URL...");
		    String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		    System.out.println("Got the Authorization URL!");
		    System.out.println("Now go and authorize Scribe here:");
		    System.out.println(authorizationUrl);
		    System.out.println("And paste the authorization code here");
		    System.out.print(">>");
	    }
	    return SUCCESS;
}

@Override
public void setSession(Map<String, Object> session) {
	this.session = session;
}

/**
 * @return the code
 */
public String getCode() {
	return code;
}

/**
 * @param code the code to set
 */
public void setCode(String code) {
	this.code = code;
}


}