package edu.uci.ics.fablixmobile.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ArrayHelper {
    public static ArrayList<String> FromJSON(JSONArray arr) throws JSONException {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++){
            list.add(arr.getJSONObject(i).getString("name"));
        }
        return list;
    }

    public static String toString(ArrayList<String> array, int count) {
        StringBuilder sb = new StringBuilder();
        count = Math.min(count, array.size()); // Take the minimum of 3 and the array length

        for (int i = 0; i < count; i++) {
            sb.append(array.get(i));
            if (i < count - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public static String toString(ArrayList<String> array) {
        StringBuilder sb = new StringBuilder();
        int count = array.size();
        for (int i = 0; i < count; i++) {
            sb.append(array.get(i));
            if (i < count - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
