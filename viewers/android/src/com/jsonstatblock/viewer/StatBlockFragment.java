package com.jsonstatblock.viewer;


import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StatBlockFragment extends Fragment {

	JSONObject statblock = null;
	TextView statblockTv = null;
	String detailskeys[] = null; 
	HashMap<String, TextView> stat_views;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void parseJSON(String json) {
		try {
			statblock = new JSONObject(json);
		} catch (JSONException e) {
			statblock = null;
			onJOSNError(e);
		}
	}
	
	protected void onJOSNError(JSONException e) {
		Toast.makeText(getActivity(), "JSON Error: " + e.toString(), Toast.LENGTH_LONG).show();
		e.printStackTrace();
	}
	
	protected String titlecase(String s) {
		String[] words = s.split("_");
		StringBuilder sb = new StringBuilder();
		if (words[0].length() > 0) {
		    sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString());
		    for (int i = 1; i < words.length; i++) {
		        sb.append(" ");
		        sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString());
		    }
		}
		return sb.toString();
	}
	
	protected SpannableString formatedKeyValue(JSONObject json, String key, int style) {
		try {
			JSONArray a = json.optJSONArray(key);
			String value = null;
			if(a == null) {
				value = json.optString(key,"");
			} else {
				value = a.join(", ").replace("\"", "");
			}
			String s = titlecase(key) + ": " + value;
			SpannableString ss = new SpannableString(s);
			ss.setSpan(new TextAppearanceSpan(getActivity(),style), 0, key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return ss;
		} catch (JSONException e) {
			onJOSNError(e);
			return new SpannableString("");
		}
	}
	
	protected SpannableString formatedAttr(JSONObject json, String key) {
		return formatedKeyValue(json, key, R.style.sb_txt_attr);
	}
	
	protected SpannableString formatedValue(JSONObject json, String key, int style) {
		SpannableString ss = new SpannableString(json.optString(key, ""));
		ss.setSpan(new TextAppearanceSpan(getActivity(),style), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ss;
	}
	
	protected String joinArray(JSONArray a) {
		try {
			return a.join(", ").replace("\"", "");
		} catch (JSONException e) {
			return "";
		}
	}
	
	protected CharSequence buildDetails() {
		//JSONObject detection = statblock.optJSONObject("detection");
		CharSequence sb = TextUtils.concat(statblock.optString("sex")," ",statblock.optString("race")," ",joinArray(statblock.optJSONArray("class")),"\n",
				statblock.optString("alignment")," ",statblock.optString("size")," ",statblock.optString("type"));
		return sb;
	}
	
	protected CharSequence buildDetection() {
		JSONObject detection = statblock.optJSONObject("detection");
		CharSequence sb = TextUtils.concat(formatedAttr(detection, "init"), "; ", formatedAttr(detection, "senses"), "; Perception ",
				statblock.optString("perception"));
		return sb;
	}
	
	protected void json2views() {
		stat_views.get("name").setText(statblock.optString("name"));
		stat_views.get("cr").setText(statblock.optString("cr"));
		stat_views.get("details").setText(buildDetails());
		stat_views.get("detection").setText(buildDetection());
	}
	
	protected CharSequence buildStatBlock() {
		CharSequence sb = buildDetails();
		
		/*for(int i=0, len = detailskeys.length; i<len; i++) {
			sb = TextUtils.concat(sb,formatedKeyValue(statblock,detailskeys[i],R.style.sb_txt_attr),"\n");
		}*/
		return sb;
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		stat_views = new HashMap<String, TextView>(2);
		detailskeys = getActivity().getResources().getStringArray(R.array.statblock_details);
		View v = inflater.inflate(R.layout.pathfinder_npc_standard, container, false);
		stat_views.put("name",  (TextView) v.findViewById(R.id.name));
		stat_views.put("cr",  (TextView) v.findViewById(R.id.cr));
		stat_views.put("details",  (TextView) v.findViewById(R.id.details));
		stat_views.put("detection",  (TextView) v.findViewById(R.id.detection));
		json2views();
		//statblockTv = (TextView) v.findViewById(R.id.statblock);
		//statblockTv.setText(buildStatBlock());
		return(v);
	}

}
