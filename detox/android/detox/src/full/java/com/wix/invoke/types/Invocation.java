package com.wix.invoke.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rotemm on 10/10/2016.
 */
public class Invocation {
    private Target target;
    private String method;
    private Object[] args;

    public Invocation() {

    }

    public Invocation(Target target, String method, Object... args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Invocation(JSONObject json) throws JSONException {
        this.target = Target.getTarget(json.getJSONObject("target"));
        this.method = json.getString("method");
        this.args = unwrapJSON(json.getJSONArray("args"));
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Object[] getArgs() {
        return args;
    }

    private static Object[] unwrapJSON(JSONArray args) throws JSONException {
        ArrayList<Object> objects = new ArrayList<>();
        for (int i = 0; i < args.length(); i++) {
            Object arg = args.opt(i);
            Object argument = unwrapJsonObject(arg);
            objects.add(argument);
        }
        return objects.toArray();
    }

    @Nullable
    private static Object unwrapJsonObject(Object arg) throws JSONException {
        if (arg instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) arg;
            ArrayList<Object> objects = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Object arg1 = jsonArray.opt(i);
                Object argument = unwrapJsonObject(arg1);
                objects.add(argument);
            }
            return objects;
        }
        if (arg instanceof JSONObject) {
            return unwrapJsonObject(toMap((JSONObject) arg));
        }

        if (!(arg instanceof Map)) {
            return arg;
        }

        Map<?, ?> jsonArgument = (Map<?, ?>) arg;
        Object type = jsonArgument.get("type");
        Object value = jsonArgument.get("value");

        if (type == null || value == null) {
            LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
            for (Object key : jsonArgument.keySet()) {
                map.put(key, unwrapJsonObject(jsonArgument.get(key)));
            }
            return map;
        }

        if ("Integer".equalsIgnoreCase(type.toString())) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                try {
                    return Integer.parseInt(value.toString());
                } catch (NumberFormatException ignored) {
                    return 0;
                }
            }
        } else if ("Float".equalsIgnoreCase(type.toString())) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else {
                try {
                    return Float.parseFloat(value.toString());
                } catch (NumberFormatException ignored) {
                    return Float.NaN;
                }
            }
        } else if ("Double".equalsIgnoreCase(type.toString())) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                try {
                    return Double.parseDouble(value.toString());
                } catch (NumberFormatException ignored) {
                    return Double.NaN;
                }
            }
        } else if ("String".equalsIgnoreCase(type.toString())) {
            return value.toString();
        } else if ("Boolean".equalsIgnoreCase(type.toString())) {
            return value instanceof String ?
                    Boolean.valueOf("true".equalsIgnoreCase((String) value)) :
                    value instanceof Boolean ? value : Boolean.valueOf(false);
        } else if ("Invocation".equalsIgnoreCase(type.toString())) {
            return value instanceof JSONObject ? new Invocation((JSONObject) value) : new Invocation();
        }
        return unwrapJsonObject(value);
    }

    private static Map<Object, Object> toMap(JSONObject arg) {
        if (arg == null) {
            return null;
        }
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        for (String key : (Iterable<String>) arg::keys) {
            map.put(key, arg.opt(key));
        }
        return map;
    }

    public void setArgs(Object[] args) throws JSONException {
        for (int i = 0; i < args.length; i++) {
            args[i] = unwrapJsonObject(args[i]);
        }
        this.args = args;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invocation)) return false;

        Invocation that = (Invocation) o;

        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        return Arrays.equals(args, that.args);

    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
