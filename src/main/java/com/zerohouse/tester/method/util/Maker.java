package com.zerohouse.tester.method.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public abstract class Maker {

    protected ObjectMapper objectMapper = new ObjectMapper();
    private Map<Class, Object> defaultValues;

    protected Maker(Map<Class, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public Object makePrimitiveElseJsonWithoutPostProcess(Class<?> clazz, String paramString) {
        if ("".equals(paramString)) {
            if (defaultValues.get(clazz) != null)
                return defaultValues.get(clazz);
            if (List.class.isAssignableFrom(clazz))
                return new ArrayList<>();
            if (Set.class.isAssignableFrom(clazz))
                return new ArrayList<>();
            if (Map.class.isAssignableFrom(clazz))
                return new HashMap<>();
            if (clazz.isEnum() && clazz.getEnumConstants().length > 0)
                return clazz.getEnumConstants()[0];
            return null;
        }
        if (clazz.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) clazz, paramString);
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
        if (clazz.equals(Boolean.class)) {
            return Boolean.parseBoolean(paramString);
        }
        return getJsonObject(paramString);
    }


    protected Object getJsonObject(String paramString) {
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

}
