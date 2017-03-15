package com.zerohouse.tester.method;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class SampleParameterGeneratorTest {
    @Test
    public void analyze() throws Exception {
        Map defaultValues = new HashMap<>();
        defaultValues.put(String.class, "");
        defaultValues.put(Integer.class, 0);
        defaultValues.put(Double.class, 0);
        defaultValues.put(Long.class, 0);
        defaultValues.put(Float.class, 0);
        defaultValues.put(Boolean.class, false);
        defaultValues.put(Date.class, null);
        defaultValues.put(List.class, new ArrayList<>());
        defaultValues.put(Map.class, new HashMap<>());
        SampleParameterGenerator sampleParameterGenerator = new SampleParameterGenerator(new ArrayList<>(), new ArrayList<>(), defaultValues);
        System.out.println(sampleParameterGenerator);
        assertTrue(true);
    }

}