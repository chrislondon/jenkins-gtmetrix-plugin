package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by chrislondon on 11/12/14.
 */
public class GtMetrixPagespeedResource extends AbstractGtMetrixJSONResource {
    public String getResourceFileName() {
        return "pagespeed.json";
    }

    public GtMetrixPagespeedResource(AbstractBuild build) {
        super(build);
    }

    public JSONArray getRules() {
        return resource.getJSONArray("rules");
    }

    public int getScore() {
        return resource.getJSONObject("pageStats").getInt("overallScore");
    }

    public int getFailureCount() {
        int failures = 0;

        JSONArray arr = resource.getJSONArray("rules");

        for (int i = 0; i < arr.size(); i++) {
            if (arr.getJSONObject(i).getInt("score") < 70) {
                failures++;
            }
        }

        return failures;
    }

    public int getWarningCount() {
        int warnings = 0;

        JSONArray arr = resource.getJSONArray("rules");

        for (int i = 0; i < arr.size(); i++) {
            if (arr.getJSONObject(i).getInt("score") >= 70 && arr.getJSONObject(i).getInt("score") < 80) {
                warnings++;
            }
        }

        return warnings;
    }
}
