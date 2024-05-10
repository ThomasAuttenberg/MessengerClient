package com.messenger.messengerclient.Models.Communication.RestAPI;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class JSONConverterFactory extends Converter.Factory{

    private static final JSONParser jsonParser = new JSONParser();

    public static JSONConverterFactory create(){
        return new JSONConverterFactory();
    }

    @Override
    public Converter<ResponseBody, JSONObject> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new JSONBodyConverter();
    }

    @Override
    public Converter<JSONObject, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new JSONRequestConverter();
    }
    public static class JSONBodyConverter implements Converter<ResponseBody, JSONObject>{
        @Override
        public JSONObject convert(ResponseBody responseBody) throws IOException {

            try {
                return (JSONObject) jsonParser.parse(responseBody.string());
            } catch (ParseException e) {
                System.out.println("UNNABLE TO CONVERT "+responseBody.string());
                return new JSONObject();
            }
        }
    }
    public static class JSONRequestConverter implements Converter<JSONObject, RequestBody>{
        @Override
        public RequestBody convert(JSONObject jsonObject) throws IOException {
            return RequestBody.create(MediaType.parse("text/plain"), jsonObject.toJSONString());
        }
    }
}
