package org.jenkinsci.plugins.gtmetrix;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.remoting.Base64;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.methods.GetMethod;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Sample {@link Builder}.
 * <p/>
 * <p/>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link GtMetrixBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #url})
 * to remember the configuration.
 * <p/>
 * <p/>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class GtMetrixBuilder extends Builder {

    private final String url;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public GtMetrixBuilder(String url) {
        this.url = url;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getUrl() {
        return url;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.

        // SET UP REQUESTER CLASS FOR MAKING ALL API CALLS
        ApiRequester requester = new ApiRequester(
            "https://gtmetrix.com/api/0.1",
            getDescriptor().getEmail(),
            getDescriptor().getApiKey()
        );

        try {
            // MAKE API CALL TO GENERATE REPORT
            HashMap params = new HashMap();
            params.put("url", url);

            JSONObject response = requester.post("test", params);

            if (requester.getLastStatusCode() != 200) {
                listener.getLogger().println("Error generating report: " + response.getString("error"));
                return false;
            }

            // POLL THE API FOR THE REPORT TO BE DONE
            String pollStateUrl = response.getString("poll_state_url");

            int counter = 0;
            int maxCounts = 60; // ONLY POLL 60 TIMES

            do {
                counter++;
                if (counter >= maxCounts) {
                    throw new Exception("Timeout: Hit Gt Metrix max counts");
                }

                Thread.sleep(5000); // WAIT 5 SECONDS BETWEEN EACH POLL

                // MAKE API CALL TO POLL
                response = requester.get(pollStateUrl);

                if (requester.getLastStatusCode() != 200) {
                    listener.getLogger().println("Error polling report (pollStateUrl): " + response.getString("error"));
                    return false;
                }
            // IF THE REPORT IS STILL "QUEUED" OR "STARTED" POLL AGAIN
            } while (response.getString("state").equals("queued") || response.getString("state").equals("started"));

            // REPORT FAILED
            if (response.getString("state").equals("error")) {
                listener.getLogger().println("Report failed: " + response.getString("error"));
                return false;
            }

            // SET UP DOWNLOADER FOR DOWNLOADING ALL FILES
            Downloader downloader = new Downloader(build, launcher, listener, "gtmetrix");
            downloader.setAuth(
                getDescriptor().getEmail(),
                getDescriptor().getApiKey()
            );

            HashMap filesToDownload = new HashMap();

            // FILES WE WANT IN OUR WORKSPACE AND OUR ARCHIVE
            // These are files that the user can view from the jenkins web app
            filesToDownload.put(response.getJSONObject("resources").getString("report_pdf"), "report.pdf");
            filesToDownload.put(response.getJSONObject("resources").getString("report_pdf_full"), "full_report.pdf");
            filesToDownload.put(response.getJSONObject("resources").getString("screenshot"), "screenshot.png");

            listener.getLogger().println("Downloading workspace files");
            downloader.download(filesToDownload, build.getWorkspace());
            listener.getLogger().println("Archiving workspace files");
            downloader.archive(filesToDownload);

            filesToDownload = new HashMap();

            // FILES WE WANT IN OUR BUILD DIR
            filesToDownload.put(pollStateUrl, "report.json");
            filesToDownload.put(response.getJSONObject("resources").getString("pagespeed"), "pagespeed.json");
            filesToDownload.put(response.getJSONObject("resources").getString("har"), "har.json");
            filesToDownload.put(response.getJSONObject("resources").getString("pagespeed_files"), "pagespeed_files.json");
            filesToDownload.put(response.getJSONObject("resources").getString("yslow"), "yslow.json");

            listener.getLogger().println("Downloading build files");
            downloader.download(filesToDownload, new FilePath(build.getRootDir()));

        } catch (Exception e) {
            listener.getLogger().println(e);
            return false;
        }

        // ADD OUR CUSTOM BUILD ACTION (SIDEBAR LINK ON BUILD PAGE)
        build.getActions().add(new GtMetrixBuildAction(build));

        // ADD OUR CUSTOM PROJECT ACTION (SUMMARY "FLOATING BOX" ON PROJECT PAGE)
        //build.getProject().getActions().add(new GtMetrixProjectAction(build.getProject()));

        return true;
    }

    /**
     * Create a project action for a project.
     * @param project the project to create the action for.
     * @return the created violations project action.
     */
    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new GtMetrixProjectAction(project);
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link GtMetrixBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/gtmetrix/GtMetrix/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * <p/>
         * <p/>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String email;
        private String apiKey;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckUrl(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a url");
            if (!value.startsWith("http://") && !value.startsWith("https://"))
                return FormValidation.error("Url must start with http:// or https://");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Perform GT Metrix";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            apiKey = formData.getString("apiKey");
            email = formData.getString("email");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setApiKey)
            save();
            return super.configure(req, formData);
        }

        /**
         * This method return the GT Metrix API key
         */
        public String getApiKey() {
            return apiKey;
        }

        public String getEmail() {
            return email;
        }
    }
}

