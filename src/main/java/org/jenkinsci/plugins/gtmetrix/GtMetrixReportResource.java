package org.jenkinsci.plugins.gtmetrix;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import net.sf.json.JSONObject;

/**
 * Created by chrislondon on 11/12/14.
 */
public class GtMetrixReportResource extends AbstractGtMetrixJSONResource {
    public String getResourceFileName() {
        return "report.json";
    }

    public GtMetrixReportResource(AbstractBuild build) {
        super(build);
    }

    public String getPageLoadTime() {
        return resource.getJSONObject("results").getString("page_load_time");
    }

    public String getPageElements() {
        return resource.getJSONObject("results").getString("page_elements");
    }

    public String getReportUrl() {
        return resource.getJSONObject("results").getString("report_url");
    }

    public String getYslowScore() {
        return resource.getJSONObject("results").getString("yslow_score");
    }

    public String getPagespeedScore() {
        return resource.getJSONObject("results").getString("pagespeed_score");
    }

    public String getHtmlBytes() {
        return resource.getJSONObject("results").getString("html_bytes");
    }

    public String getHtmlLoadTime() {
        return resource.getJSONObject("results").getString("html_load_time");
    }

    public String getPageBytes() {
        return resource.getJSONObject("results").getString("page_bytes");
    }

    /**
     * 100% < 1s
     * 0% >= 6s
     *
     * @return
     */
    public int getTimeScore() {
        int time = Integer.parseInt(getPageLoadTime());

        if (time < 1000) {
            return 100;
        } else if (time >= 6000) {
            return 0;
        }

        return (int) Math.round((-1.0/50.0) * time + 120.0);
    }

    /**
     * 100% < 100kb
     * 50% = 1mb
     *
     * @return
     */
    public int getSizeScore() {
        int size = Integer.parseInt(getPageBytes());

        if (size < 100000) {
            return 100;
        } else if (size >= 1000000) {
            return 50;
        }

        return (int) Math.round((-1.0/18000.0) * size + 105.55556);
    }

    /**
     * 100% < 5
     * 0% >= 55
     *
     * @return
     */
    public int getRequestsScore() {
        int requests = Integer.parseInt(getPageElements());

        if (requests < 5) {
            return 100;
        } else if (requests >= 55) {
            return 0;
        }

        return (int) Math.round(-2.0 * requests + 110.0);
    }

    public int getOverallScore() {
        int timeScore = getTimeScore();
        int sizeScore = getSizeScore();
        int requestsScore = getRequestsScore();


        if (timeScore < sizeScore && timeScore < requestsScore) {
            return timeScore;
        }

        if (sizeScore < timeScore && sizeScore < requestsScore) {
            return sizeScore;
        }

        return requestsScore;
    }
}
