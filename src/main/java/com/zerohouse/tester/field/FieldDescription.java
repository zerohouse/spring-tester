package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FieldDescription {

    String type;
    String name;
    String description;
    boolean isEnum;
    Object[] enumValues;
    List constraints;

    public FieldDescription(Class<?> clazz, String type, String name, Desc desc) {
        constraints = new ArrayList();
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
        isEnum = clazz.isEnum();
        if (isEnum)
            enumValues = clazz.getEnumConstants();
        this.type = type;
        this.name = name;
        if (desc != null)
            description = desc.value();

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class SizeConst extends Const {
        int min;
        int max;

        public SizeConst(int min, int max, String message) {
            super(message);
            type = "SIZE";
            this.min = min;
            this.max = max;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class PatternConst extends Const {
        String pattern;

        public PatternConst(String pattern, String message) {
            super(message);
            type = "PATTERN";
            this.pattern = pattern;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class Const {
        String message;
        String type;

        public Const(String message) {
            type = "NOT NULL";
            this.message = message;
        }
    }
}
