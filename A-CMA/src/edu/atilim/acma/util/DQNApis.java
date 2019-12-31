package edu.atilim.acma.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ForkJoinPool;

import okhttp3.*;
import org.json.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DQNApis {
	private static final String DQNServerAddress = "http://127.0.0.1:5000/";

	private static class TrainRequestBody{
		private double[] old_state;
		private int action;
		private double[] new_state;
		private double reward;
		
		public TrainRequestBody(double[] oldState, int actionId, double[] newState, double reward) {
			this.old_state = oldState;
			this.action = actionId;
			this.new_state = newState;
			this.reward = reward;
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


	public static boolean train(double[] oldState, int actionId, double[] newState, double reward) {
		
		TrainRequestBody requestBodyObject = new TrainRequestBody(oldState, actionId, newState, reward);
		ApiRequest req = new DQNApis.ApiRequest(DQNServerAddress);
		try {
			String resutl = req.post("get_experience/", getStringRequestBody(requestBodyObject));
			System.out.println(resutl);
			return true;
		} catch (IOException e) {
			System.out.println(e.toString());
			return false;
		}
	}
}
