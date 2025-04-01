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
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        int showFirst = mask.showFirst();
        int showLast = mask.showLast();
        char maskChar = mask.maskChar();
        int len = value.length();

        if (showFirst + showLast >= len) {
            gen.writeString(value);
            return;
        }

        StringBuilder masked = new StringBuilder();
        masked.append(value, 0, showFirst);
        for (int i = 0; i < len - showFirst - showLast; i++) {
            masked.append(maskChar);
        }
        masked.append(value, len - showLast, len);
        gen.writeString(masked.toString());
    }
}