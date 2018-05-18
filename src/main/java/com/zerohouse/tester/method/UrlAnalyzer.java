package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

public class UrlAnalyzer implements MethodAnalyzer {

    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        String url = method.getDeclaringClass().getAnnotation(RequestMapping.class) != null ?
                method.getDeclaringClass().getAnnotation(RequestMapping.class).value().length != 0 ?
                        method.getDeclaringClass().getAnnotation(RequestMapping.class).value()[0] : "" : "";
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping.value().length != 0)
                url += requestMapping.value()[0];
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping requestMapping = method.getAnnotation(GetMapping.class);
            if (requestMapping.value().length != 0)
                url += requestMapping.value()[0];
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping requestMapping = method.getAnnotation(PostMapping.class);
            if (requestMapping.value().length != 0)
                url += requestMapping.value()[0];
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping requestMapping = method.getAnnotation(PutMapping.class);
            if (requestMapping.value().length != 0)
                url += requestMapping.value()[0];
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping requestMapping = method.getAnnotation(DeleteMapping.class);
            if (requestMapping.value().length != 0)
                url += requestMapping.value()[0];
        }
        apiAnalysis.setUrl(url);
    }
}
