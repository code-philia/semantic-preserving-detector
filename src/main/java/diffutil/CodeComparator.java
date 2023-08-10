package diffutil;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CodeComparator {

    private static final int CONTEXT_SIZE = 5;
    private DiffFileWriter diffFileWriter;

    public CodeComparator(DiffFileWriter diffFileWriter) {  // 修改构造函数
        this.diffFileWriter = diffFileWriter;
    }

    public void compareParts(MethodDeclaration commitMethod, List<Statement> commitStatements, List<Statement> parentStatements, Map<String, Pair<String, String>> changedParts) {
        int i = 0, j = 0;

        while (i < commitStatements.size() && j < parentStatements.size()) {
            Statement commitStatement = commitStatements.get(i);
            Statement parentStatement = parentStatements.get(j);

            if (commitStatement.toString().equals(parentStatement.toString())) {
                i++;
                j++;
            } else {
                Pair<Integer, Integer> newIndices = processDifferingStatements(commitMethod, commitStatements, parentStatements, i, j, changedParts);
                i = newIndices.getLeft();
                j = newIndices.getRight();
            }
        }
    }

    private Pair<Integer, Integer> processDifferingStatements(MethodDeclaration commitMethod, List<Statement> commitStatements, List<Statement> parentStatements, int i, int j, Map<String, Pair<String, String>> changedParts) {
        boolean commitStatementFoundInParent = isStatementInListStartingFromIndex(commitStatements.get(i), parentStatements, j);
        boolean parentStatementFoundInCommit = isStatementInListStartingFromIndex(parentStatements.get(j), commitStatements, i);

        if (!commitStatementFoundInParent && !parentStatementFoundInCommit) {
            String context = generateContextForCommitStatement(commitMethod, commitStatements, i);
            Pair<String, String> diffs = generateDiffWithContext(commitStatements.toString(), parentStatements.toString());
            changedParts.put(commitMethod.getNameAsString() + "_" + i, diffs);

            try {
                diffFileWriter.printFileChange(commitMethod.getSignature().asString(), changedParts);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to write to the file due to: " + e.getMessage());
                // TODO: 添加额外的处理逻辑
            }


            changedParts.clear();
            i++;
            j++;
        } else if (commitStatementFoundInParent) {
            i++;
        } else {
            j++;
        }

        return Pair.of(i, j);
    }

    private boolean isStatementInListStartingFromIndex(Statement statement, List<Statement> statements, int startIndex) {
        for (int k = startIndex; k < statements.size(); k++) {
            if (statement.toString().equals(statements.get(k).toString())) {
                return true;
            }
        }
        return false;
    }

    private String generateContextForCommitStatement(MethodDeclaration commitMethod, List<Statement> commitStatements, int i) {
        StringBuilder context = new StringBuilder(commitMethod.getSignature().asString() + "\n");
        if (i > 0) context.append(commitStatements.get(i - 1).toString()).append("\n");
        context.append(commitStatements.get(i).toString());
        if (i < commitStatements.size() - 1) context.append("\n").append(commitStatements.get(i + 1).toString());

        return context.toString();
    }

    private static Pair<String, String> generateDiffWithContext(String oldCode, String newCode) {
        List<String> originalLines = Arrays.asList(oldCode.split("\n"));
        List<String> revisedLines = Arrays.asList(newCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(originalLines, revisedLines);
        StringBuilder originalDiff = new StringBuilder();
        StringBuilder revisedDiff = new StringBuilder();

        for (difflib.Delta<String> delta : patch.getDeltas()) {
            int originalStart = Math.max(delta.getOriginal().getPosition() - CONTEXT_SIZE, 0);
            int originalEnd = Math.min(delta.getOriginal().getPosition() + delta.getOriginal().getLines().size() + CONTEXT_SIZE, originalLines.size());
            //List<String> originalContext = originalLines.subList(originalStart, originalEnd);

            int revisedStart = Math.max(delta.getRevised().getPosition() - CONTEXT_SIZE, 0);
            int revisedEnd = Math.min(delta.getRevised().getPosition() + delta.getRevised().getLines().size() + CONTEXT_SIZE, revisedLines.size());
            //List<String> revisedContext = revisedLines.subList(revisedStart, revisedEnd);

            // For original lines context
            for (int i = originalStart; i < originalEnd; i++) {
                if (i >= delta.getOriginal().getPosition() && i < delta.getOriginal().getPosition() + delta.getOriginal().getLines().size()) {
                    originalDiff.append("+ ").append(originalLines.get(i)).append("\n");
                } else {
                    originalDiff.append("  ").append(originalLines.get(i)).append("\n");
                }
            }

            // For revised lines context
            for (int i = revisedStart; i < revisedEnd; i++) {
                if (i >= delta.getRevised().getPosition() && i < delta.getRevised().getPosition() + delta.getRevised().getLines().size()) {
                    revisedDiff.append("- ").append(revisedLines.get(i)).append("\n");
                } else {
                    revisedDiff.append("  ").append(revisedLines.get(i)).append("\n");
                }
            }
        }

        return Pair.of(originalDiff.toString(), revisedDiff.toString());
    }
}


/*

public class CodeComparator {

    private static final int CONTEXT_SIZE = 5;
    private DiffFileWriter diffFileWriter;

    public CodeComparator(DiffFileWriter diffFileWriter) {  // 修改构造函数
        this.diffFileWriter = diffFileWriter;
    }

    public void compareParts(MethodDeclaration commitMethod, List<Statement> commitStatements, List<Statement> parentStatements, Map<String, Pair<String, String>> changedParts) {
        int i = 0, j = 0;

        while (i < commitStatements.size() && j < parentStatements.size()) {
            Statement commitStatement = commitStatements.get(i);
            Statement parentStatement = parentStatements.get(j);

            if (commitStatement.toString().equals(parentStatement.toString())) {
                i++;
                j++;
            } else {
                // 检查这个commitStatement是否存在于后续的parentStatements中
                boolean commitStatementFoundInParent = false;
                for (int k = j; k < parentStatements.size(); k++) {
                    if (commitStatement.toString().equals(parentStatements.get(k).toString())) {
                        commitStatementFoundInParent = true;
                        break;
                    }
                }

                // 检查这个parentStatement是否存在于后续的commitStatements中
                boolean parentStatementFoundInCommit = false;
                for (int k = i; k < commitStatements.size(); k++) {
                    if (parentStatement.toString().equals(commitStatements.get(k).toString())) {
                        parentStatementFoundInCommit = true;
                        break;
                    }
                }

                if (!commitStatementFoundInParent && !parentStatementFoundInCommit) {
                    String context = commitMethod.getSignature().asString() + "\n"; // 提取方法签名
                    if (i > 0) context += commitStatements.get(i - 1).toString() + "\n";  // 上一个语句作为上下文
                    context += commitStatement.toString();
                    if (i < commitStatements.size() - 1)
                        context += "\n" + commitStatements.get(i + 1).toString();  // 下一个语句作为上下文

                    //changedParts.put(commitMethod.getNameAsString(), Pair.of(context, parentStatement.toString()));
                    Pair<String, String> diffs = generateDiffWithContext(commitStatements.toString(), parentStatements.toString());
                    changedParts.put(commitMethod.getNameAsString() + "_" + i, diffs);
                    //diffFileWriter.printFileChange(commitMethod.getSignature().asString(), changedParts);
                    try {
                        diffFileWriter.printFileChange(commitMethod.getSignature().asString(), changedParts);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // TODO: 添加额外的处理逻辑
                    }
                    changedParts.clear();

                    i++;
                    j++;
                } else if (commitStatementFoundInParent) {
                    i++;
                } else {
                    j++;
                }
            }
        }
    }

    private static Pair<String, String> generateDiffWithContext(String oldCode, String newCode) {
        List<String> originalLines = Arrays.asList(oldCode.split("\n"));
        List<String> revisedLines = Arrays.asList(newCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(originalLines, revisedLines);
        StringBuilder originalDiff = new StringBuilder();
        StringBuilder revisedDiff = new StringBuilder();

        for (difflib.Delta<String> delta : patch.getDeltas()) {
            int originalStart = Math.max(delta.getOriginal().getPosition() - CONTEXT_SIZE, 0);
            int originalEnd = Math.min(delta.getOriginal().getPosition() + delta.getOriginal().getLines().size() + CONTEXT_SIZE, originalLines.size());
            //List<String> originalContext = originalLines.subList(originalStart, originalEnd);

            int revisedStart = Math.max(delta.getRevised().getPosition() - CONTEXT_SIZE, 0);
            int revisedEnd = Math.min(delta.getRevised().getPosition() + delta.getRevised().getLines().size() + CONTEXT_SIZE, revisedLines.size());
            //List<String> revisedContext = revisedLines.subList(revisedStart, revisedEnd);

            // For original lines context
            for (int i = originalStart; i < originalEnd; i++) {
                if (i >= delta.getOriginal().getPosition() && i < delta.getOriginal().getPosition() + delta.getOriginal().getLines().size()) {
                    originalDiff.append("+ ").append(originalLines.get(i)).append("\n");
                } else {
                    originalDiff.append("  ").append(originalLines.get(i)).append("\n");
                }
            }

            // For revised lines context
            for (int i = revisedStart; i < revisedEnd; i++) {
                if (i >= delta.getRevised().getPosition() && i < delta.getRevised().getPosition() + delta.getRevised().getLines().size()) {
                    revisedDiff.append("- ").append(revisedLines.get(i)).append("\n");
                } else {
                    revisedDiff.append("  ").append(revisedLines.get(i)).append("\n");
                }
            }
        }

        return Pair.of(originalDiff.toString(), revisedDiff.toString());
    }
}
*/
