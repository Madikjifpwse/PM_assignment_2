package com.wolt.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, TypeToken<T> type) {
        return GSON.fromJson(json, type.getType());
    }

    private static class LocalDateTimeAdapter
            implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime dateTime, Type type,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.format(DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type type,
                                         JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);
        }
    }
}