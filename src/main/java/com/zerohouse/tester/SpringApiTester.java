package com.zerohouse.tester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohouse.tester.method.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.*;

public class SpringApiTester {
    private ObjectMapper objectMapper;
    private String packagePath;
    private String title;

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;
    List<MethodAnalyzer> methodAnalyzers;
    Map<String, String> httpHeaders;

    public Map<String, String> getTableHeaders() {
        return tableHeaders;
    }

    public void setTableHeaders(Map<String, String> tableHeaders) {
        this.tableHeaders = tableHeaders;
    }

    Map<String, String> tableHeaders;


    public SpringApiTester() {
    }

    public SpringApiTester(String packagePath) {
        objectMapper = new ObjectMapper();
        httpHeaders = new HashMap<>();
        tableHeaders = new HashMap<>();
        tableHeaders.put("name", "이름");
        tableHeaders.put("url", "Url");
        tableHeaders.put("method", "Method");
        tableHeaders.put("paramNames", "Parameters");

        this.packagePath = packagePath;
        ignoreAnnotations = new ArrayList<>();
        ignoreClasses = new ArrayList<>();
        methodAnalyzers = new ArrayList<>();
        methodAnalyzers.add(new UrlAnalyzer());
        methodAnalyzers.add(new HttpMethodAnalyzer());
        methodAnalyzers.add(new ApiDescriptionAnalyzer());
        methodAnalyzers.add(new SampleParameterGenerator(ignoreAnnotations, ignoreClasses));
        methodAnalyzers.add(new ParameterNamesGenerator(ignoreAnnotations, ignoreClasses));
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

    public String getTestPageHtml() throws IOException {
        String html = getStringFromFile("/tester.html");
        String vendor = getStringFromFile("/vendor.js");
        String js = getStringFromFile("/tester.js");
        String injects = "var apis =" + objectMapper.writeValueAsString(getApiList()) + ";";
        injects += "var headers =" + objectMapper.writeValueAsString(httpHeaders) + ";";
        injects += "var tableHeaders =" + objectMapper.writeValueAsString(tableHeaders) + ";";
        if (this.title != null)
            injects += "var title = '" + this.title + "';";
        html = html.replace("<script src=\"vendor.js\" type=\"text/javascript\"></script>", "<script>" + vendor + "</script>");
        html = html.replace("<script src=\"tester.js\" type=\"text/javascript\"></script>", "<script>" + injects + js + "</script>");
        return html;
    }

    public void generateTestPageHtml(String path) {
        try {
            FileUtils.writeStringToFile(new File(path), getTestPageHtml(), "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStringFromFile(String path) throws IOException {
        BufferedReader txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
        return IOUtils.toString(txtReader);
    }

    public void addParameterIgnoreAnnotation(Class aClass) {
        ignoreAnnotations.add(aClass);
    }

    public void addParameterIgnoreClass(Class aClass) {
        ignoreClasses.add(aClass);
    }

    public void addMethodAnalyzer(MethodAnalyzer methodAnalyzer) {
        methodAnalyzers.add(methodAnalyzer);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void putHttpHeader(String key, String value) {
        httpHeaders.put(key, value);
    }
}
