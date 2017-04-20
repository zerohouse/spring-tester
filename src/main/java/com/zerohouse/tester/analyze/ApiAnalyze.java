package com.zerohouse.tester.analyze;

import com.zerohouse.tester.field.FieldDescription;
import com.zerohouse.tester.field.ParameterDescription;
import lombok.AccessLevel;
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
    private List<ResponseDesc> errorResponses;
    private List<ResponseDesc> responses;
    private String url;
    private boolean json;
    private List<ParameterDescription> paramDesc;
    private RequestMethod[] methods;
    private String paramNames;
    private Object parameterSample;

    @Getter(AccessLevel.NONE)
    private Map options;

    private List<Explain> explains;


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
        if (responses == null)
            responses = new ArrayList<>();
        responses.add(responseSample);
    }

    public Map toMap() {
        options.put("name", name);
        options.put("description", description);
        options.put("responseDescription", responseDescription);
        options.put("responseType", responseType);
        options.put("errorResponses", errorResponses);
        options.put("responses", responses);
        options.put("url", url);
        options.put("json", json);
        options.put("paramDesc", paramDesc);
        options.put("methods", methods);
        options.put("paramNames", paramNames);
        options.put("parameterSample", parameterSample);
        options.put("explains", explains);
        return options;
    }

    public void addExplain(Explain explain) {
        if (explains == null)
            explains = new ArrayList<>();
        explains.add(explain);
    }
}
