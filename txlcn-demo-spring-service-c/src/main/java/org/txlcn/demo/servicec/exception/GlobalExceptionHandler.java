package org.txlcn.demo.servicec.exception;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理类
 * 将业务异常以json的格式返回
 *
 * @author qinminghui
 * @date 2018/11/14 18:19
 */
@RestControllerAdvice
@Configuration
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public Map<String, String> allExceptionHandler(HttpServletRequest req, Exception e) {
        Map<String, String> r = new HashMap<>(16);
        r.put("code", "500");
        r.put("msg", e.getMessage());
        return r;
    }


}
