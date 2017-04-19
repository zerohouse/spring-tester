package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

public class HttpMethodAnalyzer implements MethodAnalyzer {
    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
        apiAnalysis.setMethods(methodAnnotation.method());
    }
}
