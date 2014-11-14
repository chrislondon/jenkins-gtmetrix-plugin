package org.jenkinsci.plugins.gtmetrix;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public void archive(HashMap files) throws InterruptedException, IOException {
        build.getArtifactManager().archive(build.getWorkspace(), launcher, listener, files);
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

            byte[] buffer = new byte[8 * 1024];

            InputStream input = connection.getInputStream();
            try {
                OutputStream output = new FileOutputStream(target.toURI().getPath() + (String)pairs.getValue());
                try {
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } finally {
                    output.close();
                }
            } finally {
                input.close();
            }

            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void setAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
