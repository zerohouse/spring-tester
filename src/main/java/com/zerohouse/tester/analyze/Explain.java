package com.zerohouse.tester.analyze;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Explain {
    private String body;
    private String title;

    public Explain(String title, String body) {
        this.body = body;
        this.title = title;
    }
}
