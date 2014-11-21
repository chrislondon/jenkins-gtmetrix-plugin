package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by chrislondon on 11/12/14.
 */
public class GtMetrixHarResource extends AbstractGtMetrixJSONResource {
    public String getResourceFileName() {
        return "har.json";
    }

    public GtMetrixHarResource(AbstractBuild build) {
        super(build);
    }

    public String getVersion() {
        return resource.getJSONObject("log").getString("version");
    }

    public JSONArray getEntries() {
        return resource.getJSONObject("log").getJSONArray("entries");
    }
}
