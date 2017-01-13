package com.zerohouse.tester;

import org.junit.Test;

public class SpringApiAnalyzerTest {
    @Test
    public void generateTestPage() throws Exception {
        SpringApiTester springApiTester = new SpringApiTester("com.zerohouse");
        springApiTester.setTitle("편돌이");
        springApiTester.getDefaultHeaders().put("asdf", "asdf");
        springApiTester.generateTestPageHtml("test.html");
    }

}