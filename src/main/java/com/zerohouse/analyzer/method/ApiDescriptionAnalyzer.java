package com.zerohouse.analyzer.method;


import com.zerohouse.annotation.ApiDescription;

import java.lang.reflect.Method;
import java.util.Map;

public class ApiDescriptionAnalyzer implements MethodAnalyzer {
    @Override
    public void analyze(Method method, Map apiAnalysis) {
        if (!method.isAnnotationPresent(ApiDescription.class))
            return;
        ApiDescription apiDescription = method.getAnnotation(ApiDescription.class);
        apiAnalysis.put("name", apiDescription.name());
        apiAnalysis.put("apiFor", apiDescription.apiFor());
        apiAnalysis.put("description", apiDescription.description());
        apiAnalysis.put("parameterDescription", apiDescription.parameterDescription());
    }
}
