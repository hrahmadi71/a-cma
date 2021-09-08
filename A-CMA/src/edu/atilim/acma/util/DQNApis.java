package edu.atilim.acma.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.lang.reflect.Type;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class DQNApis {
	// private static final String DQNServerAddress = "http://172.17.3.115:5000/";
	private static final String DQNServerAddress = "http://127.0.0.1:5000/";

	private static class TrainRequestBody{
		private Double[] old_state;
		private int action;
		private Double[] new_state;
		private double reward;
		
		public TrainRequestBody(Double[] oldState, int actionId, Double[] newState, double reward) {
			this.old_state = oldState;
			this.action = actionId;
			this.new_state = newState;
			this.reward = reward;
		}
	}
	
	private static class GetQValuesRequestBody{
		private Double[] state;
		
		public GetQValuesRequestBody(Double[] state) {
			this.state = state;
		}
	}
	
	private static class GetQValuesResponseBody{
		private double[] q_values;
		
		public GetQValuesResponseBody(double[] q_values) {
			this.q_values = q_values;
		}
	}
	
	private static String getStringRequestBody(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	private static class ApiRequest{
		private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		private String baseUrl = new String();
		
		public ApiRequest(String baseUrl) {
			this.baseUrl = baseUrl;
		}
				
		private String post(String url, String json) throws IOException {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder()
					.url(baseUrl + url)
					.post(body)
					.build();
			OkHttpClient client = new OkHttpClient();
			try (Response response = client.newCall(request).execute()) {
				return response.body().string();
			}
		}
	}


	public static boolean train(Double[] oldState, int actionId, Double[] newState, double reward) {
//		System.out.println("reward: " + Double.toString(reward) + " actionId: " + Integer.toString(actionId));
		TrainRequestBody requestBodyObject = new TrainRequestBody(oldState, actionId, newState, reward);
		ApiRequest req = new DQNApis.ApiRequest(DQNServerAddress);
		try {
			req.post("get_experience/", getStringRequestBody(requestBodyObject));
			return true;
		} catch (IOException e) {
			System.out.println(e.toString());
			return false;
		}
	}
	
	public static double[] getQValues(Double[] state) {
		GetQValuesRequestBody requestBodyObject = new GetQValuesRequestBody(state);
		ApiRequest req = new DQNApis.ApiRequest(DQNServerAddress);
		try {
			String result = req.post("get_q_values/", getStringRequestBody(requestBodyObject));
			GetQValuesResponseBody response = new Gson().fromJson(result, GetQValuesResponseBody.class);
						
			return response.q_values;
		} catch (IOException e) {
			System.out.println(e.toString());
			return null;
		}
	}
}
