package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

@Getter
@Setter
@NoArgsConstructor
public class ParameterDescription extends FieldDescription {

    boolean required;

    public ParameterDescription(Desc desc, Parameter clazz, String type, String name) {
        this(desc, clazz.getType(), type, name);
        addConstraints(clazz, new ConstraintGetter<Parameter>() {
            @Override
            public <T extends Annotation> T getConst(Parameter object, Class<T> annotationClass) {
                return object.getAnnotation(annotationClass);
            }
        });
    }

    public ParameterDescription(Desc desc, Class clazz, String type, String name) {
        super(desc, clazz, type, name, desc != null ? desc.value() : "");
    }

    public void checkNotNull(boolean notnull) {
        this.required = notnull || (desc != null && desc.required());
    }
}
