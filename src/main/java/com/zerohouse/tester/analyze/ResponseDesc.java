package com.zerohouse.tester.analyze;

import com.zerohouse.tester.annotation.Example;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDesc {

    Object example;
    private String description;

    public ResponseDesc(Example r, Object example) {
        this.example = example;
        this.description = r.description();
    }

    public ResponseDesc(String description, Object example) {
        this.example = example;
        if (!"".equals(description))
            this.description = description;
    }
}
