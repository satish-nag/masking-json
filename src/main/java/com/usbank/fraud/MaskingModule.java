package com.usbank.fraud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class MaskingModule extends SimpleModule {
    public MaskingModule() {
        setSerializerModifier(new MaskingBeanSerializerModifier());
    }

    public static ObjectMapper createMaskedObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new MaskingModule());
        return mapper;
    }
}