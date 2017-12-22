package action;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.struts2.interceptor.SessionAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;
import uc.sd.apis.FacebookApi2;

public class FacebookAction extends ActionSupport implements SessionAware{
/**
	 * 
	 */
	private static final long serialVersionUID = -7014565432319532405L;
	private static final String NETWORK_NAME = "Facebook";
	private static final Token EMPTY_TOKEN = null;
	private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
	private String apiKey = "176392666280433";
	private String apiSecret = "e54722eb61ffc23184814505a0cc26c7";	
	private String code = null;
	private Map<String, Object> session;

  public String execute() throws RemoteException {
	// Replace these with your own api key and secret
	  	OAuthService service;
	  	if(session.get("service") == null) {
		  	service = new ServiceBuilder()
		          .provider(FacebookApi2.class)
		          .apiKey(apiKey)
		          .apiSecret(apiSecret)
		          .callback("http://localhost:8080/ivotaswebserver/facebook") // Do not change this.
		          .scope("publish_actions")
		          .build();
		  	session.put("service", service);
	  	} else {
	  		service = (OAuthService) session.get("service");
	  	}
		Verifier verifier = new Verifier(code);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
		session.put("accessToken", accessToken);
	    service.signRequest(accessToken, request);
	    Response response = request.send();
		// Now let's go and ask for a protected resource!
		JSONParser parser = new JSONParser();
		String userId = null;
		try {
              JSONObject responseBody = (JSONObject) parser.parse(response.getBody());
              if(responseBody.containsKey("id")) {
                  userId = responseBody.get("id").toString();
              }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(this.getSessionBean().getUsername() == null) {
			if(this.getSessionBean().loginFacebook(userId)) {
				System.out.println("Login facebook funcionou");
			} else {
				System.out.println("Login facebook nao funcionou");
				return ERROR;
			}
		} else {
			if(this.getSessionBean().associateFacebook(userId)) {
				System.out.println("Associar facebook funcionou");
			} else {
				System.out.println("Associar facebook não funcionou");
			}
		}
		//postToFacebook(service, userId, "teste", accessToken.getToken());
		return SUCCESS;
  	}
  
	public SessionBean getSessionBean(){
		if(!session.containsKey("sessionBean"))
			this.setSessionBean(new SessionBean());
		return (SessionBean) session.get("sessionBean");
	}
	
	public void setSessionBean(SessionBean sessionBean){
		this.session.put("sessionBean", sessionBean);
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