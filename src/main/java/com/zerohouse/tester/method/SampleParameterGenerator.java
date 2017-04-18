package com.zerohouse.tester.method;

import com.zerohouse.tester.method.util.ObjectMaker;
import com.zerohouse.tester.method.util.ParameterIgnoreChecker;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SampleParameterGenerator implements MethodAnalyzer {

    ObjectMaker objectMaker;
    private ParameterIgnoreChecker parameterIgnoreChecker;

    public SampleParameterGenerator(ParameterIgnoreChecker parameterIgnoreChecker, ObjectMaker objectMaker) {
        this.parameterIgnoreChecker = parameterIgnoreChecker;
        this.objectMaker = objectMaker;
    }

    @Override
    public void analyze(Method method, Map apiAnalysis) {
        Optional<Parameter> optional = Arrays.stream(method.getParameters()).filter(parameter -> parameter.isAnnotationPresent(RequestBody.class)).findAny();
        boolean json = optional.isPresent();
        apiAnalysis.put("json", json);
        if (!json) {
            HashMap<String, Object> params = new HashMap<>();
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (parameterIgnoreChecker.isIgnore(parameter))
                    continue;
                Object paramObject = objectMaker.getParamObject(parameter);
                if (paramObject != null) {
                    params.put(parameterNameDiscoverer.getParameterNames(method)[i], paramObject);
                    continue;
                }
                params.put(parameterNameDiscoverer.getParameterNames(method)[i], "");
            }
            apiAnalysis.put("parameter", params);
            return;
        }
        Parameter find = optional.get();
        Object paramObject = objectMaker.getParamObject(find);
        if (paramObject != null) {
            apiAnalysis.put("parameter", paramObject);
            return;
        }
        if (List.class.isAssignableFrom(find.getType()) || find.getType().isArray())
            apiAnalysis.put("parameter", new ArrayList<>());
        else if (Map.class.isAssignableFrom(find.getType()))
            apiAnalysis.put("parameter", new HashMap<>());
        else
            apiAnalysis.put("parameter", objectMaker.makeSampleObject(find.getType()));
    }

}
