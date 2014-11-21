package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by chrislondon on 11/12/14.
 */
public class GtMetrixHistoryResource extends AbstractGtMetrixJSONResource {
    public String getResourceFileName() {
        return "history.json";
    }

    public GtMetrixHistoryResource(AbstractBuild build) {
        super(build);
    }

    public void generate() {
        Result r = Result.SUCCESS;

        GtMetrixReportResource report = new GtMetrixReportResource(build);
        GtMetrixPagespeedResource pagespeed = new GtMetrixPagespeedResource(build);
        GtMetrixYslowResource yslow = new GtMetrixYslowResource(build);

        resource = getLastHistory();

        /**
         * PAGE SPEED
         */
        if (r == Result.SUCCESS) {
            if (pagespeed.getFailureCount() > 0 || pagespeed.getScore() < 70) {
                r = Result.FAILURE;
            } else if (pagespeed.getWarningCount() > 0 || pagespeed.getScore() < 80) {
                r = Result.UNSTABLE;
            }
        }

        JSONObject data = new JSONObject();
        data.put("score", pagespeed.getScore() + "");
        data.put("failures", pagespeed.getFailureCount() + "");
        data.put("warnings", pagespeed.getWarningCount() + "");
        addHistory("pagespeed", data);

        /**
         * YSLOW
         */
        if (r == Result.SUCCESS) {
            if (yslow.getFailureCount() > 0) {
                r = Result.FAILURE;
            } else if (yslow.getWarningCount() > 0) {
                r = Result.UNSTABLE;
            }
        }

        data = new JSONObject();
        data.put("score", yslow.getScore() + "");
        data.put("failures", yslow.getFailureCount() + "");
        data.put("warnings", yslow.getWarningCount() + "");
        addHistory("yslow", data);

        /**
         * REQUEST
         */
        if (r == Result.SUCCESS) {
            if (report.getTimeScore() < 70 || report.getSizeScore() < 70 || report.getRequestsScore() < 70) {
                r = Result.FAILURE;
            } else if (report.getTimeScore() < 80 || report.getSizeScore() < 80 || report.getRequestsScore() < 80) {
                r = Result.UNSTABLE;
            }
        }

        data = new JSONObject();
        data.put("load", report.getPageLoadTime());
        data.put("size", report.getPageBytes());
        data.put("requests", report.getPageElements());
        addHistory("request", data);

        build.setResult(r);
    }

    public JSONArray getYslow() {
        return resource.getJSONArray("yslow");
    }

    public JSONArray getRequest() {
        return resource.getJSONArray("request");
    }

    public JSONArray getPagespeed() {
        return resource.getJSONArray("pagespeed");
    }

    protected void addHistory(String group, JSONObject row) {
        if (!resource.has(group)) {
            resource.put(group, new JSONArray());
        }

        JSONArray data = resource.getJSONArray(group);

        // ADD NEW HISTORY TO DATA
        row.put("build", build.getNumber() + "");
        data.add(row);

        // IF DATA HAS MORE THAN 30 DROP OLDEST
        if (data.size() > 30) {
            data.discard(0);
        }
    }

    public JSONObject getLastHistory() {
        AbstractBuild previousBuild = build;
        GtMetrixHistoryResource previousHistory;

        while (previousBuild.getPreviousBuild() != null) {
            previousBuild = previousBuild.getPreviousBuild();
            previousHistory = new GtMetrixHistoryResource(previousBuild);

            if (previousHistory.isValid()) {
                return previousHistory.getResource();
            }
        }

        return new JSONObject();
    }
}
