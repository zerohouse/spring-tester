package com.zerohouse.tester.method;

import java.lang.reflect.Method;
import java.util.Map;

public interface MethodAnalyzer {
    void analyze(Method method, Map apiAnalysis);
}
