package com.zerohouse.tester.method;


import com.zerohouse.tester.annotation.Api;
import com.zerohouse.tester.annotation.Desc;
import com.zerohouse.tester.annotation.ExcludeResponse;
import com.zerohouse.tester.annotation.ResponseExample;
import com.zerohouse.tester.field.FieldDescription;
import com.zerohouse.tester.field.FieldSubClass;
import com.zerohouse.tester.method.util.ObjectMaker;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ApiDescriptionAnalyzer implements MethodAnalyzer {

    private ObjectMaker objectMaker;

    public ApiDescriptionAnalyzer(ObjectMaker objectMaker) {
        this.objectMaker = objectMaker;
    }

    @Override
    public void analyze(Method method, Map apiAnalysis) {
        if (!method.isAnnotationPresent(Api.class))
            return;
        Api apiDescription = method.getAnnotation(Api.class);
        apiAnalysis.put("value", apiDescription.value());
        apiAnalysis.put("description", apiDescription.description());
        apiAnalysis.put("errorResponses", Arrays.stream(apiDescription.errorResponses()).map(objectMaker::getJsonObject).collect(Collectors.toList()));
        if (!apiDescription.responseClass().equals(void.class)) {
            apiAnalysis.put("responseDesc", makeResponseDesc(apiDescription.responseClass()));
            if (apiDescription.responseAsList())
                apiAnalysis.put("responseSample", new Map[]{makeSampleResponse(apiDescription.responseClass())});
            else
                apiAnalysis.put("responseSample", makeSampleResponse(apiDescription.responseClass()));
        }
        if (!"".equals(apiDescription.responseJson()))
            apiAnalysis.put("responseSample", objectMaker.parse(apiDescription.responseClass(), apiDescription.responseJson()));
    }

    private List makeResponseDesc(Class<?> response) {
        List results = new ArrayList();
        if (response.getSuperclass() != null) {
            results.addAll(makeResponseDesc(response.getSuperclass()));
        }
        Arrays.stream(response.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(ExcludeResponse.class))
                return;
            Desc desc = field.getAnnotation(Desc.class);
            if (desc == null)
                return;
            String name = field.getName();
            String type = field.getType().getSimpleName();
            if (desc.subClass()) {
                results.add(new FieldSubClass(field.getType(), type, name, desc, makeResponseDesc(field.getType())));
                return;
            }
            results.add(new FieldDescription(field.getType(), type, name, desc));
        });
        return results;
    }

    private Map makeSampleResponse(Class<?> response) {
        Map map = new HashMap();
        if (response.getSuperclass() != null) {
            map.putAll(makeSampleResponse(response.getSuperclass()));
        }
        Arrays.stream(response.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(ExcludeResponse.class))
                return;
            ResponseExample responseExample = field.getAnnotation(ResponseExample.class);
            String name = field.getName();
            if (responseExample != null) {
                map.put(name, objectMaker.parse(field.getType(), responseExample.value()));
                return;
            }
            Desc desc = field.getAnnotation(Desc.class);
            if (desc == null)
                return;
            map.put(name, objectMaker.parse(field.getType(), desc.example()));
            if (desc.subClass()) {
                map.put(name, makeSampleResponse(field.getType()));
            }
        });
        return map;
    }
}
