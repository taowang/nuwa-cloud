package com.study.oauth2.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class NuwaOAuth2ExceptionSerializer extends StdSerializer<NuwaOAuth2Exception> {

    protected NuwaOAuth2ExceptionSerializer() {
        super(NuwaOAuth2Exception.class);
    }

    @Override
    public void serialize(NuwaOAuth2Exception e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("code", e.getHttpErrorCode());
        jsonGenerator.writeStringField("msg", e.getOAuth2ErrorCode());
        jsonGenerator.writeEndObject();
    }
}
