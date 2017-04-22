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
    private String title;

    public ResponseDesc(Example r, Object example) {
        this.example = example;
        this.title = r.title();
        this.description = r.description();
    }

    public ResponseDesc(String title, String description, Object example) {
        this.title = title;
        this.example = example;
        if (!"".equals(description))
            this.description = description;
    }

    public ResponseDesc(Object sampleResponse) {
        this(null, null, sampleResponse);
    }
}
