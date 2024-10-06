package com.study.nuwa.cloud.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Oauth2异常类序列化类
 */
public class NuwaOAuthExceptionJackson2Serializer extends StdSerializer<NuwaOAuth2Exception> {

    protected NuwaOAuthExceptionJackson2Serializer() {
        super(NuwaOAuth2Exception.class);
    }

    @Override
    public void serialize(NuwaOAuth2Exception value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        Map<String, String> content = new HashMap<>();

        //序列化error
        content.put(OAuth2Exception.ERROR, value.getOAuth2ErrorCode());
        //jgen.writeStringField(OAuth2Exception.ERROR,value.getOAuth2ErrorCode());

        //序列化error_description
        content.put(OAuth2Exception.DESCRIPTION, value.getMessage());
        //jgen.writeStringField(OAuth2Exception.DESCRIPTION,value.getMessage());

        //序列化额外的附加信息AdditionalInformation
        if (value.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                content.put(key, add);
                //jgen.writeStringField(key, add);
            }
        }
        jgen.writeFieldName("result");
        jgen.writeObject(content);
        jgen.writeFieldName("code");
        jgen.writeNumber(500);
        jgen.writeEndObject();
    }
}
