package diffutil;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeComparatorTest {
    private CodeComparator comparator;
    private DiffFileWriter mockDiffFileWriter;

    @BeforeEach
    public void setup() {
        // Use a mock DiffFileWriter to prevent actual file writes during testing
        mockDiffFileWriter = Mockito.mock(DiffFileWriter.class);
        comparator = new CodeComparator(mockDiffFileWriter);
    }

    @Test
    public void testCompareIdenticalStatements() {
        List<Statement> commitStatements = Arrays.asList(
                StaticJavaParser.parseStatement("int a = 10;"),
                StaticJavaParser.parseStatement("System.out.println(a);")
        );

        List<Statement> parentStatements = Arrays.asList(
                StaticJavaParser.parseStatement("int a = 10;"),
                StaticJavaParser.parseStatement("System.out.println(a);")
        );

        Map<String, Pair<String, String>> diffs = new HashMap<>();
        MethodDeclaration dummyMethod = StaticJavaParser.parseMethodDeclaration("public void dummyMethod() {}");
        comparator.compareParts(dummyMethod, commitStatements, parentStatements, diffs);


        assertTrue(diffs.isEmpty());
    }

    @Test
    public void testCompareDifferingStatements() {
        List<Statement> commitStatements = Arrays.asList(
                StaticJavaParser.parseStatement("object.put(key, value);"),
                StaticJavaParser.parseStatement("System.out.println(a);")
        );

        List<Statement> parentStatements = Arrays.asList(
                StaticJavaParser.parseStatement("map.put(key, value);"),
                StaticJavaParser.parseStatement("System.out.println(a);")
        );

        Map<String, Pair<String, String>> diffs = new HashMap<>();
        MethodDeclaration dummyMethod = StaticJavaParser.parseMethodDeclaration("public void dummyMethod() {}");
        comparator.compareParts(dummyMethod, commitStatements, parentStatements, diffs);


        assertTrue(diffs.isEmpty());
        assertFalse(diffs.containsKey("dummyMethod_0"));

    }



}




