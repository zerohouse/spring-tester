# spring-tester

    
    
### generate example
    @Configuration
    @EnableWebMvc
    @EnableSpringDataWebSupport
    public class WebConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            SpringApiTester apiTester = new SpringApiTester("com.pd");
            apiTester.addParameterIgnoreAnnotation(Logged.class);
            apiTester.generateTestPageHtml("/test/testPage.html");
            registry.addResourceHandler("/api/test/**").addResourceLocations("file:/test/");
        }
    }
    
    
### pom.xml
#### repository
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
    
#### dependency    
     <dependency>
         <groupId>com.github.zerohouse</groupId>
         <artifactId>spring-tester</artifactId>
         <version>0.0.1</version>
     </dependency>
    
   