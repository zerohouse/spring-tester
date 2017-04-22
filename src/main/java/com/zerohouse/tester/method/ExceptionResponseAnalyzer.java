package com.zerohouse.tester.method;


import com.zerohouse.tester.analyze.ApiAnalyze;
import com.zerohouse.tester.analyze.ResponseDesc;
import com.zerohouse.tester.annotation.Api;
import com.zerohouse.tester.annotation.ExceptionResponse;
import com.zerohouse.tester.method.util.ResponseMaker;
import org.reflections.Reflections;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ExceptionResponseAnalyzer implements MethodAnalyzer {

    Map<Class, Object> responseMap;
    ResponseMaker responseMaker;

    public ExceptionResponseAnalyzer(ResponseMaker responseMaker, Reflections reflections) {
        responseMap = new HashMap<>();
        this.responseMaker = responseMaker;
        reflections.getMethodsAnnotatedWith(ExceptionHandler.class).forEach(method -> {
            ExceptionResponse exceptionResponse = method.getAnnotation(ExceptionResponse.class);
            if (exceptionResponse == null)
                return;
            Class<?> returnType = void.class.equals(exceptionResponse.type()) ? method.getReturnType() : exceptionResponse.type();
            Object o = responseMaker.makePrimitiveElseJsonWithoutPostProcess(returnType, exceptionResponse.value());
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            Arrays.stream(exceptionHandler.value()).forEach(aClass -> {
                responseMap.put(aClass, o);
            });
        });
    }


    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        Api apiDescription = method.getAnnotation(Api.class);
        List<ResponseDesc> responseDescList = new ArrayList<>();
        Arrays.stream(method.getExceptionTypes()).forEach(aClass -> {
            Object o = responseMaker.postProcess(responseMap.get(aClass));
            List<ResponseDesc> responseDescs = Arrays.stream(apiDescription.exceptions())
                    .filter(exceptionDescription -> exceptionDescription.type().equals(aClass))
                    .map(exceptionDescription -> new ResponseDesc(exceptionDescription.title(), exceptionDescription.description(), o)).collect(Collectors.toList());
            if (responseDescs.size() == 0)
                responseDescList.add(new ResponseDesc(o));
            else
                responseDescList.addAll(responseDescs);
        });
        apiAnalysis.setErrorResponses(responseDescList);
    }
}
