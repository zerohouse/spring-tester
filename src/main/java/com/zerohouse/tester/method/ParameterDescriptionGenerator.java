package com.zerohouse.tester.method;

import com.zerohouse.tester.analyze.ApiAnalyze;
import com.zerohouse.tester.annotation.Desc;
import com.zerohouse.tester.annotation.ExcludeParameter;
import com.zerohouse.tester.annotation.Subclasses;
import com.zerohouse.tester.field.ParameterDescription;
import com.zerohouse.tester.field.ParameterSubClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterDescriptionGenerator implements MethodAnalyzer {

    List<Class> ignoreAnnotations;
    List<Class> ignoreClasses;

    public ParameterDescriptionGenerator(List<Class> ignoreAnnotations, List<Class> ignoreClasses) {
        this.ignoreAnnotations = ignoreAnnotations;
        this.ignoreClasses = ignoreClasses;
    }

    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        if (Arrays.stream(method.getParameters()).noneMatch(parameter -> parameter.isAnnotationPresent(RequestBody.class))) {
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            List<ParameterDescription> parameterDescriptions = new ArrayList<>();
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (ignoreAnnotations.stream().anyMatch(parameter::isAnnotationPresent))
                    continue;
                if (ignoreClasses.stream().anyMatch(aClass -> aClass.equals(parameter.getType())))
                    continue;
                ParameterDescription parameterDescription = new ParameterDescription(
                        parameter.getAnnotation(Desc.class),
                        method.getParameters()[i],
                        method.getParameters()[i].getType().getSimpleName(),
                        parameterNameDiscoverer.getParameterNames(method)[i]
                );
                parameterDescription.setRequired(parameter.isAnnotationPresent(NotNull.class) || (parameter.getAnnotation(Desc.class) != null && parameter.getAnnotation(Desc.class).required()));
                parameterDescriptions.add(parameterDescription);
            }
            apiAnalysis.setParamDesc(parameterDescriptions);
            return;
        }
        Parameter find = Arrays.stream(method.getParameters()).filter(parameter -> parameter.isAnnotationPresent(RequestBody.class)).findAny().get();
        Class type = find.getType();
        List<ParameterDescription> parameterDescriptions = getDescriptions(type);
        apiAnalysis.setParamDesc(parameterDescriptions);
    }

    private List<ParameterDescription> getDescriptions(Class<?> clazz) {
        List<ParameterDescription> list = new ArrayList<>();
        if (clazz.getSuperclass() != null) {
            list.addAll(getDescriptions(clazz.getSuperclass()));
        }
        Subclasses subclasses = clazz.getAnnotation(Subclasses.class);
        if (subclasses != null)
            Arrays.stream(subclasses.value()).forEach(aClass -> {
                ParameterSubClass parameterSubClass = new ParameterSubClass(aClass.getSimpleName(), getDescriptions(aClass));
                list.add(parameterSubClass);
            });
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(ExcludeParameter.class))
                return;
            Desc desc = field.getAnnotation(Desc.class);
            if (desc == null)
                return;
            String name = field.getName();
            String type = field.getType().getSimpleName();
//            if (desc.subClass()) {
//                ParameterSubClass parameterSubClass = new ParameterSubClass(desc, field.getType(), type, name, getDescriptions(field.getType()));
//                parameterSubClass.checkNotNull(field.isAnnotationPresent(NotNull.class));
//                list.add(parameterSubClass);
//                return;
//            }

            ParameterDescription parameterDescription = new ParameterDescription(desc, field.getType(), type, name);
            parameterDescription.checkNotNull(field.isAnnotationPresent(NotNull.class));
            list.add(parameterDescription);
        });
        return list;
    }
}
