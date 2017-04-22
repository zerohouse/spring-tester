package com.zerohouse.tester.field;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FieldSubClass extends FieldDescription {
    List subClass;
    String subType;

    public FieldSubClass(Class<?> clazz, String type, String subType, String name, String description, List list) {
        super(null, clazz, type, name, description);
        subClass = list;
        this.subType = subType;
    }

    public FieldSubClass(String type, List list) {
        subClass = list;
        this.type = type;
    }
}
