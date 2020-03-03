package adx;

import com.google.gson.annotations.SerializedName;

public class Campaign {

    @SerializedName("campaignId") String campaignId;
    @SerializedName("blockContentId") String blockContentId;
    @SerializedName("timeDelay") String timeDelay;
    @SerializedName("elementId") String elementId;
    @SerializedName("content") String content;
    @SerializedName("javascript") String javascript;
    @SerializedName("css") String css;
    @SerializedName("positionId") int positionId;
    @SerializedName("closeButton") boolean closeButton;

    public Campaign(String campaignId, String blockContentId, String timeDelay, String elementId, String content, String javascript,
                    String css, int positionId, boolean closeButton) {
        this.campaignId = campaignId;
        this.blockContentId = blockContentId;
        this.timeDelay = timeDelay;
        this.elementId = elementId;
        this.content = content;
        this.javascript = javascript;
        this.css = css;
        this.positionId = positionId;
        this.closeButton = closeButton;
    }

    public Campaign() {
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getBlockContentId() {
        return blockContentId;
    }

    public void setBlockContentId(String blockContentId) {
        this.blockContentId = blockContentId;
    }

    public String getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(String timeDelay) {
        this.timeDelay = timeDelay;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public boolean isCloseButton() {
        return closeButton;
    }

    public void setCloseButton(boolean closeButton) {
        this.closeButton = closeButton;
    }
}
