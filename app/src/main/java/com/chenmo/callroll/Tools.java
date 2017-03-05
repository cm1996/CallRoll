package com.chenmo.callroll;

import java.net.URLEncoder;
import java.util.Map;

public class Tools {
    /**
     * map中存放用户名和密码的键值对
     */
    public static String sendPOSTPath(Map<String, String> params) throws Exception {
        // StringBuilder是用来组拼请求参数
        StringBuilder sb = new StringBuilder();
        if (params != null){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
