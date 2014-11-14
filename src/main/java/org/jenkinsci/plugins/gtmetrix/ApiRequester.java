package org.jenkinsci.plugins.gtmetrix;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chrislondon on 11/14/14.
 */
public class ApiRequester {
    protected String baseUrl;
    protected int lastStatusCode;

    protected HttpClient client;

    public ApiRequester(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;

        client = new HttpClient();
        client.getState().setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(
                username,
                password
            )
        );
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public JSONObject post(String target) throws IOException {
        return post(target, null);
    }

    public JSONObject post(String target, HashMap properties) throws IOException {
        PostMethod post = new PostMethod(baseUrl + "/" + target);
        post.setDoAuthentication(true);

        if (properties != null) {
            Iterator it = properties.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();

                post.addParameter((String)pairs.getKey(), (String)pairs.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        }


        lastStatusCode = client.executeMethod(post);
        JSONObject response = (JSONObject) JSONSerializer.toJSON(post.getResponseBodyAsString());

        post.releaseConnection();

        return response;
    }

    public JSONObject get(String target) throws IOException {
        return get(target, null);
    }

    public JSONObject get(String target, HashMap properties) throws IOException {
        String params = "";


        if (properties != null) {
            params += "?";

            Iterator it = properties.entrySet().iterator();
            boolean isFirst = true;

            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();

                if (isFirst) {
                    isFirst = false;
                } else {
                    params += "&";
                }

                params += (String)pairs.getKey() + "=" + (String)pairs.getValue();
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        GetMethod get = new GetMethod(baseUrl + "/" + target + params);
        get.setDoAuthentication(true);


        lastStatusCode = client.executeMethod(get);
        JSONObject response = (JSONObject) JSONSerializer.toJSON(get.getResponseBodyAsString());

        get.releaseConnection();

        return response;
    }
}
