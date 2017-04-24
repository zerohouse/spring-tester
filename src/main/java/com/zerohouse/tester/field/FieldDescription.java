package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.*;
import org.hibernate.validator.constraints.*;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class FieldDescription {

    String type;
    String name;
    String description;
    boolean isEnum;
    Map<String, Map> enumValues;
    List<Const> constraints;

    @Getter(AccessLevel.NONE)
    protected Desc desc;

    public FieldDescription(Desc desc, Class<?> clazz, String typeName, String fieldName, String description) {
        constraints = new ArrayList<>();
        addConstraints(clazz, new ConstraintGetter<Class<?>>() {
            @Override
            public <T extends Annotation> T getConst(Class<?> object, Class<T> annotationClass) {
                return object.getAnnotation(annotationClass);
            }
        });
        isEnum = clazz.isEnum();
        if (isEnum)
            enumValues = getEnums((Class<? extends Enum>) clazz);
        this.type = (desc == null || "".equals(desc.type())) ? typeName : desc.type();
        this.name = (desc == null || "".equals(desc.name())) ? fieldName : desc.name();
        if (description != null)
            this.description = description;

    }

    private Map<String, Map> getEnums(Class<? extends Enum> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toMap(Enum::toString, o -> Arrays.stream(enumClass.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .collect(Collectors.toMap(Field::getName, field -> {
                            try {
                                field.setAccessible(true);
                                return field.get(o);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }))));
    }

    <T> void addConstraints(T clazz, ConstraintGetter<T> getter) {
        Size size = getter.getConst(clazz, Size.class);
        if (size != null)
            constraints.add(new SizeConst(size.min(), size.max(), size.message()));
        Pattern pattern = getter.getConst(clazz, Pattern.class);
        if (pattern != null)
            constraints.add(new PatternConst(pattern.regexp(), pattern.message()));
        Arrays.stream(new Class[]{
                Email.class, CreditCardNumber.class, Length.class, Range.class, SafeHtml.class, URL.class,
                AssertFalse.class, AssertTrue.class, DecimalMax.class, DecimalMin.class, Digits.class, Future.class, Max.class, Min.class, NotNull.class, Null.class, Past.class}).forEach(aClass -> {
            Annotation annotation = getter.getConst(clazz, aClass);
            if (annotation != null)
                try {
                    constraints.add(new Const(annotation.annotationType().getSimpleName(), String.valueOf(annotation.getClass().getDeclaredMethod("message").invoke(annotation))));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
        });
    }

    public FieldDescription(String name) {
        this.name = name;
    }

    public interface ConstraintGetter<E> {
        <T extends Annotation> T getConst(E object, Class<T> annotationClass);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class SizeConst extends Const {
        SizeConst(int min, int max, String message) {
            super("Size", message);
            value = String.format("min:%d, max:%d", min, max);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class PatternConst extends Const {
        PatternConst(String pattern, String message) {
            super("Pattern", message);
            this.value = pattern;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class Const {
        String message;
        String type;
        String value;

        Const(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }
}
