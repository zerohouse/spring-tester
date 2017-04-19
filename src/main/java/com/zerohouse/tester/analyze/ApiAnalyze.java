package com.zerohouse.tester.analyze;

import com.zerohouse.tester.field.FieldDescription;
import com.zerohouse.tester.field.ParameterDescription;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ApiAnalyze {

    private String name;
    private String description;

    private List<FieldDescription> responseDescription;

    private String responseType;
    private ResponseDesc responseSample;
    private List<ResponseDesc> errorResponses;
    private List<ResponseDesc> responses;
    private String url;
    private boolean json;
    private List<ParameterDescription> paramDesc;
    private RequestMethod[] methods;
    private String paramNames;
    private Object parameterSample;
    private Map options;

    public ApiAnalyze() {
        options = new HashMap();
    }

    public void put(Object key, Object value) {
        options.put(key, value);
    }

    public Object get(Object key) {
        return options.get(key);
    }

    public void addResponses(ResponseDesc responseSample) {
        if(responses == null)
            responses = new ArrayList<>();
        responses.add(responseSample);
    }
}
