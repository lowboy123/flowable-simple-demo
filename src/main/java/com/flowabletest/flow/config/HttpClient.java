package com.flowabletest.flow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * http请求工具类，需要配置 RestTemplate 才能使用
 *
 * @author: Lizq
 * @Date: 2019/12/5 10:19
 */
@Component
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    @Autowired
    private RestTemplate restTemplate;


    /**
     * 接口调用
     * 设置json请求头，将map数据转换为json放入请求体中发起请求，出现异常会返回异常信息
     *
     * @param url
     * @param param
     * @param urlparam 带上token
     * @return 返回结果
     */
    public String postForObject(String url, Map<String, Object> urlparam, Map<String, Object> param, String method) {
        url = doUrl(url, urlparam);
        try {
            //设置请求头
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            if (method.equals("merge")) {
                httpHeaders.add("X-HTTP-Method-Override", "MERGE");
            }
            HttpEntity request = new HttpEntity<Map<String, Object>>(param, httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode.equals(HttpStatus.OK) || statusCode.equals(HttpStatus.CREATED)) {
                logger.error("-----------接口请求ok，url = " + url + ", 参数：" + param + "返回码：" + statusCode.value());
                String body = responseEntity.getBody();
                return body;
            } else {
                logger.error("-----------接口请求失败，url = " + url + ", 参数：" + param + "返回码：" + statusCode.value());
                String body = responseEntity.getBody();
                return body;
            }
        } catch (RestClientResponseException e) {
            logger.error("请求异常 {} ", e.getResponseBodyAsString(), e);
            logger.error("url = " + url + ", 参数：" + param);
            return "请求异常" + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("------------请求接口出错：url = " + url + "， 参数：" + param, e);
            return "程序异常";
        }

    }

    /**
     * 传入map的参数会自动进行判断和拼接
     *
     * @param url      访问路径
     * @param urlparam 需要传入在地址的参数
     * @return 返回结果
     */
    public String getForObject(String url, Map<String, Object> urlparam) {
        url = doUrl(url, urlparam);
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode.equals(HttpStatus.OK)) {
                String body = responseEntity.getBody();
                return body;
            } else {
                logger.error("-----------接口请求失败，url = " + url + ", 参数：" + urlparam + "返回码：" + statusCode.value());
                return null;
            }
        } catch (RestClientResponseException e) {
            logger.error("请求异常 {} ", e.getResponseBodyAsString(), e);
            logger.error("url = " + url + ", 参数：" + urlparam);
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("------------请求接口出错：url = " + url + "， 参数：" + urlparam, e);
            return "程序异常";
        }

    }

    /**
     * @param url      请求路径
     * @param urlparam 传入地址的参数
     * @param param    请求体中的参数
     * @return 查询结果
     */
    public String putForObject(String url, Map<String, Object> urlparam, Map<String, Object> param) {
        url = doUrl(url, urlparam);
        try {
            //设置请求头
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(param, httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode.equals(HttpStatus.OK) || statusCode.equals(HttpStatus.CREATED)) {
                String body = responseEntity.getBody();
                return body;
            } else {
                logger.error("-----------接口请求失败，url = " + url + ", 参数：" + param + "返回码：" + statusCode.value());
                return null;
            }
        } catch (RestClientResponseException e) {
            logger.error("出现异常 {} ", e.getResponseBodyAsString(), e);
            logger.error("url = " + url + ", 参数：" + param);
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("------------请求接口出错：url = " + url + "， 参数：" + param, e);
            return "程序异常";
        }

    }

    public String deleteforObject(String url, Map<String, Object> urlparam) {
        //判断param是否为空
        url = doUrl(url, urlparam);
        try {
            //设置请求头
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();
            //等于204是业务的特殊需求，左边为一般情况
            if (statusCode.equals(HttpStatus.OK) || statusCode.equals(HttpStatus.NO_CONTENT)) {
                String body = responseEntity.getBody();
                return body;
            } else {
                logger.error("-----------接口请求失败，url = " + url + ", 参数：" + urlparam + "返回码：" + statusCode.value());
                return "-1";
            }
        } catch (RestClientResponseException e) {
            logger.error("请求异常 {} ", e.getResponseBodyAsString(), e);
            logger.error("url = " + url + ", 参数：" + urlparam);
            return e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("------------请求接口出错：url = " + url + "， 参数：" + urlparam, e);
            return "程序异常";
        }

    }

    private static String doUrl(String url, Map<String, Object> urlparam) {
        //判断param是否为空
        if (urlparam == null || urlparam.isEmpty()) {
        } else {
            //不为空进行url拼接
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("?");
            for (Map.Entry<String, Object> entry : urlparam.entrySet()) {
                stringBuilder.append(entry.getKey() + "=" + entry.getValue() + "&");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            url = url + stringBuilder.toString();
        }
        return url;
    }
}
