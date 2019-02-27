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
import com.google.gson.TypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            //统一将服务器后台返回的msg数组转换为字符串
            JSONObject jsonObject = new JSONObject(value.string());
            JSONObject json = jsonObject.optJSONObject("msg");
            JSONArray jsonArray = null;
            if (json == null) {
                jsonArray = jsonObject.optJSONArray("msg");
            } else {
                jsonObject.put("msg", json.optString("msg"));
            }
            if (jsonArray != null) {
                jsonObject.put("msg", jsonArray.length() > 0 ? jsonArray.get(0).toString() : "");
            }
            return adapter.fromJson(jsonObject.toString());
        } catch (JSONException e) {
            return adapter.read(gson.newJsonReader(value.charStream()));
        } finally {
            value.close();
        }
    }
}
