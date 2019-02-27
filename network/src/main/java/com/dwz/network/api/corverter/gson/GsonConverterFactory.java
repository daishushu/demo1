/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dwz.network.api.corverter.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} which uses Gson for JSON.
 * <p/>
 * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
 * all types. If you are mixing JSON serialization with something else (such as protocol buffers),
 * you must {@linkplain Retrofit.Builder#addConverterFactory(Converter.Factory) add this instance}
 * last to allow the other converters a chance to see their types.
 */
public final class GsonConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactory create() {
        return create(buildSimpleGson());
    }

    public static Gson buildSimpleGson(){
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(String.class, new StringDefaultAdapter())
                .registerTypeHierarchyAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeHierarchyAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeHierarchyAdapter(Float.class, new FloatDefault0Adapter())
                .registerTypeHierarchyAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeHierarchyAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeHierarchyAdapter(long.class, new LongDefault0Adapter())
                .registerTypeHierarchyAdapter(float.class, new FloatDefault0Adapter())
                .registerTypeHierarchyAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeHierarchyAdapter(List.class,new ListJsonDefaultAdapter<>())
                .create();

    }
    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactory create(Gson gson) {
        return new GsonConverterFactory(gson);
    }

    private final Gson gson;

    private GsonConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    public static class IntegerDefault0Adapter implements JsonDeserializer<Integer>, JsonSerializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                //定义为int类型,如果后台返回""或者null,则返回0
                if ("".equals(json.getAsString()) || "null".equals(json.getAsString())
                        ||json.isJsonArray() || json.isJsonNull()|| json.isJsonObject()) {
                    return 0;
                }
            } catch (Exception ignore) {
                return 0;
            }
            try {
                return json.getAsInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Integer integer, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(integer);
        }
    }

    public static class StringDefaultAdapter implements JsonDeserializer<String>, JsonSerializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                //定义为int类型,如果后台返回""或者null,则返回0
                if (json.isJsonArray() || json.isJsonNull()|| json.isJsonObject()) {
                    return "";
                }
            } catch (Exception ignore) {
                return "";
            }
            try {
                return json.getAsString();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(String integer, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(integer);
        }
    }

    public static class DoubleDefault0Adapter implements JsonDeserializer<Double>, JsonSerializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if ("".equals(json.getAsString()) || "null".equals(json.getAsString())
                        ||json.isJsonArray() || json.isJsonNull()|| json.isJsonObject()) {//定义为int类型,如果后台返回""或者null,则返回0
                    return 0d;
                }
            } catch (Exception ignore) {
                return 0d;
            }
            try {
                return json.getAsDouble();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Double integer, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(integer);
        }
    }

    public static class FloatDefault0Adapter implements JsonDeserializer<Float>, JsonSerializer<Float> {
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if ("".equals(json.getAsString()) || "null".equals(json.getAsString())
                        ||json.isJsonArray() || json.isJsonNull()|| json.isJsonObject()) {//定义为int类型,如果后台返回""或者null,则返回0
                    return 0f;
                }
            } catch (Exception ignore) {
                return 0f;
            }
            try {
                return json.getAsFloat();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Float integer, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(integer);
        }
    }

    public static class LongDefault0Adapter implements JsonDeserializer<Long>, JsonSerializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                //定义为int类型,如果后台返回""或者null,则返回0
                if ("".equals(json.getAsString()) || "null".equals(json.getAsString())
                        ||json.isJsonArray() || json.isJsonNull()|| json.isJsonObject()) {
                    return 0L;
                }
            } catch (Exception ignore) {
                return 0L;
            }
            try {
                return json.getAsLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Long integer, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(integer);
        }
    }

    public static class ArrayJsonDefaultAdapter implements JsonDeserializer<JsonArray> {
        @Override
        public JsonArray deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            //list,如果后台没返回大括号，则创建一个空的集合
            if (!json.isJsonArray()) {
                return new JsonArray();
            }
            try {
                return json.getAsJsonArray();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }
    public static class ListJsonDefaultAdapter<T> implements JsonDeserializer<List<T>> {
        @Override
        public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            //list,如果后台没返回大括号，则创建一个空的集合
            if (!json.isJsonArray()) {
                return Collections.emptyList();
            }
            return new Gson().fromJson(json,typeOfT);
        }
    }
}
