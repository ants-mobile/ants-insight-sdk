package ants.mobile.ants_insight.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Dimension {
    private String id;
    private String name;
    private String dimensionCategory;

    public Dimension(String itemCategory, String itemId, String itemName) {
        this.id = itemId;
        this.name = itemName;
        this.dimensionCategory = itemCategory;
    }


    public JSONObject getDimensionObject() {
        JSONObject param = new JSONObject();
        try {
            param.put("id", id);
            param.put("name", name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return param;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDimensionCategory() {
        return dimensionCategory;
    }

    public void setDimensionCategory(String dimensionCategory) {
        this.dimensionCategory = dimensionCategory;
    }
}
