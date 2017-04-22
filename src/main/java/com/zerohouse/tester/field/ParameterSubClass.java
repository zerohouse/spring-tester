package com.zerohouse.tester.field;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ParameterSubClass extends ParameterDescription {
    List subClass;

    public ParameterSubClass(String type, List subClass) {
        this.subClass = subClass;
        this.type = type;
    }
}
