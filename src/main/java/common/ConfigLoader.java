package common;

import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private Properties properties;
    public ConfigLoader() {
        this("/config.properties");
    }


    public ConfigLoader(String filename) {
        properties = new Properties();
        try {
            //properties.load(new FileInputStream(filename));
            properties.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRepoUrl() {
        return properties.getProperty("repoUrl");
    }

    public String getLocalPath() {
        return properties.getProperty("localPath");
    }

    public String getCommitId() {
        return properties.getProperty("commitId");
    }

    public String getOutputFilename() {
        return properties.getProperty("outputFile");
    }

    public String getOpenaiApiKey() { return properties.getProperty("openaiApiKey"); }
}

