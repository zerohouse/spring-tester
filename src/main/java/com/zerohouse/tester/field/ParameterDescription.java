package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

@Getter
@Setter
@NoArgsConstructor
public class ParameterDescription extends FieldDescription {

    boolean required;

    public ParameterDescription(Parameter clazz, String type, String name, Desc desc, boolean required) {
        this(clazz.getType(), type, name, desc, required);
        Email email = clazz.getAnnotation(Email.class);
        addConstraints(clazz, new ConstraintGetter<Parameter>() {
            @Override
            public <T extends Annotation> T getConst(Parameter object, Class<T> annotationClass) {
                return object.getAnnotation(annotationClass);
            }
        });
    }

    public ParameterDescription(Class clazz, String type, String name, Desc desc, boolean required) {
        super(clazz, type, name, desc != null ? desc.value() : "");
        this.required = required || (desc != null && desc.required());
    }

}
