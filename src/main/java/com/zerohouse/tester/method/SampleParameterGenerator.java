package com.zerohouse.tester.method;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohouse.tester.annotation.Api;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SampleParameterGenerator implements MethodAnalyzer {

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;
    Map<Class, Object> defaultValues;
    ObjectMapper objectMapper = new ObjectMapper();

    public SampleParameterGenerator(List<Class> ignoreAnnotations, List<Class> ignoreClasses, Map<Class, Object> defaultValues) {
        this.ignoreAnnotations = ignoreAnnotations;
        this.ignoreClasses = ignoreClasses;
        this.defaultValues = defaultValues;
    }

    @Override
    public void analyze(Method method, Map apiAnalysis) {
        Parameter find = Arrays.stream(method.getParameters()).filter(parameter -> parameter.isAnnotationPresent(RequestBody.class)).findAny().get();
        apiAnalysis.put("json", true);
        if (method.isAnnotationPresent(Api.class)) {
            Api apiDescription = method.getAnnotation(Api.class);
            if (!"".equals(apiDescription.parameter())) {
                try {
                    apiAnalysis.put("parameter", objectMapper.readValue(apiDescription.parameter().replace("'", "\""), apiDescription.parameterType()));
                } catch (IOException e) {
                    apiAnalysis.put("parameter", new HashMap<>());
                    e.printStackTrace();
                }
                return;
            }
        }
        if (!Arrays.stream(method.getParameters()).anyMatch(parameter -> parameter.isAnnotationPresent(RequestBody.class))) {
            HashMap<String, String> params = new HashMap<>();
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (ignoreAnnotations.stream().anyMatch(parameter::isAnnotationPresent))
                    continue;
                if (ignoreClasses.stream().anyMatch(aClass -> aClass.equals(parameter.getType())))
                    continue;
                params.put(parameterNameDiscoverer.getParameterNames(method)[i], "");
            }
            apiAnalysis.put("parameter", params);
            return;
        }
        if (find.getType() == List.class || find.getType().isArray())
            apiAnalysis.put("parameter", new ArrayList<>());
        else if (find.getType() == Map.class)
            apiAnalysis.put("parameter", new HashMap<>());
        else
            try {
                apiAnalysis.put("parameter", find.getType().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
    }
}
