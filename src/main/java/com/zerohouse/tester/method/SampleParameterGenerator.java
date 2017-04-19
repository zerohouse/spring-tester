package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;
import com.zerohouse.tester.method.util.ParameterIgnoreChecker;
import com.zerohouse.tester.method.util.ParameterMaker;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SampleParameterGenerator implements MethodAnalyzer {

    ParameterMaker parameterMaker;
    private ParameterIgnoreChecker parameterIgnoreChecker;

    public SampleParameterGenerator(ParameterIgnoreChecker parameterIgnoreChecker, ParameterMaker parameterMaker) {
        this.parameterIgnoreChecker = parameterIgnoreChecker;
        this.parameterMaker = parameterMaker;
    }

    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        Optional<Parameter> optional = Arrays.stream(method.getParameters()).filter(parameter -> parameter.isAnnotationPresent(RequestBody.class)).findAny();
        apiAnalysis.setJson(optional.isPresent());
        Object parameterSample = getParameterSample(optional, method);
        apiAnalysis.setParameterSample(parameterSample);
    }

    private Object getParameterSample(Optional<Parameter> optional, Method method) {
        if (!optional.isPresent()) {
            HashMap<String, Object> params = new HashMap<>();
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (parameterIgnoreChecker.isIgnore(parameter))
                    continue;
                Object paramObject = parameterMaker.parameterToSample(parameter);
                if (paramObject != null) {
                    params.put(parameterNameDiscoverer.getParameterNames(method)[i], paramObject);
                    continue;
                }
                params.put(parameterNameDiscoverer.getParameterNames(method)[i], "");
            }
            return params;
        }
        Parameter find = optional.get();
        Object paramObject = parameterMaker.parameterToSample(find);
        if (paramObject != null) {
            return paramObject;
        }
        if (List.class.isAssignableFrom(find.getType()) || find.getType().isArray())
            return new ArrayList<>();
        else if (Map.class.isAssignableFrom(find.getType()))
            return new HashMap<>();
        else
            return parameterMaker.makeSampleObject(find.getType());
    }


}
