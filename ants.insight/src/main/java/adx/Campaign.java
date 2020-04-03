package adx;

import com.google.gson.annotations.SerializedName;

public class Campaign {

    @SerializedName("campaignId")
    private String campaignId;
    @SerializedName("blockContentId")
    private String blockContentId;
    @SerializedName("timeDelay")
    private String timeDelay;
    @SerializedName("elementId")
    private String elementId;
    @SerializedName("content")
    String content;
    @SerializedName("javascript")
    private String javascript;
    @SerializedName("css")
    private String css;
    @SerializedName("positionId")
    private String positionId;
    @SerializedName("closeButton")
    private boolean closeButton;

    public Campaign() {
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getBlockContentId() {
        return blockContentId;
    }

    public String getTimeDelay() {
        return timeDelay;
    }

    public String getElementId() {
        return elementId;
    }

    public String getContent() {
        return content;
    }

    public String getJavascript() {
        return javascript;
    }

    public String getCss() {
        return css;
    }

    public String getPositionId() {
        return positionId;
    }

    public boolean isCloseButton() {
        return closeButton;
    }

    public void setTimeDelay(String timeDelay) {
        this.timeDelay = timeDelay;
    }
}
