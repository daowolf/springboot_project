package com.cecdata.esapi;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsapiApplicationTests {

	@Test
	public void contextLoads() {
		String url = "http://10.20.30.3:9200/greenplum/logs/_search";
		String postJson= "{\"query\":{\"bool\":{\"should\":[{\"match\": {\"sql\": \"sfzh\"}},{\"match\": {\"user\": \"jkda\"}}]}}}";
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		try {
			StringEntity stringEntity = new StringEntity(postJson);
			httpPost.setEntity(stringEntity);
			httpPost.setHeader("Content-Type","application/json");
			HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
			String response = EntityUtils.toString(httpResponse.getEntity());
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
