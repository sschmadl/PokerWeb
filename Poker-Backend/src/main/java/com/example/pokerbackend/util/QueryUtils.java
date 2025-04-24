package com.example.pokerbackend.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QueryUtils {
    public static Map<String, String> splitQuery(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (query == null || query.isEmpty()) return queryPairs;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                queryPairs.put(key, value);
            } catch (Exception e) {
                // handle exception or skip this param
            }
        }
        return queryPairs;
    }
}