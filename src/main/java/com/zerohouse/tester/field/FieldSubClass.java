package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FieldSubClass extends FieldDescription {
    List subClass;

    public FieldSubClass(Class<?> clazz, String type, String name, Desc desc, List list) {
        super(clazz, type, name, desc);
        subClass = list;
    }
}
