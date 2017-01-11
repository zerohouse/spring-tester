package com.zerohouse.analyzer;

import com.zerohouse.analyzer.method.*;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.*;

public class SpringApiAnalyzer {
    private String packagePath;

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;
    List<MethodAnalyzer> methodAnalyzers;

    public SpringApiAnalyzer(String packagePath) {
        this.packagePath = packagePath;
        ignoreAnnotations = new ArrayList<>();
        ignoreClasses = new ArrayList<>();
        methodAnalyzers = new ArrayList<>();
        methodAnalyzers.add(new UrlAnalyzer());
        methodAnalyzers.add(new HttpMethodAnalyzer());
        methodAnalyzers.add(new ApiDescriptionAnalyzer());
        methodAnalyzers.add(new SampleParameterGenerator(ignoreAnnotations, ignoreClasses));
    }

    public List<Map> getApiList() {
        Reflections reflections = new Reflections(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), ClasspathHelper.forPackage(packagePath));
        Set<Method> requestMappingMethods = reflections.getMethodsAnnotatedWith(RequestMapping.class);
        List<Map> apiAnalysisList = new ArrayList<>();

        requestMappingMethods.forEach(method -> {
            Map apiAnalysis = new HashMap<>();
            methodAnalyzers.forEach(methodAnalyzer -> methodAnalyzer.analyze(method, apiAnalysis));
            apiAnalysisList.add(apiAnalysis);
        });
        return apiAnalysisList;
    }

    public void addIgnoreAnnotation(Class aClass) {
        ignoreAnnotations.add(aClass);
    }

    public void addIgnoreClass(Class aClass) {
        ignoreClasses.add(aClass);
    }

    public void addMethodAnalyzer(MethodAnalyzer methodAnalyzer) {
        methodAnalyzers.add(methodAnalyzer);
    }
}
