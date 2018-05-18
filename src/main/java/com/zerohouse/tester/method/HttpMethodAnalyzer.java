package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpMethodAnalyzer implements MethodAnalyzer {
    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        List<RequestMethod> result = new ArrayList<>();
        RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
        if (methodAnnotation != null)
            Collections.addAll(result, methodAnnotation.method());
        if (method.isAnnotationPresent(GetMapping.class) && !result.contains(RequestMethod.GET))
            result.add(RequestMethod.GET);
        if (method.isAnnotationPresent(PostMapping.class) && !result.contains(RequestMethod.POST))
            result.add(RequestMethod.POST);
        if (method.isAnnotationPresent(DeleteMapping.class) && !result.contains(RequestMethod.DELETE))
            result.add(RequestMethod.DELETE);
        if (method.isAnnotationPresent(PutMapping.class) && !result.contains(RequestMethod.PUT))
            result.add(RequestMethod.PUT);
        apiAnalysis.setMethods((RequestMethod[]) result.toArray());
    }
}
