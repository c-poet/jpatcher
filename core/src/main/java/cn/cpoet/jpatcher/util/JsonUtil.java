package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.exception.JPatcherException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Json处理工具
 *
 * @author CPoet
 */
public class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new JsonMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        }
        return objectMapper;
    }

    public static <T> T read(byte[] bytes, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(bytes, clazz);
        } catch (Exception e) {
            throw new JPatcherException("Data deserialization failed", e);
        }
    }

    public static <T> T read(String content, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(content, clazz);
        } catch (Exception e) {
            throw new JPatcherException("Data deserialization failed", e);
        }
    }

    public static <T> T read(byte[] bytes, TypeReference<T> typeRef) {
        try {
            return getObjectMapper().readValue(bytes, typeRef);
        } catch (Exception e) {
            throw new JPatcherException("Data deserialization failed", e);
        }
    }

    public static <T> T read(String content, TypeReference<T> typeRef) {
        try {
            return getObjectMapper().readValue(content, typeRef);
        } catch (Exception e) {
            throw new JPatcherException("Data deserialization failed", e);
        }
    }

    public static String writeAsString(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new JPatcherException("Data serialization failed", e);
        }
    }

    public static byte[] writeAsBytes(Object obj) {
        try {
            return getObjectMapper().writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new JPatcherException("Data serialization failed", e);
        }
    }
}
