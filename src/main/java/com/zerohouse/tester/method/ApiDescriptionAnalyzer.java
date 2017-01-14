package com.zerohouse.tester.method;


import com.zerohouse.tester.annotation.Api;

import java.lang.reflect.Method;
import java.util.Map;

public class ApiDescriptionAnalyzer implements MethodAnalyzer {
    @Override
    public void analyze(Method method, Map apiAnalysis) {
        if (!method.isAnnotationPresent(Api.class))
            return;
        Api apiDescription = method.getAnnotation(Api.class);
        apiAnalysis.put("value", apiDescription.value());
        apiAnalysis.put("description", apiDescription.description());
    }
}
