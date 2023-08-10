package llmanalysis;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

import java.util.List;

public class OpenAiServiceModule {

    private final String apiKey;
    private final int timeout;
    private final OpenAiService service;

    public OpenAiServiceModule(String openaiApiKey, int timeout) {
        this.apiKey = openaiApiKey;
        this.timeout = timeout;
        this.service = new OpenAiService(apiKey, timeout);
    }

    public List<CompletionChoice> getSemanticAnalysis(String change) {
        CompletionRequest request = CompletionRequest.builder()
                .model("text-davinci-003")
                //.prompt("请判断两个版本中方法的变化是否是语义保留的。为什么?\n" + change)
                .prompt("Please determine whether the change of method in the two versions is semantically preserved. Why?\n" + change)
                .temperature(0D)
                //.maxTokens(1000)
                .build();

        return service.createCompletion(request).getChoices();
    }
}
