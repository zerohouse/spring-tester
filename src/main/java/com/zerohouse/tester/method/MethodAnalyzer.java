package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;

import java.lang.reflect.Method;

public interface MethodAnalyzer {
    void analyze(Method method, ApiAnalyze apiAnalysis);
}
