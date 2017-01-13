package com.zerohouse.tester.method;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Map;

public class HttpMethodAnalyzer implements MethodAnalyzer {
    @Override
    public void analyze(Method method, Map apiAnalysis) {
        RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
        apiAnalysis.put("methods", methodAnnotation.method());
    }
}
