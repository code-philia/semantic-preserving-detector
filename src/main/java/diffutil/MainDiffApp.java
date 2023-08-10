package diffutil;

import common.ConfigLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;
import java.util.Map;

// The main application that groups all the modules together.

public class MainDiffApp {

    public static void main(String[] args) {
        runDiffAnalysis();
    }

    public static void runDiffAnalysis() {
        ConfigLoader config = new ConfigLoader("config.properties");
        GitRepoHandler gitHandler = new GitRepoHandler(config.getRepoUrl(), config.getLocalPath());
        DiffFileWriter writer = null;

        try {
            Repository repo = gitHandler.cloneRepository();
            writer = new DiffFileWriter(config.getOutputFilename());

            DiffAnalyzer analyzer = new DiffAnalyzer(writer);
            Map<String, Pair<String, String>> differences = analyzer.analyzeDifferences(repo, config.getCommitId());

            for (String methodSignature : differences.keySet()) {
                writer.printFileChange(methodSignature, differences);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void runDiffAnalysis() {
        ConfigLoader config = new ConfigLoader("config.properties");

        GitRepoHandler gitHandler = new GitRepoHandler(config.getRepoUrl(), config.getLocalPath());
        try {
            Repository repo = gitHandler.cloneRepository();
            DiffFileWriter writer = new DiffFileWriter(config.getOutputFilename());

            DiffAnalyzer analyzer = new DiffAnalyzer(writer);
            Map<String, Pair<String, String>> differences = analyzer.analyzeDifferences(repo, config.getCommitId());

            for (String methodSignature : differences.keySet()) {
                writer.printFileChange(methodSignature, differences);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}



