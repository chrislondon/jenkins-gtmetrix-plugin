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

    public String getSize(String sizeString) {
        boolean si = true;
        long bytes = Long.parseLong(sizeString);
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

    }

    public String getTime(String milliseconds) {
        double ms = Double.parseDouble(milliseconds);

        if (ms < 1000.0) {
            return ms + " ms";
        }

        return (ms / 1000.0) + " s";
    }

    public String getGrade(String scoreString) {
        int score = Integer.parseInt(scoreString);

        if (score < 60) {
            return "F";
        }

        if (score < 70) {
            return "D";
        }

        if (score < 80) {
            return "C";
        }

        if (score < 90) {
            return "B";
        }

        return "A";
    }
}
