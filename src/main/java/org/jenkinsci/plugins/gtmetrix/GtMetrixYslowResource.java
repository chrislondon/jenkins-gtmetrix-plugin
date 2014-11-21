package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by chrislondon on 11/12/14.
 */
public class GtMetrixYslowResource extends AbstractGtMetrixJSONResource {
    public String getResourceFileName() {
        return "yslow.json";
    }

    public GtMetrixYslowResource(AbstractBuild build) {
        super(build);
    }

    public JSONArray getRules() {
        JSONArray response = new JSONArray();


        response.add(generateRule("yexpires", "Add expires headers"));
        response.add(generateRule("ycompress", "Compress components with gzip"));
        response.add(generateRule("ycdn", "Use a Content Delivery Network (CDN)"));
        response.add(generateRule("ynumreq", "Make fewer HTTP requests"));
        response.add(generateRule("ycookiefree", "Use cookie-free domains"));
        response.add(generateRule("yetags", "Configure entity tags (ETags)"));
        response.add(generateRule("yminify", "Minify JavaScript and CSS"));
        response.add(generateRule("ydns", "Reduce DNS lookups"));
        response.add(generateRule("yemptysrc", "Avoid empty src or href"));
        response.add(generateRule("yredirects", "Avoid URL redirects"));
        response.add(generateRule("yxhr", "Make AJAX cacheable"));
        response.add(generateRule("ycsstop", "Put CSS at the top"));
        response.add(generateRule("ydupes", "Remove duplicate JavaScript and CSS"));
        response.add(generateRule("yjsbottom", "Put JavaScript at bottom"));
        response.add(generateRule("ynofilter", "Avoid AlphaImageLoader filter"));
        response.add(generateRule("yno404", "Avoid HTTP 404 (Not Found) error"));
        response.add(generateRule("ymindom", "Reduce the number of DOM elements"));
        response.add(generateRule("yexpressions", "Avoid CSS expressions "));
        response.add(generateRule("yxhrmethod", "Use GET for AJAX requests"));
        response.add(generateRule("yimgnoscale", "Do not scale images in HTML"));
        response.add(generateRule("ymincookie", "Reduce cookie size"));
        response.add(generateRule("yfavicon", "Make favicon small and cacheable"));
        //response.add(generateRule("yexternal", "Make JavaScript and CSS external"));

        return response;
    }

    protected JSONObject generateRule(String key, String name) {
        JSONObject response = new JSONObject();
        JSONObject element = resource.getJSONObject("g").getJSONObject(key);

        response.put("name", name);

        try {
            response.put("score", element.getInt("score") + "");
        } catch (Exception e) {
            response.put("score", "0");
        }

        return response;
    }

    public int getFailureCount() {
        int failures = 0;

        JSONArray arr = getRules();

        for (int i = 0; i < arr.size(); i++) {
            if (arr.getJSONObject(i).getInt("score") < 70) {
                failures++;
            }
        }

        return failures;
    }

    public int getWarningCount() {
        int warnings = 0;

        JSONArray arr = getRules();

        for (int i = 0; i < arr.size(); i++) {
            if (arr.getJSONObject(i).getInt("score") >= 70 && arr.getJSONObject(i).getInt("score") < 80) {
                warnings++;
            }
        }

        return warnings;
    }
}
