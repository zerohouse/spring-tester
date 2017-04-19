package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;
import com.zerohouse.tester.method.util.ParameterIgnoreChecker;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterNamesGenerator implements MethodAnalyzer {

    ParameterIgnoreChecker parameterIgnoreChecker;

    public ParameterNamesGenerator(ParameterIgnoreChecker parameterIgnoreChecker) {
        this.parameterIgnoreChecker = parameterIgnoreChecker;
    }

    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        if (Arrays.stream(method.getParameters()).noneMatch(parameter -> parameter.isAnnotationPresent(RequestBody.class))) {
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            List<String> paramNames = new ArrayList<>();
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (parameterIgnoreChecker.isIgnore(parameter))
                    continue;
                paramNames.add(method.getParameters()[i].getType().getSimpleName() + " " + parameterNameDiscoverer.getParameterNames(method)[i]);
            }
            apiAnalysis.setParamNames(paramNames.stream().collect(Collectors.joining(", ")));
            return;
        }
        Parameter find = Arrays.stream(method.getParameters()).filter(parameter -> parameter.isAnnotationPresent(RequestBody.class)).findAny().get();
        apiAnalysis.setParamNames(find.getType().getSimpleName() + " (JSON)");
    }

}
