package ants.mobile.ants_insight.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringDef;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ants.mobile.ants_insight.Constants.ActionEvent;
import ants.mobile.ants_insight.InsightSharedPref;

import static ants.mobile.ants_insight.Constants.ActionEvent.USER_SIGN_IN_ACTION;

public class InsightDataRequest {
    private List<ProductItem> productItemList = new ArrayList<>();
    private ContextModel contextModel;
    private ExtraItem extraItem;
    private String eventAction;
    private String eventCategory;
    private List<Dimension> dimensionList;
    private static final Long INTERVAL_REFRESH_SECTION = 1800000L; // milli second
    private Context mContext;
    private boolean isCustomizeAction = false;
    private String eventActionCustom;
    private String eventCategoryCustom;
    private UserItem userItem;
    private static String sections = "";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ActionEvent.PRODUCTS_SEARCHED_ACTION, ActionEvent.PRODUCT_LIST_VIEWED_ACTION,
            ActionEvent.PRODUCT_LIST_FILTERED_ACTION, ActionEvent.PRODUCT_CLICK_ACTION,
            ActionEvent.PRODUCT_VIEW_ACTION, ActionEvent.ADD_TO_CART_ACTION, ActionEvent.REMOVE_CART_ACTION,
            ActionEvent.CART_VIEW_ACTION, ActionEvent.CHECKOUT_ACTION, ActionEvent.PAYMENT_INFO_ENTERED_ACTION,
            ActionEvent.PURCHASE_ACTION, ActionEvent.SCREEN_VIEW_ACTION, ActionEvent.USER_IDENTIFY_ACTION,
            ActionEvent.USER_SIGN_OUT_ACTION, USER_SIGN_IN_ACTION, ActionEvent.IMPRESSION_ACTION, ActionEvent.VIEWABLE_ACTION, ActionEvent.ADX_CLICK_ACTION})
    private @interface validateActionEvent {
    }

    public InsightDataRequest(Context mContext, String eventAction) {
        this.eventAction = eventAction;
        this.mContext = mContext;
        contextModel = new ContextModel(mContext);
    }

    public InsightDataRequest(Context mContext) {
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JSONObject getDataRequest() {
        JSONObject param = new JSONObject();
        try {
            param.put("uid", getUID());
            param.put("aid", getAid());
            param.put("sid", getSectionId());
            param.put("ea", getEventAction());
            param.put("ec", getEventCategory());
            param.putOpt("context", contextModel.getContextModel());

            if (getDimension() != null)
                param.putOpt("dims", getDimension());

            if (productItemList != null && productItemList.size() > 0)
                param.putOpt("items", getProducts());
            else if (userItem != null)
                param.putOpt("items", getUser());

            if (InsightSharedPref.getIsFirstInstallApp(mContext)) {
                JSONObject extraParam = new JSONObject();

                extraParam.put("onesignal_id", InsightSharedPref.getPushNotificationId(mContext));
                param.putOpt("extra", extraParam);

                InsightSharedPref.setIsFirstInstallApp(mContext, false);

            } else {
                if (extraItem != null)
                    param.putOpt("extra", getExtraItem());

                if (userItem != null && userItem.getUserInfo(mContext) != null && !"reset_anonymous_id".equals(getEventAction()))
                    param.putOpt("extra", userItem.getUserInfo(mContext));

                if ("reset_anonymous_id".equals(getEventAction())) {
                    AnonymousItem anonymousItem = new AnonymousItem(getUID(), getNewUID());
                    param.put("extra", anonymousItem.getAnonymousItem());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    public void setContextModel(ContextModel contextModel) {
        this.contextModel = contextModel;
    }

    private List<ProductItem> getProductItemList() {
        return productItemList;
    }

    private String getUID() {
        String uid;
        if (getAnonymousIndex() != 0)
            uid = getAid() + "_" + (getAnonymousIndex());
        else
            uid = getAid();
        return uid;
    }

    private String getNewUID() {
        String uid;
        uid = getAid() + "_" + (getAnonymousIndex() + 1);
        return uid;
    }

    @SuppressLint("HardwareIds")
    private String getAid() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getEventCategory(@NonNull String eventAction) {
        String category = "";

        switch (eventAction) {
            case ActionEvent.IMPRESSION_ACTION:
            case ActionEvent.ADX_CLICK_ACTION:
                category = ActionEvent.ADVERTISING_CATEGORY;
                break;
            case ActionEvent.USER_IDENTIFY_ACTION:
            case ActionEvent.USER_SIGN_OUT_ACTION:
                category = ActionEvent.USER_IDENTIFY_CATEGORY;
                break;
            case ActionEvent.SCREEN_VIEW_ACTION:
                if (getProductItemList().size() == 0)
                    category = ActionEvent.SCREEN_VIEW_CATEGORY;
                break;
            case ActionEvent.PRODUCT_LIST_FILTERED_ACTION:
            case ActionEvent.PRODUCT_LIST_VIEWED_ACTION:
            case ActionEvent.PRODUCTS_SEARCHED_ACTION:
                category = ActionEvent.BROWSING_CATEGORY;
                break;
        }
        for (int i = 0; i < ActionEvent.actionListHasCategoryProduct().size(); i++) {
            if (ActionEvent.actionListHasCategoryProduct().contains(eventAction)) {
                category = ActionEvent.PRODUCT_CATEGORY;
                break;
            }
        }
        return eventCategory = category;
    }

    private String getSectionId() {
        if (TextUtils.isEmpty(sections)) sections = getSections();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sections = getSections();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, INTERVAL_REFRESH_SECTION);
        return sections;
    }

    private static String getSections() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm", Locale.getDefault());
        return formatter.format(todayDate);
    }

    public void setEventCustom(@NonNull String eventActionCustom, @NonNull String eventCategoryCustom) {
        isCustomizeAction = true;
        this.eventActionCustom = eventActionCustom;
        this.eventCategoryCustom = eventCategoryCustom;
    }

    public void setEventAction(@validateActionEvent String eventAction) {
        this.eventAction = eventAction;
    }


    private String getEventActionCustom() {
        return this.eventActionCustom;
    }

    private String getEventAction() {
        return eventAction = isCustomizeAction ? getEventActionCustom()
                : eventAction;
    }

    private String getEventCategoryCustom() {
        return eventCategoryCustom;
    }

    private String getEventCategory() {
        return eventCategory = isCustomizeAction ? getEventCategoryCustom()
                : this.getEventCategory(eventAction);
    }

    public void setProductItemList(List<ProductItem> productItemList) {
        this.productItemList = productItemList;
    }

    public void setDimensionList(List<Dimension> dimensionList) {
        this.dimensionList = dimensionList;
    }

    public void setExtraItem(ExtraItem extraItem) {
        this.extraItem = extraItem;
    }

    private JSONObject getExtraItem() {
        return extraItem.getExtraData();
    }

    private JSONArray getProducts() {
        if (productItemList == null)
            return null;
        JSONArray array = new JSONArray();
        for (ProductItem productItem : productItemList) {
            array.put(productItem.getProduct());
        }
        return array;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private JSONArray getUser() {
        if (userItem == null)
            return null;
        List<UserItem> userList = new ArrayList<>();
        userList.add(userItem);
        JSONArray array = new JSONArray();
        for (UserItem userItem : userList) {
            if (userItem.getUserInfo(mContext) != null)
                array.put(userItem.getUserInfo(mContext));
        }
        return array;
    }

    public UserItem getUserItem() {
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public void setCampaign(Campaign campaign) {
        if (contextModel != null)
            contextModel.setCampaign(campaign);
    }

    private JSONObject getDimension() {
        if (dimensionList == null)
            return null;
        JSONObject dimensionObject = new JSONObject();
        for (Dimension dimension : dimensionList) {
            try {
                dimensionObject.putOpt(dimension.getDimensionCategory(), dimension.getDimensionObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dimensionObject;
    }

    private int getAnonymousIndex() {
        int index = 0;
        String indexFromFile = Anonymous.getInstance().getIndexFromStorageLocal().toString();
        if (TextUtils.isEmpty(indexFromFile)) {
            return 0;
        } else {
            try {
                index = Integer.parseInt(indexFromFile);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return index;
    }

    public void updateAnonymousIndex() {
        int newIndex = getAnonymousIndex() + 1;
        Anonymous.getInstance().saveIndexToStorageLocal(mContext, String.valueOf(newIndex));
    }
}
