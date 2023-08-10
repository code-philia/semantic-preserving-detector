package llmanalysis;

import com.theokanning.openai.completion.CompletionChoice;
import common.ConfigLoader;

import java.io.IOException;
import java.util.List;

public class AnalysisExecutor {

    public static void main(String[] args) throws IOException, InterruptedException {
        runAnalysis();
    }

    public static void runAnalysis() throws IOException, InterruptedException {
        ConfigLoader config = new ConfigLoader();
        String FILENAME = config.getOutputFilename();
        String OPENAI_API_KEY = config.getOpenaiApiKey();

        ChangeExtractor extractor = new ChangeExtractor(FILENAME);
        List<String> changes = extractor.getChangesFromTxt();

        OpenAiServiceModule serviceModule = new OpenAiServiceModule(OPENAI_API_KEY, 5000);

        for (int i = 0; i < changes.size(); i++) {
            String change = changes.get(i);

            List<CompletionChoice> choices = serviceModule.getSemanticAnalysis(change);
            choices.forEach(System.out::println);

            if (i != changes.size() - 1) {
                Thread.sleep(30000);
            }
        }
    }
}

