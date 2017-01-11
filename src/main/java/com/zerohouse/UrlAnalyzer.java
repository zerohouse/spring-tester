package com.zerohouse;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Map;

public class UrlAnalyzer implements MethodAnalyzer {

    @Override
    public void analyze(Method method, Map apiAnalysis) {
        RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
        String url = method.getDeclaringClass().getAnnotation(RequestMapping.class).value()[0];
        if (methodAnnotation.value().length != 0)
            url += methodAnnotation.value()[0];
        apiAnalysis.put("url", url);
    }
}
