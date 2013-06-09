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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StatBlockFragment extends Fragment {

	JSONObject statblock = null;
	TextView statblockTv = null;
	String detailskeys[] = null;
    String skillkeys[] = null;
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
        if(words[0].length() == 2 && words.length==1) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(Character.toUpperCase(words[0].charAt(1)));
        } else if (words[0].length() > 0) {
		    sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString());
		    for (int i = 1; i < words.length; i++) {
		        sb.append(" ").append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString());
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
            value = value.replace("\\", "");
			String s = titlecase(key) + " " + value;
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
		CharSequence sb = TextUtils.concat(statblock.optString("sex")," ",statblock.optString("race")," ",joinArray(statblock.optJSONArray("class")),"\n",
				statblock.optString("alignment")," ",statblock.optString("size")," ",statblock.optString("type"));
		return sb;
	}
	
	protected CharSequence buildDetection() {
		JSONObject detection = statblock.optJSONObject("detection");
		CharSequence sb = TextUtils.concat(formatedAttr(detection, "init"), "; ", formatedAttr(detection, "senses"), "; Perception ",
                detection.optString("perception"));
		return sb;
	}

    protected CharSequence buildAC() {
        JSONObject defenses = statblock.optJSONObject("defenses");
        CharSequence sb = TextUtils.concat(formatedAttr(defenses, "ac"), ", touch ", defenses.optString("touch"), ", flat-footed ",
                defenses.optString("flat-footed")," ", defenses.optString("ac_details"));
        return sb;
    }

    protected CharSequence buildSaves() {
        JSONObject defenses = statblock.optJSONObject("defenses");
        CharSequence sb = TextUtils.concat(formatedAttr(defenses, "fort"), ", ",formatedAttr(defenses, "ref"), ", ",formatedAttr(defenses, "will"));
        return sb;
    }

    protected CharSequence buildFightSpace() {
        JSONObject g = statblock.optJSONObject("offensive");
        CharSequence sb = TextUtils.concat(formatedAttr(g, "space"), "; ",formatedAttr(g, "reach"));
        return sb;
    }

    protected CharSequence buildAbilityScores() {
        JSONObject g = statblock.optJSONObject("statistics");
        CharSequence sb = TextUtils.concat(formatedAttr(g, "str"), ", ",formatedAttr(g, "dex"), ", ",
                formatedAttr(g, "con"), ", ",formatedAttr(g, "int"), ", ",formatedAttr(g, "wis"), ", ",formatedAttr(g, "cha"));
        return sb;
    }

    protected CharSequence buildAttack() {
        JSONObject g = statblock.optJSONObject("statistics");
        CharSequence sb = TextUtils.concat(formatedAttr(g, "base_atk"), "; ",formatedAttr(g, "cmb"), "; ",formatedAttr(g, "cmd"));
        return sb;
    }

    protected CharSequence buildSkills() {
        try {
            JSONObject skills = statblock.getJSONObject("skills");
            CharSequence sb = "";
            boolean first = true;
            for(int i=0,len=skillkeys.length;i<len;i++) {
                String k = skillkeys[i];
                String s = skills.optString(k);
                //Log.d("skill key: ",k);
                if(!s.equals("0")) {
                    sb = (first) ? TextUtils.concat(sb,formatedKeyValue(skills, k, R.style.sb_txt)) : TextUtils.concat(sb,", ",formatedKeyValue(skills, k, R.style.sb_txt));
                    first = false;
                }
            }
            SpannableString ss = new SpannableString("Skills ");
            ss.setSpan(new TextAppearanceSpan(getActivity(),R.style.sb_txt_attr), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb = TextUtils.concat(ss,sb);
            return sb;
        } catch (JSONException e) {
            return "";
        }
    }

    protected void setBasicStatView(String group_key, String key) {
        JSONObject g = statblock.optJSONObject(group_key);
        stat_views.get(key).setText(formatedAttr(g,key));
    }

	protected void json2views() {
		stat_views.get("name").setText(statblock.optString("name"));
		stat_views.get("cr").setText(statblock.optString("cr"));
		stat_views.get("details").setText(buildDetails());
		stat_views.get("detection").setText(buildDetection());
        stat_views.get("ac").setText(buildAC());
        setBasicStatView("defenses","hp");
        stat_views.get("saves").setText(buildSaves());
        setBasicStatView("defenses","defensive_abilities");
        setBasicStatView("offensive","speed");
        setBasicStatView("offensive","melee");
        setBasicStatView("offensive","ranged");
        setBasicStatView("offensive","special_attacks");
        setBasicStatView("offensive","spell_like_abilities");
        stat_views.get("fighting_space").setText(buildFightSpace());
        stat_views.get("ability_scores").setText(buildAbilityScores());
        stat_views.get("attack").setText(buildAttack());
        setBasicStatView("statistics","feats");
        stat_views.get("skills").setText(buildSkills());
        setBasicStatView("statistics","languages");
        setBasicStatView("statistics","sq");
        setBasicStatView("statistics","combat_gear");
        setBasicStatView("statistics","other_gear");

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
        skillkeys = getActivity().getResources().getStringArray(R.array.skill_keys);
		View v = inflater.inflate(R.layout.pathfinder_npc_standard, container, false);
		stat_views.put("name",  (TextView) v.findViewById(R.id.name));
		stat_views.put("cr",  (TextView) v.findViewById(R.id.cr));
		stat_views.put("details",  (TextView) v.findViewById(R.id.details));
		stat_views.put("detection",  (TextView) v.findViewById(R.id.detection));
        stat_views.put("ac",  (TextView) v.findViewById(R.id.ac));
        stat_views.put("hp",  (TextView) v.findViewById(R.id.hp));
        stat_views.put("saves",  (TextView) v.findViewById(R.id.saves));
        stat_views.put("defensive_abilities",  (TextView) v.findViewById(R.id.defensive_abilities));

        stat_views.put("speed",  (TextView) v.findViewById(R.id.speed));
        stat_views.put("melee",  (TextView) v.findViewById(R.id.melee));
        stat_views.put("ranged",  (TextView) v.findViewById(R.id.ranged));
        stat_views.put("fighting_space",  (TextView) v.findViewById(R.id.fighting_space));
        stat_views.put("special_attacks",  (TextView) v.findViewById(R.id.special_attacks));
        stat_views.put("spell_like_abilities",  (TextView) v.findViewById(R.id.spell_like_abilities));

        stat_views.put("ability_scores",  (TextView) v.findViewById(R.id.ability_scores));
        stat_views.put("attack",  (TextView) v.findViewById(R.id.attack));
        stat_views.put("feats",  (TextView) v.findViewById(R.id.feats));
        stat_views.put("skills",  (TextView) v.findViewById(R.id.skills));
        stat_views.put("languages",  (TextView) v.findViewById(R.id.languages));
        stat_views.put("sq",  (TextView) v.findViewById(R.id.sq));
        stat_views.put("combat_gear",  (TextView) v.findViewById(R.id.combat_gear));
        stat_views.put("other_gear",  (TextView) v.findViewById(R.id.other_gear));

		json2views();
		//statblockTv = (TextView) v.findViewById(R.id.statblock);
		//statblockTv.setText(buildStatBlock());
		return(v);
	}

}
