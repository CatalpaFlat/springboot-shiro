package cn.catalpaflat.config.http;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HttpResponseSupport {

    private HttpResponseSupport(){}

    private static JSONObject responseJson;

    private static JSONObject responseErrorJson;

    static {
        responseJson = new JSONObject();
        responseErrorJson = new JSONObject();
    }

    public synchronized static HttpEntity<Object> success(){
        return obtainResponseEntity(HttpStatus.OK,responseJson);
    }

    public synchronized static HttpEntity<Object> success(Object obj){
        return obtainResponseEntity(HttpStatus.OK,obj);
    }

    public synchronized static HttpEntity<Object> error(HttpStatus httpStatus,String msg,String reason){
        responseErrorJson.put("message",msg);
        responseErrorJson.put("reason",reason);
        return obtainResponseEntity(httpStatus,responseErrorJson);
    }

    private static ResponseEntity<Object> obtainResponseEntity(HttpStatus httpStatus, Object response) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        return new ResponseEntity<Object>(response, headers, httpStatus);
    }
}
