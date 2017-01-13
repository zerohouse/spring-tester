package com.zerohouse.tester.method;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SampleParameterGenerator implements MethodAnalyzer {

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;

    public SampleParameterGenerator(List<Class> ignoreAnnotations, List<Class> ignoreClasses) {
        this.ignoreAnnotations = ignoreAnnotations;
        this.ignoreClasses = ignoreClasses;
    }

    @Override
    public void analyze(Method method, Map apiAnalysis) {
        if (!Arrays.stream(method.getParameters()).anyMatch(parameter -> parameter.isAnnotationPresent(RequestBody.class))) {
            HashMap<String, String> params = new HashMap<>();
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (ignoreAnnotations.stream().anyMatch(parameter::isAnnotationPresent))
                    return;
                if (ignoreClasses.stream().anyMatch(aClass -> aClass.equals(parameter.getType())))
                    return;
                params.put(parameterNameDiscoverer.getParameterNames(method)[i], "");
            }
            apiAnalysis.put("parameter", params);
            return;
        }
        Parameter find = Arrays.stream(method.getParameters()).filter(parameter -> parameter.isAnnotationPresent(RequestBody.class)).findAny().get();
        try {
            apiAnalysis.put("json", true);
            if (find.getType() == List.class || find.getType().isArray())
                apiAnalysis.put("parameter", new ArrayList<>());
            else if (find.getType() == Map.class)
                apiAnalysis.put("parameter", new HashMap<>());
            else
                apiAnalysis.put("parameter", find.getType().newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
