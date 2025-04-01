package com.usbank.fraud;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class RecursiveMaskingSerializer extends JsonSerializer<Object> {

    private final Mask mask;

    public RecursiveMaskingSerializer(Mask mask) {
        this.mask = mask;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        serializeValue(value, gen, serializers);
    }

    private void serializeValue(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value instanceof String str) {
            gen.writeString(applyMask(str));
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
                Object item = Array.get(value, i);
                serializeValue(item, gen, serializers);
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
            // Fallback to normal Jackson serialization
            gen.writeObject(value);
        }
    }

    private String applyMask(String input) {
        int showFirst = mask.showFirst();
        int showLast = mask.showLast();
        char maskChar = mask.maskChar();

        int len = input.length();
        if (showFirst + showLast >= len) return input;

        StringBuilder masked = new StringBuilder();
        masked.append(input, 0, showFirst);
        masked.append(String.valueOf(maskChar).repeat(len - showFirst - showLast));
        masked.append(input.substring(len - showLast));
        return masked.toString();
    }
}