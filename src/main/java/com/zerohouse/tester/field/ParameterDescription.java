package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.reflect.Parameter;

@Getter
@Setter
@NoArgsConstructor
public class ParameterDescription extends FieldDescription {

    boolean required;

    public ParameterDescription(Parameter clazz, String type, String name, Desc desc, boolean required) {
        this(clazz.getType(), type, name, desc, required);
        if (clazz.isAnnotationPresent(Size.class)) {
            Size size = clazz.getAnnotation(Size.class);
            constraints.add(new SizeConst(size.min(), size.max(), size.message()));
        }
        if (clazz.isAnnotationPresent(Pattern.class)) {
            Pattern pattern = clazz.getAnnotation(Pattern.class);
            constraints.add(new PatternConst(pattern.regexp(), pattern.message()));
        }
        if (clazz.isAnnotationPresent(NotNull.class)) {
            constraints.add(new Const(clazz.getAnnotation(NotNull.class).message()));
        }
    }

    public ParameterDescription(Class clazz, String type, String name, Desc desc, boolean required) {
        super(clazz, type, name, desc != null ? desc.value() : "");
        this.required = required || (desc != null && desc.required());
    }

}
