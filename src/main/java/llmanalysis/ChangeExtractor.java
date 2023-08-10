package llmanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChangeExtractor {

    private final String fileName;

    public ChangeExtractor(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getChangesFromTxt() throws IOException {
        List<String> changes = new ArrayList<>();
        StringBuilder change = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("---------------- METHOD BREAK ---------------")) {
                    changes.add(change.toString().trim());
                    change = new StringBuilder();
                } else {
                    change.append(line).append("\n");
                }
            }
        }

        return changes;
    }
}
