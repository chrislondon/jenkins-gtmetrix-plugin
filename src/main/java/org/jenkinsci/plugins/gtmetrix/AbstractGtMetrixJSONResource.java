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
abstract class AbstractGtMetrixJSONResource {
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

        String line = null;
        while ((line = reader.readLine()) != null) {
            jsonString += line;
        }

        return (JSONObject) JSONSerializer.toJSON(jsonString);
    }

    public boolean isValid() {
        return resource != null;
    }

    public String humanizeMilliseconds(String msString) {
        double ms = Double.parseDouble(msString);

        if (ms < 1000) {
            return ms + "ms";
        }

        if (ms < 60000) {
            return round(ms / 1000, 2) + "s";
        }

        return round(ms / 60000, 2) + "m";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
