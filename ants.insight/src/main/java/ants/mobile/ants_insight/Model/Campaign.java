package ants.mobile.ants_insight.Model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class Campaign {
    private String content;
    private String medium;
    private String name;
    private String resource;
    private String term;

    public Campaign() {
    }

    public Campaign(String content, String medium, String name, String resource, String term) {
        this.content = content;
        this.medium = medium;
        this.name = name;
        this.resource = resource;
        this.term = term;
    }

    public JSONObject getCampaign() {

        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(content))
                jsonObject.put("content", content);

            if (!TextUtils.isEmpty(medium))
                jsonObject.put("medium", medium);

            if (!TextUtils.isEmpty(name))
                jsonObject.put("name", name);

            if (!TextUtils.isEmpty(term))
                jsonObject.put("term", term);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public void setCampaignName(String name) {
        this.name = name;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
