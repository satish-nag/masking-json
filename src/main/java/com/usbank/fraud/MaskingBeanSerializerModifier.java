package com.usbank.fraud;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;

public class MaskingBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            AnnotatedField field = writer.getMember().getAnnotated() instanceof java.lang.reflect.Field f
                    ? writer.getMember().getAnnotated().getAnnotation(Mask.class) != null ? (java.lang.reflect.Field) writer.getMember().getAnnotated() : null
                    : null;

            if (field != null && field.isAnnotationPresent(Mask.class)) {
                Mask mask = field.getAnnotation(Mask.class);
                JsonSerializer<Object> serializer = new MaskingSerializer(mask);
                writer.assignSerializer(serializer);
            }
        }
        return beanProperties;
    }
}