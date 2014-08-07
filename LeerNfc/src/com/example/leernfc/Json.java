package com.example.leernfc;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Json {
	
	public static JSONObject getJSON(String data){
		//JSONObject jArray = null;
		JSONObject object = null;
		try {
			object = (JSONObject) new JSONTokener(data).nextValue();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		 return object;
	}

}
