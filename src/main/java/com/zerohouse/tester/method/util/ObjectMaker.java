package com.zerohouse.tester.method.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohouse.tester.annotation.Desc;
import com.zerohouse.tester.annotation.ParameterExample;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMaker {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<Class, Object> defaultValues;

    public ObjectMaker(Map<Class, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public Object getParamObject(Parameter parameter) {
        String paramString = null;
        if (parameter.getAnnotation(ParameterExample.class) != null && !"".equals(parameter.getAnnotation(ParameterExample.class).value()))
            paramString = parameter.getAnnotation(ParameterExample.class).value();
        else if (parameter.getAnnotation(Desc.class) != null && !"".equals(parameter.getAnnotation(Desc.class).example()))
            paramString = parameter.getAnnotation(Desc.class).example();
        if (paramString == null)
            return null;
        return parse(parameter.getType(), paramString);
    }

    public Object parse(Class<?> clazz, String paramString) {
        if ("".equals(paramString)) {
            if (defaultValues.get(clazz) != null)
                return defaultValues.get(clazz);
            if (clazz.isEnum() && clazz.getEnumConstants().length > 0)
                return clazz.getEnumConstants()[0];
            return null;
        }
        if (clazz.equals(String.class)) {
            return paramString;
        }
        if (clazz.equals(Long.class)) {
            return Long.parseLong(paramString);
        }
        if (clazz.equals(Integer.class)) {
            return Integer.parseInt(paramString);
        }
        if (clazz.equals(Double.class)) {
            return Double.parseDouble(paramString);
        }
        if (clazz.equals(Float.class)) {
            return Float.parseFloat(paramString);
        }
        if (clazz.equals(Boolean.class)) {
            return Boolean.parseBoolean(paramString);
        }
        return getJsonObject(paramString);
    }

    public Object getJsonObject(String paramString) {
        try {
            if ("".equals(paramString))
                return null;
            if (paramString.charAt(0) == '[')
                return objectMapper.readValue(paramString.replaceAll("'", "\""), List.class);
            if (paramString.charAt(0) == '{')
                return objectMapper.readValue(paramString.replaceAll("'", "\""), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramString;
    }

    public Object makeSampleObject(Class<?> type) {
        Map<String, Object> objectMap = new HashMap<>();
        Arrays.stream(type.getDeclaredFields()).forEach(field -> {
            ParameterExample parameterExample = field.getAnnotation(ParameterExample.class);
            if (parameterExample != null) {
                objectMap.put(field.getName(), parse(field.getType(), parameterExample.value()));
                return;
            }
            Desc desc = field.getAnnotation(Desc.class);
            if (desc != null) {
                objectMap.put(field.getName(), parse(field.getType(), desc.example()));
            }
        });
        return objectMap;
    }
}
