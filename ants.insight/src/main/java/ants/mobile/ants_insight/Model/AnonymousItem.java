package ants.mobile.ants_insight.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class AnonymousItem {
    private String resetUid;
    private String generateUid;

    public AnonymousItem(String resetUid, String generateUid) {
        this.resetUid = resetUid;
        this.generateUid = generateUid;
    }

    public JSONObject getAnonymousItem() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reset_uid", resetUid);
            jsonObject.put("generate_uid", generateUid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
