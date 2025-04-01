package com.usbank.fraud;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MaskingSerializer extends JsonSerializer<String> {
    private final Mask mask;

    public MaskingSerializer(Mask mask) {
        this.mask = mask;
    }

    @Override
    private void serializeValue(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value instanceof String str) {
            gen.writeString(applyMask(str));
        } else if (value instanceof Number num) {
            gen.writeString(applyMask(num.toString())); // ← Mask numbers as strings
        } else if (value instanceof Collection<?> collection) {
            gen.writeStartArray();
            for (Object item : collection) {
                serializeValue(item, gen, serializers);
            }
            gen.writeEndArray();
        } else if (value.getClass().isArray()) {
            gen.writeStartArray();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                serializeValue(Array.get(value, i), gen, serializers);
            }
            gen.writeEndArray();
        } else if (value instanceof Map<?, ?> map) {
            gen.writeStartObject();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                gen.writeFieldName(String.valueOf(entry.getKey()));
                serializeValue(entry.getValue(), gen, serializers);
            }
            gen.writeEndObject();
        } else {
            gen.writeObject(value); // fallback for unknown types
        }
    }
}