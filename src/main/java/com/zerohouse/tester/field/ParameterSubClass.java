package com.zerohouse.tester.field;

import com.zerohouse.tester.annotation.Desc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ParameterSubClass extends ParameterDescription {
    List subClass;

    public ParameterSubClass(Class<?> clazz, String type, String name, Desc desc, boolean required, List subClass) {
        super(clazz, type, name, desc, required);
        this.subClass = subClass;
    }
}
