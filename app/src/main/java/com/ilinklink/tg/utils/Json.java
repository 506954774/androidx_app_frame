package com.ilinklink.tg.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class Json {


    public static String toJson(Object object) {
        return new GsonBuilder().serializeNulls().create().toJson(object);
    }


    public static String getString(String json, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getString(key);
    }


    public static int getInt(String json, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getInt(key);
    }


    public static double getDouble(String json, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getDouble(key);
    }


    public static boolean getBoolean(String json, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getBoolean(key);
    }


    public static long getLong(String json, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getLong(key);
    }


    public static <T> T getObject(String result, String keyName, Class<T> t) throws JSONException {
        JSONObject obj = new JSONObject(result).getJSONObject(keyName);
        return (T) new Gson().fromJson(obj.toString(), t);
    }


    public static <T> T fromJson(String json, Type type) {
        return new Gson().fromJson(json, type);
    }


    public static <T> T fromJson(String json, Class<T> t) {
        return new Gson().fromJson(json, t);
    }

    public static <T> T fromJson(JsonElement json, Class<T> t) {
        return new Gson().fromJson(json, t);
    }


    public static <T> ArrayList<T> toList(String json, String name, Class<T> t) throws JSONException {
        JSONArray array = null;
        if (!TextUtils.isEmpty(name))
            array = new JSONObject(json).getJSONArray(name);
        else
            array = new JSONArray(json);
        ArrayList<T> entitys = new ArrayList<T>();
        Gson gson = new Gson();
        for (int i = 0; i < array.length(); i++) {
            T t1 = (T) gson.fromJson(array.getString(i), t);
            entitys.add(t1);
        }
        return entitys;
    }

    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)

    {

        Type type = new TypeToken<ArrayList<JsonObject>>()

        {}.getType();

        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();

        for (JsonObject jsonObject : jsonObjects)

        {

            arrayList.add(new Gson().fromJson(jsonObject, clazz));

        }

        return arrayList;

    }

    public static <T> ArrayList<T> toList(JsonElement jsonStr, Class<T> t) throws JSONException {
        ArrayList<T> entitys = new ArrayList<T>();
        if (jsonStr == null) {
            return entitys;
        } else {
            JsonArray array = jsonStr.getAsJsonArray();
            Gson gson = new Gson();
            for (int i = 0; i < array.size(); i++) {
                T t1 = (T) gson.fromJson(array.get(i), t);
                entitys.add(t1);
            }
            return entitys;
        }
    }
}
