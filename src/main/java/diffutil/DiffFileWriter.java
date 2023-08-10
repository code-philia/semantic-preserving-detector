package diffutil;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class DiffFileWriter {

    private final BufferedWriter writer;

    public DiffFileWriter(String filename) throws IOException {
        // 使用 FileWriter 时，将其第二个参数设置为 false，这样就可以覆盖文件内容
        this.writer = new BufferedWriter(new FileWriter(filename, false));
    }

    public void printFileChange(String commitMethodSignature, Map<String, Pair<String, String>> changedParts) throws IOException {
        for (Map.Entry<String, Pair<String, String>> entry : changedParts.entrySet()) {
            writer.write("Method: " + commitMethodSignature  + "\n");
            writer.write("Commit Version: \n");
            writer.write(entry.getValue().getLeft() + "\n");
            writer.write("Parent Version: \n");
            writer.write(entry.getValue().getRight() + "\n");
            writer.write("---------------- METHOD BREAK ---------------\n");
        }
    }

    public void close() throws IOException {
        writer.close();
    }
}

