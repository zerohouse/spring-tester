package com.zerohouse.tester.controller;

import com.zerohouse.tester.SpringApiTester;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@RequestMapping("/api/testPage")
public class SpringTesterController {

    private String testPage;

    public SpringTesterController(SpringApiTester apiTester) throws IOException {
        this.testPage = apiTester.getTestPageHtml();
    }

    @RequestMapping(produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String testPage() {
        return testPage;
    }
}
