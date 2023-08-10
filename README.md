# Semantic Preserving Detector
The semantic-preserving-detector project is a tool designed to compare Java code from different Git commits and detect if the changes made in the codebase preserve the semantic meaning. The purpose is to assist developers in ensuring that their code refactorings or optimizations do not inadvertently change the behavior of the program.

### Features

* Detailed Analysis: With the DiffAnalyzer class, the project dives deeper into repositories to extract changes from different commits, ensuring that each modification is accounted for.

* Code Comparison: Leveraging the CodeComparator class, the tool can detect differences between Java methods in two different code versions.

* Output Management: Through DiffFileWriter, the tool writes the detected differences into human-readable formats, helping developers easily understand and trace the changes.

* Semantic Analysis: Once the differences are captured, the tool determines whether the changes are semantically preserving or not. This deeper level of understanding ensures that developers grasp the implications of changes made.


### Environment Setup
* Maven (mvn): This tool uses Maven for dependency management.
* JDK: Ensure you have JDK 11 installed.
* OpenAI API Key: This tool requires access to the OpenAI API. Ensure you have a valid API key.


### How to Use
* Modify Configurations: Adjust necessary configurations in the path: src/main/resources/config.properties to suit your requirements.

* Capture Code Changes: To detect differences in code, run the MainDiffApp.java located in the diffutil package.  The scope of the context can be customized by changing the CONTEXT_SIZE. This will analyze the repository and output detected code changes.

* Analyze Semantic Preserving Changes: With the saved changes from the previous step, you can run AnalysisExecutor.java from the llmanalysis package to determine if the changes are semantically preserving.
* You can also directly execute AutoExecutor to get the final analysis results on whether the detected changes are semantically preserving or not.
### Output
* Modified Code Capture: The tool provides the code modified after the submission and the context.This helps developers trace back to their modifications.

* Semantic Analysis: After analyzing, the tool outputs whether the changes are semantically preserving or not, offering a deeper understanding of code modifications.

### Conclusion
Semantic-Preserving-Detector offers a comprehensive solution to understanding and analyzing code changes in Java projects. It is not just about detecting differences but also about understanding the implications of those changes on the code's semantics. Make your code reviews more meaningful and insightful with this tool.
