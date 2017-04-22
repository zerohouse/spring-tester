package com.zerohouse.tester.method.util;

import com.zerohouse.tester.annotation.Desc;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParameterMaker extends Maker {

    public ParameterMaker(Map<Class, Object> defaultValues) {
        super(defaultValues);
    }

    public Object parameterToSample(Parameter parameter) {
        String paramString = null;
        if (parameter.getAnnotation(Desc.class) != null && !"".equals(parameter.getAnnotation(Desc.class).example()))
            paramString = parameter.getAnnotation(Desc.class).example();
        if (paramString == null)
            return null;
        return makePrimitiveElseJsonWithoutPostProcess(parameter.getType(), paramString);
    }


    public Object makeSampleObject(Class<?> type) {
        Map<String, Object> objectMap = new HashMap<>();
        Arrays.stream(type.getDeclaredFields()).forEach(field -> {
            Desc desc = field.getAnnotation(Desc.class);
            if (desc != null) {
                objectMap.put(field.getName(), makePrimitiveElseJsonWithoutPostProcess(field.getType(), desc.example()));
            }
        });
        return objectMap;
    }


}
