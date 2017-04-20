package com.zerohouse;

import com.zerohouse.tester.SpringApiTester;
import org.junit.Test;

public class SpringApiAnalyzerTest {
    @Test
    public void generateTestPage() throws Exception {
        SpringApiTester springApiTester = new SpringApiTester("com.zerohouse", null);
        springApiTester.setTitle("편돌이");
        springApiTester.putHttpHeader("asdf", "asdf");
        springApiTester.generateTestPageHtml("test.html");
    }

}