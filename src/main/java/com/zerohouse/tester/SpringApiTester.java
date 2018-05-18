package com.zerohouse.tester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohouse.tester.analyze.ApiAnalyze;
import com.zerohouse.tester.annotation.ExcludeApi;
import com.zerohouse.tester.field.FieldDescription;
import com.zerohouse.tester.method.*;
import com.zerohouse.tester.method.util.ParameterIgnoreChecker;
import com.zerohouse.tester.method.util.ParameterMaker;
import com.zerohouse.tester.method.util.ResponseMaker;
import com.zerohouse.tester.spec.ResponseSampleProcessor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class SpringApiTester extends SimpleUrlHandlerMapping {
    private ObjectMapper objectMapper;
    private String title;
    private String url;

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;
    List<ResponseSampleProcessor> responseSampleProcessors;
    List<MethodAnalyzer> methodAnalyzers;
    Map<String, String> httpHeaders;
    Map<Class, Object> defaultValues;
    Set<Method> requestMappingMethods;

    @Setter
    Map<String, String> tableHeaders;

    @Setter
    Map<String, String> additionalExplain;

    public void newColumn(String columnPath, String name) {
        tableHeaders.put(columnPath, name);
    }

    public SpringApiTester(String packagePath, String pageTitle, String url) {
        this(packagePath, url, pageTitle, null, new ObjectMapper());
    }

    public SpringApiTester(String packagePath, String url, String pageTitle, Object defaultResponse) {
        this(packagePath, url, pageTitle, defaultResponse, new ObjectMapper());
    }

    public SpringApiTester(String packagePath, String url, String pageTitle, Object defaultResponse, ObjectMapper objectMapper) {
        this.objectMapper = FieldDescription.objectMapper = objectMapper;
        httpHeaders = new LinkedHashMap<>();
        tableHeaders = new LinkedHashMap<>();
        additionalExplain = new LinkedHashMap<>();
        responseSampleProcessors = new ArrayList<>();
        tableHeaders.put("name", "Name");

        tableHeaders.put("url", "Url");
        tableHeaders.put("methodsString", "Method");
        tableHeaders.put("paramNames", "Parameters");

        defaultValues = new HashMap<>();
        defaultValues.put(String.class, "");
        defaultValues.put(Integer.class, 0);
        defaultValues.put(int.class, 0);
        defaultValues.put(Double.class, 0d);
        defaultValues.put(double.class, 0);
        defaultValues.put(Long.class, 0L);
        defaultValues.put(long.class, 0);
        defaultValues.put(Float.class, 0f);
        defaultValues.put(float.class, 0);
        defaultValues.put(Boolean.class, false);
        defaultValues.put(boolean.class, 0);
        defaultValues.put(Date.class, new Date());

        ignoreAnnotations = new ArrayList<>();
        ignoreClasses = new ArrayList<>();
        ignoreClasses.add(HttpServletRequest.class);
        ignoreClasses.add(HttpServletResponse.class);
        methodAnalyzers = new ArrayList<>();
        methodAnalyzers.add(new UrlAnalyzer());
        methodAnalyzers.add(new HttpMethodAnalyzer());
        ResponseMaker responseMaker = new ResponseMaker(defaultValues, responseSampleProcessors, defaultResponse);
        methodAnalyzers.add(new ApiDescriptionAnalyzer(responseMaker));
        ParameterIgnoreChecker parameterIgnoreChecker = new ParameterIgnoreChecker(ignoreAnnotations, ignoreClasses);
        ParameterMaker parameterMaker = new ParameterMaker(defaultValues);
        methodAnalyzers.add(new SampleParameterGenerator(parameterIgnoreChecker, parameterMaker));
        methodAnalyzers.add(new ParameterNamesGenerator(parameterIgnoreChecker));
        methodAnalyzers.add(new ParameterDescriptionGenerator(ignoreAnnotations, ignoreClasses));
        Reflections reflections = new Reflections(new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), ClasspathHelper.forPackage(packagePath));
        requestMappingMethods = reflections.getMethodsAnnotatedWith(RequestMapping.class);
        requestMappingMethods = reflections.getMethodsAnnotatedWith(GetMapping.class);
        requestMappingMethods = reflections.getMethodsAnnotatedWith(PostMapping.class);
        requestMappingMethods = reflections.getMethodsAnnotatedWith(DeleteMapping.class);
        requestMappingMethods = reflections.getMethodsAnnotatedWith(PutMapping.class);
        methodAnalyzers.add(new ExceptionResponseAnalyzer(responseMaker, reflections.getMethodsAnnotatedWith(ExceptionHandler.class)));
        this.title = pageTitle;
        this.url = url;
    }

    public void generate() {
        Map<String, Object> urlMap = new HashMap<>();
        try {
            String html = this.getTestPageHtml();
            urlMap.put(this.url, (HttpRequestHandler) (request, response) -> {
                PrintWriter writer = response.getWriter();
                writer.write(html);
            });
            this.setUrlMap(urlMap);
        } catch (IOException e) {
            System.out.printf("Spring API Tester - 페이지 생성중 오류가 발생했습니다.");
        }
    }

    public List<Map> getApiList() {
        List<ApiAnalyze> apiAnalysisList = new ArrayList<>();
        requestMappingMethods.stream()
                .filter(method -> !method.getDeclaringClass().isAnnotationPresent(ExcludeApi.class) && method.getAnnotation(ExcludeApi.class) == null)
                .forEach(method -> {
                    ApiAnalyze apiAnalysis = new ApiAnalyze();
                    methodAnalyzers.forEach(methodAnalyzer -> methodAnalyzer.analyze(method, apiAnalysis));
                    apiAnalysisList.add(apiAnalysis);
                });
        return apiAnalysisList.stream().map(ApiAnalyze::toMap).collect(Collectors.toList());
    }

    public String getTestPageHtml() throws IOException {
        String html = getStringFromFile("/tester.html");
        String vendor = getStringFromFile("/vendor.js");
        String js = getStringFromFile("/tester.js");
        String injects = "var apis =" + objectMapper.writeValueAsString(getApiList()) + ";";
        injects += "var headers =" + objectMapper.writeValueAsString(httpHeaders) + ";";
        injects += "var tableHeaders =" + objectMapper.writeValueAsString(tableHeaders) + ";";
        injects += "var additionalExplain =" + objectMapper.writeValueAsString(additionalExplain) + ";";
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

    public void putDefaultValue(Class aClass, Object value) {
        defaultValues.put(aClass, value);
    }

    public void putHttpHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public void addPostProcessor(ResponseSampleProcessor responseSampleProcessor) {
        responseSampleProcessors.add(responseSampleProcessor);
    }
}
