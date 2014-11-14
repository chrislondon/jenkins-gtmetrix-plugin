package org.jenkinsci.plugins.gtmetrix;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import net.sf.json.JSONObject;

/**
 * Created by chrislondon on 11/12/14.
 */
class GtMetrixReportResource extends AbstractGtMetrixJSONResource {
    public String getResourceFileName() {
        return "report.json";
    }

    public GtMetrixReportResource(AbstractBuild build) {
        super(build);
    }

    public String getPageLoadTime() {
        System.out.println(resource.getJSONObject("results").getString("page_load_time"));
        return resource.getJSONObject("results").getString("page_load_time");
    }
}
