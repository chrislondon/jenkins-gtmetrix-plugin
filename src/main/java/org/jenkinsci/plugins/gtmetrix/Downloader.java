package org.jenkinsci.plugins.gtmetrix;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chrislondon on 11/14/14.
 */
public class Downloader {
    protected AbstractBuild build;
    protected Launcher launcher;
    protected BuildListener listener;
    protected String subDir;

    protected String username;
    protected String password;

    public Downloader(AbstractBuild build, Launcher launcher, BuildListener listener, String subDir) {
        this.build = build;
        this.launcher = launcher;
        this.listener = listener;
        this.subDir = subDir;
    }

    public void archive(HashMap files) {

    }

    public void download(HashMap files, FilePath target) throws InterruptedException, IOException {
        target = new FilePath(target, subDir);

        if (!target.exists()) {
            target.mkdirs();
        }

        Iterator it = files.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();

            URL resource = new URL((String)pairs.getKey());
            URLConnection connection = resource.openConnection();

            if (username != null && password != null) {
                String basicAuth = username + ":" + password;
                connection.setRequestProperty("Authorization", "Basic " + Base64.encode(basicAuth.getBytes()));
            }

            InputStream in = connection.getInputStream();
            target.copyFrom(in);

            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void setAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
