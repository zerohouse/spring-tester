package com.zerohouse.tester.method;


import com.zerohouse.tester.analyze.ApiAnalyze;
import com.zerohouse.tester.analyze.ResponseDesc;
import com.zerohouse.tester.annotation.*;
import com.zerohouse.tester.field.FieldDescription;
import com.zerohouse.tester.field.FieldSubClass;
import com.zerohouse.tester.method.util.ResponseMaker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiDescriptionAnalyzer implements MethodAnalyzer {

    private ResponseMaker responseMaker;

    public ApiDescriptionAnalyzer(ResponseMaker responseMaker) {
        this.responseMaker = responseMaker;
    }

    @Override
    public void analyze(Method method, ApiAnalyze apiAnalysis) {
        if (!method.isAnnotationPresent(Api.class))
            return;
        Api apiDescription = method.getAnnotation(Api.class);
        apiAnalysis.setName(apiDescription.value());
        apiAnalysis.setDescription(apiDescription.description());

        Class<?> returnType = method.getReturnType();

        List<FieldDescription> description = makeResponseDesc(returnType);
        Arrays.stream(apiDescription.subClasses()).forEach(subClass -> {
            if (void.class.equals(subClass.value()))
                return;
            description.remove(new FieldDescription(subClass.name()));
            description.add(new FieldSubClass(subClass.value(), "".equals(subClass.type()) ? subClass.value().getSimpleName() : subClass.type(), subClass.value().getSimpleName(), subClass.name(), subClass.description(), makeResponseDesc(subClass.value())));
        });

        boolean isList = isList(returnType);
        apiAnalysis.setResponseDescription(description);
        String responseType = isList ? "List" : returnType.getSimpleName();
        Subclass subclass = apiDescription.subClasses()[0];
        if (isList && !void.class.equals(subclass.value()))
            responseType += "<" + subclass.value().getSimpleName() + ">";
        apiAnalysis.setResponseType(responseType);

        if (apiDescription.responses().length != 0)
            apiAnalysis.setResponses(Arrays.stream(apiDescription.responses()).map(e -> {
                Object o = null;
                if (!void.class.equals(e.jsonType()))
                    o = responseMaker.asJson(e.value(), e.jsonType());
                else if (!void.class.equals(e.type()))
                    responseMaker.makePrimitiveOrJson(e.type(), e.value());
                else
                    o = responseMaker.makePrimitiveOrJson(returnType, e.value());
                return new ResponseDesc(e, o);
            }).collect(Collectors.toList()));
        else if (isList)
            apiAnalysis.addResponses(new ResponseDesc(responseMaker.getSampleListResponse(subclass.value())));
        else
            apiAnalysis.addResponses(new ResponseDesc(responseMaker.getSampleResponse(returnType)));
    }

    private boolean isList(Class<?> returnType) {
        return returnType.isArray() || List.class.isAssignableFrom(returnType) || Set.class.isAssignableFrom(returnType);
    }

    private List<FieldDescription> makeResponseDesc(Class<?> response) {
        List<FieldDescription> results = new ArrayList<>();
        if (response.getSuperclass() != null) {
            results.addAll(makeResponseDesc(response.getSuperclass()));
        }
        Subclasses subclasses = response.getAnnotation(Subclasses.class);
        if (subclasses != null)
            Arrays.stream(subclasses.value()).forEach(aClass -> {
                FieldSubClass fieldSubClass = new FieldSubClass(aClass.getSimpleName(), makeResponseDesc(aClass));
                results.add(fieldSubClass);
            });
        Arrays.stream(response.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(ExcludeResponse.class))
                return;
            Desc desc = field.getAnnotation(Desc.class);
            if (desc == null)
                return;
            String name = field.getName();
            Class<?> type = field.getType();
            results.add(new FieldDescription(desc, type, type.getSimpleName(), name, desc.value()));
        });
        return results;
    }

}
