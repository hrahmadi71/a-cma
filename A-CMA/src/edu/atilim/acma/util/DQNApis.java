package edu.atilim.acma.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Type;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import edu.atilim.acma.transition.actions.Action;

public class DQNApis {
//	private static final String DQNServerAddress = "http://172.17.9.244:5000/";
	private static final String DQNServerAddress = "http://127.0.0.1:5000/";

	private static class TrainRequestBody{
		private int action_type;
		private int action;
		private Double[] old_common_state;
		private Double[] new_common_state;
		private int[] action_params;
		private double reward;
		
		public TrainRequestBody(int actionType,  int actionId, Double[] oldState, Double[] newState, int[] actionParams, double reward) {
			this.action_type = actionType;
			this.action = actionId;
			this.old_common_state = oldState;
			this.new_common_state = newState;
			this.action_params = actionParams;
			this.reward = reward;
		}
	}
	
	public static class GetQValuesRequestBody{
		private int action_type;
		private Double[] common_state;
		private int[] action_params;
		
		public GetQValuesRequestBody(int actionType, Double[] state, int[] actionParams) {
			this.action_type = actionType;
			this.common_state = state;
			this.action_params = actionParams;
		}
	}
	
	private static class GetQValuesResponseBody{
		private double[] q_values;
		
		public GetQValuesResponseBody(double[] q_values) {
			this.q_values = q_values;
		}
	}
	
	private static class PossibleAction{
		private int action_type;
		private int[] action_params;
		
		public PossibleAction(int actionType, int[] actionParams) {
			this.action_type = actionType;
			this.action_params = actionParams;
		}
	}
	
	private static class PossibleActions{
		private List<PossibleAction> possible_actions;
		
		public PossibleActions() {
			this.possible_actions = new ArrayList<PossibleAction>();
		}
		
		public void addAction(int actionType, int[] actionParams) {
			this.possible_actions.add(new PossibleAction(actionType, actionParams));
		}
	}
	
	private static class SaveAndLoadModelRequestBody{
		private String model_name;
		
		public SaveAndLoadModelRequestBody(String modelName) {
			this.model_name = modelName;
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
		
//		private static Map<String, OkHttpClient> clients = null;
//		
//		private static getClient(String){}
				
		public String post(String url, String json) throws IOException {
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder()
					.url(baseUrl + url)
					.post(body)
					.build();
//			OkHttpClient client = new OkHttpClient();
			OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
			clientBuilder.readTimeout(20, TimeUnit.SECONDS);
			OkHttpClient client = clientBuilder.build();
			try (Response response = client.newCall(request).execute()) {
				return response.body().string();
			}
		}
	}
	
	private static ApiRequest request = null;
	private static ApiRequest getApiRequest() {
		if(request == null) {
			request = new ApiRequest(DQNServerAddress);
		}
		return request;
	}


	public static boolean train(int actionType, int actionId, Double[] oldState, Double[] newState, int[] actionParams, double reward) {
		TrainRequestBody requestBodyObject = new TrainRequestBody(actionType, actionId, oldState, newState, actionParams, reward);
//		ApiRequest req = new DQNApis.ApiRequest(DQNServerAddress);
		try {
			String result = getApiRequest().post("get_experience/", getStringRequestBody(requestBodyObject));
			return true;
		} catch (IOException e) {
			System.out.println(e.toString());
			return false;
		}
	}
	
	public static double[] getQValues(GetQValuesRequestBody requestBodyObject) {
		try {
			String result = getApiRequest().post("get_q_values/", getStringRequestBody(requestBodyObject));
			GetQValuesResponseBody response = new Gson().fromJson(result, GetQValuesResponseBody.class);

			return response.q_values;
		} catch (IOException e) {
			System.out.println(e.toString());
			return null;
		}
	}
	
	public static boolean sendPossibleActions(List<Action> actions) {
		PossibleActions possibleActions = new PossibleActions();
		for (Action a : actions) {
			possibleActions.addAction(a.getType(), a.getParams());
		}
		
		String json = new Gson().toJson(possibleActions);
		try {
			getApiRequest().post("possible_actions/", json);
			return true;
		} catch(IOException e) {
			System.out.println(e.toString());
			return false;
		}
	}
	
	public static void saveModel(String modelName) {
		SaveAndLoadModelRequestBody requestBodyObject = new SaveAndLoadModelRequestBody(modelName);
		
		try {
			String result = getApiRequest().post("save_weights/", getStringRequestBody(requestBodyObject));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}
