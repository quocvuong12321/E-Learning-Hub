package com.king.lms.e_learning_hub.util;


import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    public void saveCookie(String key, String value, int timeExpiredSecond, HttpServletResponse response){
        Cookie cookie = new Cookie(key,value);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(timeExpiredSecond);

        response.addCookie(cookie);
    }

    public Cookie getCookie(String key, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();

        if(cookies == null)
            throw new AppException(ErrorCode.INVALID_TOKEN);

        for(var cookie:cookies){
            if(key.equals(cookie.getName())){
                return cookie;
            }
        }
        throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    public void deleteCookie(String key,HttpServletResponse response){
        Cookie cookie = new Cookie(key,"");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

}
