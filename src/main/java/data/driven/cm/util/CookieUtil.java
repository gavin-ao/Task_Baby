package data.driven.cm.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 何晋凯
 * @date 2018/07/01
 */
public class CookieUtil {

    public static void addCookie(String name, String value, int maxage, String path, HttpServletRequest request,HttpServletResponse response)
    {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxage);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    public static void addCookie(String name, String value, int maxage,HttpServletRequest request, HttpServletResponse response)
    {
        addCookie(name, value, maxage, "/", request, response);
    }

    public static String getCookie(HttpServletRequest request, String name)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(name)) {
                return cookies[i].getValue();
            }
        }
        return null;
    }
}
