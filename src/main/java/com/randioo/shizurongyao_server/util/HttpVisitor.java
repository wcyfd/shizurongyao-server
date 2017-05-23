package com.randioo.shizurongyao_server.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpVisitor {
	private PoolingHttpClientConnectionManager cm;
	private int maxTotal = 50;
	private int maxPerRoute = 5;
	private String url;
	private static String UTF_8 = "UTF-8";

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void init() {
		if (cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(maxTotal);// 整个连接池最大连接数
			cm.setDefaultMaxPerRoute(maxPerRoute);// 每路由最大连接数，默认值是2
		}
	}

	public CloseableHttpClient getHttpClient() {
		init();
		return HttpClients.custom().setConnectionManager(cm).build();
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String httpGetRequest(String url) {
		HttpGet httpGet = new HttpGet(url);
		return getResult(httpGet);
	}

	public String httpGetRequest(Map<String,Object> params) throws URISyntaxException{
		URIBuilder ub = new URIBuilder();
		ub.setPath(url);

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		ub.setParameters(pairs);

		HttpGet httpGet = new HttpGet(ub.build());
		return getResult(httpGet);
	}
	
	public String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException {
		URIBuilder ub = new URIBuilder();
		ub.setPath(url);

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		ub.setParameters(pairs);

		HttpGet httpGet = new HttpGet(ub.build());
		return getResult(httpGet);
	}

	public String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
			throws URISyntaxException {
		URIBuilder ub = new URIBuilder();
		ub.setPath(url);

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		ub.setParameters(pairs);

		HttpGet httpGet = new HttpGet(ub.build());
		for (Map.Entry<String, Object> param : headers.entrySet()) {
			httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
		}
		return getResult(httpGet);
	}

	public String httpPostRequest(String url) {
		HttpPost httpPost = new HttpPost(url);
		return getResult(httpPost);
	}

	public String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
		return getResult(httpPost);
	}

	public String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
			throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);

		for (Map.Entry<String, Object> param : headers.entrySet()) {
			httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
		}

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));

		return getResult(httpPost);
	}

	private ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
		}

		return pairs;
	}

	/**
	 * 处理Http请求
	 * 
	 * @param request
	 * @return
	 */
	private String getResult(HttpRequestBase request) {
		// CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpClient httpClient = getHttpClient();
		try {
			CloseableHttpResponse response = httpClient.execute(request);
			// response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// long len = entity.getContentLength();// -1 表示长度未知
				String result = EntityUtils.toString(entity);
				response.close();
				// httpClient.close();
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		return "";
	}

	public static void main(String[] args) {
		final HttpVisitor util = new HttpVisitor();
		Map<String, Object> attrMap = new HashMap<>();
		attrMap.put("key", "f4f3f65d6d804d138043fbbd1843d510");
//		attrMap.put(
//				"arr",
//				"{\"account\":\"1\",\"game_name\":\"1\",\"game_star\":\"1\",\"match_name\":\"1\",\"user_name\":\"1\",\"game_icon\":\"1\",\"state\":\"1\",\"type\":\"1\",\"scale\":\"1\",\"bouns\":\"1\",\"bouns_fp\":\"1\",\"war_number\":\"1\",\"zb_time\":\"1\",\"match_info\":\"1\"}");
		attrMap.put("key","f4f3f65d6d804d138043fbbd1843d510");
		attrMap.put("id", "10");
		// TODO Auto-generated method stub
		try {
			String s = util.httpGetRequest("http://manager.app.randioo.com/gateway/PhpServices/Match/getZDSInfo.php", attrMap);
			System.out.println(s);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
