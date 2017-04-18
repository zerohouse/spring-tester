package com.zerohouse.tester.method.util;

import com.zerohouse.tester.annotation.ExcludeParameter;

import java.lang.reflect.Parameter;
import java.util.List;

public class ParameterIgnoreChecker {

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;

    public ParameterIgnoreChecker(List<Class> ignoreAnnotations, List<Class> ignoreClasses) {
        this.ignoreAnnotations = ignoreAnnotations;
        this.ignoreClasses = ignoreClasses;
    }

    public boolean isIgnore(Parameter parameter) {
        if (ignoreAnnotations.stream().anyMatch(parameter::isAnnotationPresent))
            return true;
        if (ignoreClasses.stream().anyMatch(aClass -> aClass.equals(parameter.getType())))
            return true;
        return parameter.isAnnotationPresent(ExcludeParameter.class);
    }
}
