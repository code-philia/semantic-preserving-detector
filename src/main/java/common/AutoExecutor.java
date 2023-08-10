package common;

import diffutil.MainDiffApp;
import llmanalysis.AnalysisExecutor;

public class AutoExecutor {
    public static void main(String[] args) throws Exception {  // 使用 Exception 或更具体的异常
        MainDiffApp.runDiffAnalysis();
        AnalysisExecutor.runAnalysis();
    }
}

