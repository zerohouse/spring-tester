# spring-tester

    
    


Generate Spring MVC API Test Page

Mapping Path : /api/testPage

[DEMO](https://rawgit.com/zerohouse/spring-tester/master/test.html)
    
### Usage
    
    @Configuration
    public class Config {
    
        @Bean
        public SpringApiTester springApiTester() {
            SpringApiTester apiTester = new SpringApiTester("com.funny.production");
            apiTester.addParameterIgnoreAnnotation(Connected.class);
            apiTester.setTitle("API 테스트 페이지");
            apiTester.putHttpHeader("AccessId", "");
            apiTester.generate();
            return apiTester;
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
         <version>0.9.3</version>
     </dependency>
    
   
