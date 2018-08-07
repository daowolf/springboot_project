package com.cecdata.esapi.service;

import com.cecdata.esapi.basic.TriggerRequest;
import com.cecdata.esapi.basic.TriggerResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class EsService {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(EsService.class);

    public String search(String url) {

        String reponse = null;

        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            reponse = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reponse;
    }

    public String getHitsTotal(String json) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode.get("hits").get("total").toString();
    }


    public int sendMessage(String url, String keyWord, int benchmark) {

        int statusCode = 404;

        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity stringEntity = new StringEntity(keyWord + "查询次数大于" + benchmark, Charset.forName("UTF-8"));
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return statusCode;

    }

    public String triggerPoint(String content) {
        String json = null;
        TriggerRequest requestJson = null;
        try {
            requestJson = objectMapper.readValue(content, TriggerRequest.class);
        } catch (Exception e) {
            log.error("传过来的数据格式有问题", e);
        }
        String esUrl = requestJson.getEsUrl();
        String esColumn = requestJson.getEsColumn();
        int benchMark = requestJson.getBenchMark();
        String queryWord = requestJson.getQueryWord();
        String user = requestJson.getUser();
        String postJson = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"terms\": {\n" +
                "            \"user\": [\n" +
                "              \""+user+"\"\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"query_string\": {\n" +
                "            \"default_field\": \""+esColumn+"\",\n" +
                "            \"query\": \"\\\""+queryWord+"\\\"\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"must_not\": []\n" +
                "    }\n" +
                "  },\n" +
                "  \"from\": 0,\n" +
                "  \"size\": 50,\n" +
                "  \"sort\": []\n" +
                "}";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(esUrl);
        httpPost.setHeader("Content-Type", "application/json");
        try {
            StringEntity entity = new StringEntity(postJson);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String total = getHitsTotal(EntityUtils.toString(response.getEntity()));
                TriggerResponse triggerResponse = new TriggerResponse();
                int count =Integer.valueOf(total);
                String messageJson ="{\"count\":"+count+",\"keyword\":\""+queryWord+"\",\"EsColumn\":\""+esColumn+"\"}";
                if ( count < benchMark) {
                    triggerResponse.setStatus(1);
                } else {
                    triggerResponse.setStatus(2);
                }
                triggerResponse.setMessage(messageJson);
                json = objectMapper.writeValueAsString(triggerResponse);
            }
        } catch (Exception e) {
            log.error("Post请求错误", e);
        } finally {
            httpPost.releaseConnection();
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}
