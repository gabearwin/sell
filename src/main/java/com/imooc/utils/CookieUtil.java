package com.imooc.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * cookie工具类
 * Created by 廖师兄
 * 2017-07-30 16:31
 */
public class CookieUtil {

    /**
     * 设置cookie
     *
     * @param response 请求返回response
     * @param name     cookie名字
     * @param value    cookie值
     * @param maxAge   过期时间
     */
    public static void set(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 通过cookie名字删除cookie
     *
     * @param response 请求返回response
     * @param name     cookie名字
     */
    public static void delete(HttpServletResponse response, String name) {
        set(response, name, null, 0);
    }

    /**
     * 获取cookie
     *
     * @param request 请求request
     * @param name    cookie的名字
     * @return 由名字得到的cookie
     */
    public static Cookie get(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)) {
            return cookieMap.get(name);
        } else {
            return null;
        }
    }

    /**
     * 将cookie封装成Map
     *
     * @param request
     * @return
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
}
