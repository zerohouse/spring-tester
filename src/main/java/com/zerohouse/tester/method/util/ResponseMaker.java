package com.zerohouse.tester.method.util;

import com.zerohouse.tester.annotation.Desc;
import com.zerohouse.tester.annotation.ExcludeResponse;
import com.zerohouse.tester.spec.ResponseSampleProcessor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseMaker extends Maker {

    private List<ResponseSampleProcessor> responseSampleProcessors;
    private Object defaultObj;

    public ResponseMaker(Map<Class, Object> defaultValues, List<ResponseSampleProcessor> responseSampleProcessors, Object defaultObj) {
        super(defaultValues);
        this.responseSampleProcessors = responseSampleProcessors;
        this.defaultObj = defaultObj;
    }

    private Object postProcess(Object example) {
        if (example == null)
            example = defaultObj;
        for (ResponseSampleProcessor responseSampleProcessor : responseSampleProcessors) {
            example = responseSampleProcessor.postProcess(example);
        }
        return example;
    }


    public Object makePrimitiveOrJson(Class<?> clazz, String paramString) {
        return postProcess(makePrimitiveElseJson(clazz, paramString));
    }

    private Map makeSampleResponse(Class<?> response) {
        Map map = new HashMap();
        if (response.getSuperclass() != null) {
            map.putAll(makeSampleResponse(response.getSuperclass()));
        }
        Arrays.stream(response.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(ExcludeResponse.class))
                return;
            String name = field.getName();
            Desc desc = field.getAnnotation(Desc.class);
            if (desc == null)
                return;
            map.put(name, this.makePrimitiveElseJson(field.getType(), desc.example()));
            if (desc.subClass()) {
                map.put(name, makeSampleResponse(field.getType()));
            }
        });
        return map;
    }


    public Object getSampleResponse(Class<?> returnType) {
        Map map = makeSampleResponse(returnType);
        if (map.size() == 0)
            return postProcess(null);
        return postProcess(map);
    }

    public Object getSampleJson(String value) {
        return postProcess(getJsonObject(value));
    }

    public Object asJson(String value, Class<?> aClass) {
        try {
            return postProcess(objectMapper.readValue(value, aClass));
        } catch (IOException e) {
            e.printStackTrace();
            return postProcess(null);
        }
    }
}
