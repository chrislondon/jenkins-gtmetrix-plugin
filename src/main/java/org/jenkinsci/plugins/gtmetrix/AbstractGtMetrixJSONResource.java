package org.jenkinsci.plugins.gtmetrix;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by chrislondon on 11/12/14.
 */
public abstract class AbstractGtMetrixJSONResource {
    protected JSONObject resource = null;
    protected JSONObject previousResource = null;

    public abstract String getResourceFileName();

    protected AbstractGtMetrixJSONResource(AbstractBuild build) {
        try {
            resource = loadFile(build);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            previousResource = loadFile(build.getPreviousBuild());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public JSONObject loadFile(AbstractBuild build) throws IOException, InterruptedException {
        String jsonString = "";

        BufferedReader reader = new BufferedReader(new FileReader(build.getRootDir().getPath() + "/gtmetrix/" + getResourceFileName()));

        System.out.println(build.getRootDir().getPath() + "/gtmetrix/" + getResourceFileName());

        String line = null;
        while ((line = reader.readLine()) != null) {
            jsonString += line;
        }

        return (JSONObject) JSONSerializer.toJSON(jsonString);
    }

    public boolean isValid() {
        return resource != null;
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
        return getTime(Double.parseDouble(milliseconds));
    }

    public String getTime(int milliseconds) {
        return getTime((double) milliseconds);
    }

    public String getTime(double ms) {
        if (ms < 1000.0) {
            return ms + " ms";
        }

        return (ms / 1000.0) + " s";
    }

    public String getGrade(String scoreString) {
        return getGrade(Integer.parseInt(scoreString));
    }

    public String getGrade(int score) {
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getStatus(int score) {
        if (score < 70) {
            return "Failure";
        }

        if (score < 80) {
            return "Warning";
        }

        return "Success";
    }

    public String getStatus(String score) {
        return getStatus(Integer.parseInt(score));
    }
}
