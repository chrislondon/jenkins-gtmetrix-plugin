package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import hudson.model.Build;
import hudson.model.Action;
import org.kohsuke.stapler.StaplerProxy;

/**
 * Created by chrislondon on 11/11/14.
 */
public class GtMetrixBuildAction implements Action {
    public final AbstractBuild<?, ?> build;
    public GtMetrixReportResource report = null;
    public GtMetrixHarResource har = null;
    public GtMetrixPagespeedResource pagespeed = null;
    public GtMetrixYslowResource yslow = null;

    public GtMetrixBuildAction(AbstractBuild<?, ?> build)
    {
        super();
        this.build = build;
    }

    public static GtMetrixBuildAction load(Build<?, ?> build) {
        return new GtMetrixBuildAction(build);
    }

    public GtMetrixReportResource getReport() {
        if (report != null) {
            return report;
        }

        report = new GtMetrixReportResource(build);
        return report;
    }

    public GtMetrixHarResource getHar() {
        if (har != null) {
            return har;
        }

        har = new GtMetrixHarResource(build);
        return har;
    }

    public GtMetrixPagespeedResource getPagespeed() {
        if (pagespeed != null) {
            return pagespeed;
        }

        pagespeed = new GtMetrixPagespeedResource(build);
        return pagespeed;
    }

    public GtMetrixYslowResource getYslow() {
        if (yslow != null) {
            return yslow;
        }

        yslow = new GtMetrixYslowResource(build);
        return yslow;
    }

    /**
     * Returns the path to the JDepend page
     * @see hudson.model.Action#getUrlName()
     */
    public String getUrlName() {
        return "gtmetrix";
    }

    /**
     * Return the JDepend display name
     * @see hudson.model.Action#getDisplayName()
     */
    public String getDisplayName() {
        return "GT Metrix";
    }

    /**
     * Return the JDepend icon path
     * @see hudson.model.Action#getIconFileName()
     */
    public String getIconFileName() {
        return "graph.gif";
    }
}
