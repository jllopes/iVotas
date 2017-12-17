package com.github.scribejava.apis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20ServiceImpl;
import com.github.scribejava.core.oauth.OAuthService;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.utils.Preconditions;

public class InstagramApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "https://api.instagram.com/oauth/authorize/?"+
            "client_id=%s&" +
            "scope=%s&" +
            "redirect_uri=%s&" +
            "response_type=code&" +
            "access_type=offline";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.instagram.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return String.format(AUTHORIZE_URL,
                config.getApiKey(),
                OAuthEncoder.encode(config.getScope()),
                OAuthEncoder.encode(config.getCallback()));

    }
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new AccessTokenExtractor() {
            private Pattern accessTokenPattern =
                    Pattern.compile("\"access_token\"\\s*:\\s*\"(\\S*?)\"");

            @Override
            public Token extract(String response) {
                Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");
                Matcher matcher = accessTokenPattern.matcher(response);
                if (matcher.find()) {
                    return new Token(matcher.group(1), "", response);
                } else {
                    throw new OAuthException("Cannot extract an acces token. Response was: " + response);
                }
            }
        };
    }


    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public OAuthService createService(final OAuthConfig config) {
        return new OAuth20ServiceImpl(this, config) {
            public Token getAccessToken(Token requestToken, Verifier verifier) {
                OAuthRequest request = new OAuthRequest(getAccessTokenVerb(),
                        getAccessTokenEndpoint(), this);
                request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
                request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
                request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
                request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
                request.addBodyParameter("grant_type", "authorization_code");
                if (config.hasScope()) {
                    request.addBodyParameter(OAuthConstants.SCOPE, config.getScope());
                }
                Response response = request.send();
                return getAccessTokenExtractor().extract(response.getBody());
            }
            
            @Override
            public void signRequest(Token accessToken, AbstractRequest request) {
            	System.out.println(accessToken + " token");
                request.addQuerystringParameter("access_token", accessToken.getToken());
            	System.out.println("asking for " + request.getCompleteUrl());
            }
        };
    }
}