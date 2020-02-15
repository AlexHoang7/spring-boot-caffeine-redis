package com.example.caffeine.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json工具类
 *
 * @author Alex
 */
public class JsonUtil {

    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        //取消默认timestamps格式
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		/*DeserializationConfig cfg = MAPPER.getDeserializationConfig();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");*/

    }

    /**
     * bean对象转json字符串
     *
     * @param obj 缓存的值
     * @return json字符串
     * @throws JsonProcessingException json执行异常
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * json字符串转bean对象
     *
     * @param json  json字符串
     * @param clazz 反序列化class
     * @return 对应bean对象
     * @throws JsonParseException   json解析异常
     * @throws JsonMappingException json映射异常
     * @throws IOException          io异常
     */
    public static <T> T toBean(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return MAPPER.readValue(json, clazz);
    }

    /**
     * json字符串转换成list
     *
     * @param json json字符串
     * @return 对应转换集合
     * @throws JsonParseException   json解析异常
     * @throws JsonMappingException json映射异常
     * @throws IOException          io异常
     */
    public static <T> List<T> toList(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        //return MAPPER.readValue(json, new TypeReference<List<T>>() {});
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz));
    }

    /**
     * json字符串转换成map
     *
     * @param json json字符串
     * @return 对应转换map
     * @throws JsonParseException   json解析异常
     * @throws JsonMappingException json映射异常
     * @throws IOException          io异常
     */
    public static <T> Map<String, T> toMap(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(HashMap.class, String.class, clazz));
    }
}
