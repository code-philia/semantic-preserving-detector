package diffutil;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffAnalyzer {

    private CodeComparator codeComparator;

    /*public DiffAnalyzer() {
        this.codeComparator = new CodeComparator();
    }*/

    public DiffAnalyzer(String outputFileName) {
        try {
            DiffFileWriter diffFileWriter = new DiffFileWriter(outputFileName);
            this.codeComparator = new CodeComparator(diffFileWriter);
        } catch (IOException e) {
            e.printStackTrace();
            // 这里可以设置codeComparator为null或提供一个默认的实现。
        }
    }


    public DiffAnalyzer(DiffFileWriter diffFileWriter) {
        this.codeComparator = new CodeComparator(diffFileWriter);
    }

    public Map<String, Pair<String, String>> analyzeDifferences(Repository repository, String commitHash) throws Exception {
        ObjectId commitId = repository.resolve(commitHash);

        Map<String, Pair<String, String>> changedMethods = new HashMap<>();

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitId);
            RevCommit parent = revWalk.parseCommit(commit.getParent(0).getId());

            // Create a DiffFormatter to get the modified files
            try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                diffFormatter.setRepository(repository);
                List<DiffEntry> diffs = diffFormatter.scan(parent.getTree(), commit.getTree());

                for (DiffEntry diff : diffs) {
                    String path = diff.getNewPath();
                    if (path.endsWith(".java")) {
                        System.out.println("Processing file: " + path);

                        byte[] commitData = repository.open(diff.getNewId().toObjectId()).getBytes();
                        if (diff.getChangeType() == DiffEntry.ChangeType.ADD || diff.getChangeType() == DiffEntry.ChangeType.DELETE) {
                            continue;
                        }
                        byte[] parentData = repository.open(diff.getOldId().toObjectId()).getBytes();

                        CompilationUnit commitCu = StaticJavaParser.parse(new String(commitData));
                        CompilationUnit parentCu = StaticJavaParser.parse(new String(parentData));

                        List<MethodDeclaration> commitMethods = commitCu.findAll(MethodDeclaration.class);
                        List<MethodDeclaration> parentMethods = parentCu.findAll(MethodDeclaration.class);

                        for (MethodDeclaration commitMethod : commitMethods) {
                            for (MethodDeclaration parentMethod : parentMethods) {
                                if (commitMethod.getSignature().equals(parentMethod.getSignature())) {
                                    String commitBody = commitMethod.getBody().map(Object::toString).orElse("");
                                    String parentBody = parentMethod.getBody().map(Object::toString).orElse("");

                                    if (!commitBody.equals(parentBody)) {
                                        System.out.println("Modified method detected: " + commitMethod.getSignature());
                                        codeComparator.compareParts(commitMethod, commitMethod.getBody().get().getStatements(), parentMethod.getBody().get().getStatements(), changedMethods);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return changedMethods;
    }
}


