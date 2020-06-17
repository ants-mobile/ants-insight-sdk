package ants.mobile.ants_insight.adx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import static ants.mobile.ants_insight.adx.Utils.dpToPx;

public class WebViewManager extends ActivityLifecycleHandler.ActivityAvailableListener {

    private static final int MARGIN_PX_SIZE = dpToPx(24);
    private static final int IN_APP_MESSAGE_INIT_DELAY = 10000;
    public static boolean isShowingAds;
    private static final String TAG = "WEBVIEW MANAGER";
    private int pageHeight;

    enum Position {
        TOP_BANNER,
        BOTTOM_BANNER,
        CENTER_MODAL,
        FULL_SCREEN;

        boolean isBanner() {
            switch (this) {
                case TOP_BANNER:
                case BOTTOM_BANNER:
                    return true;
            }
            return false;
        }
    }

    @Nullable
    private WebView webView;
    @Nullable
    private InAppMessageView messageView;

    @Nullable
    public static WebViewManager lastInstance = null;

    @NonNull
    private Activity activity;

    private Campaign campaign;

    interface InsightsGenericCallback {
        void onComplete();
    }

    private WebViewManager(Campaign campaign, @NonNull Activity activity) {
        this.activity = activity;
        this.campaign = campaign;
    }


    /**
     * Creates a new WebView
     * Dismiss WebView if already showing
     */
    public static void showHTMLString(Campaign campaign) {
        final Activity currentActivity = ActivityLifecycleHandler.curActivity;
        /* IMPORTANT
         * This is the starting route for grabbing the current Activity and passing it to InAppMessageView */
        if (currentActivity != null) {
            // Only a preview will be dismissed, this prevents normal messages from being
            // removed when a preview is sent into the app
            if (lastInstance != null) {
                // Created a callback for dismissing a message and preparing the next one
                lastInstance.dismissAndAwaitNextMessage(() -> {
                    lastInstance = null;
                    initInAppMessage(currentActivity, campaign);
                });
            } else
                initInAppMessage(currentActivity, campaign);
            return;
        }

        /* IMPORTANT
         * Loop the setup for in app message until curActivity is not null */
        if (Looper.myLooper() == null) {
            Looper.prepare();
            new Handler().postDelayed(() -> showHTMLString(campaign), IN_APP_MESSAGE_INIT_DELAY);
        }
    }


    private static void initInAppMessage(@NonNull final Activity currentActivity, Campaign campaign) {

        final WebViewManager webViewManager = new WebViewManager(campaign, currentActivity);
        lastInstance = webViewManager;
        Utils.runOnMainUIThread(() -> webViewManager.setupWebView(currentActivity));
    }

    @Override
    void available(final @NonNull Activity activity) {
        this.activity = activity;
//        showMessageView();
    }

    @Override
    void stopped(WeakReference<Activity> reference) {
        if (messageView != null)
            messageView.removeAllViews();
    }

    private void showHtmlMessageView() {
        if (webView == null)
            return;
        messageView = new InAppMessageView(webView, getDisplayLocation(), pageHeight, Double.parseDouble(campaign.getTimeDelay()));
        ActivityLifecycleHandler.setActivityAvailableListener(TAG + campaign.getCampaignId(), this);
        messageView.setWebView(webView);
        messageView.showView(activity);
        messageView.checkIfShouldDismiss();
    }

    private void showNativeMessageView() {
        messageView = new InAppMessageView(activity, getDisplayLocation(), pageHeight, Double.parseDouble(campaign.getTimeDelay()), campaign);
        ActivityLifecycleHandler.setActivityAvailableListener(TAG + campaign.getCampaignId(), this);
        messageView.showView(activity);
        messageView.checkIfShouldDismiss();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setupWebView(@NonNull final Activity currentActivity) {
        if (campaign.isNative())
            showNativeMessageView();
        else {
            webView = new WebView(currentActivity);
            webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.getSettings().setJavaScriptEnabled(true);

            blurryRenderingWebViewForKitKatWorkAround(webView);

            Utils.decorViewReady(currentActivity, () -> {
                setWebViewToMaxSize(currentActivity);
                webView.loadDataWithBaseURL(null, htmlData(), "text/html", "utf-8", null);
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    showHtmlMessageView();
                }
            });
        }
    }

    private String
    htmlData() {
        String styleCss = "<style>" + campaign.getCss() + "</style>";
        String javascript = "<style>" + campaign.getJavascript() + "</style>";
        String content = "<div>" + campaign.getContent() + "</div>";
        return content + javascript + styleCss;
    }

    private void blurryRenderingWebViewForKitKatWorkAround(@NonNull WebView webView) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void setWebViewToMaxSize(Activity activity) {
        if (webView != null)
            webView.layout(0, 0, getWebViewMaxSizeX(activity), getWebViewMaxSizeY(activity));
    }

    private static int getWebViewMaxSizeX(Activity activity) {
        return Utils.getWindowWidth(activity) - (MARGIN_PX_SIZE * 2);
    }

    private static int getWebViewMaxSizeY(Activity activity) {
        return Utils.getWindowHeight(activity) - (MARGIN_PX_SIZE * 2);
    }

    private void dismissAndAwaitNextMessage(@Nullable final InsightsGenericCallback callback) {
        if (messageView == null) {
            if (callback != null)
                callback.onComplete();
            return;
        }

        messageView.dismissAndAwaitNextMessage(() -> {
            messageView = null;
            if (callback != null)
                callback.onComplete();
        });
    }

    private @NonNull
    Position getDisplayLocation() {
        Position displayLocation = Position.FULL_SCREEN;
        if (campaign == null)
            return displayLocation;
        else {
            switch (campaign.getPositionId()) {
                case "top":
                    displayLocation = Position.TOP_BANNER;
                    pageHeight = Utils.getHeightScreen(activity) / 4;
                    break;
                case "bottom":
                    displayLocation = Position.BOTTOM_BANNER;
                    pageHeight = Utils.getHeightScreen(activity) / 4;
                    break;
                case "center":
                    displayLocation = Position.CENTER_MODAL;
                    pageHeight = Utils.getHeightScreen(activity) / 3;
                    break;
                case "full_screen":
                    displayLocation = Position.FULL_SCREEN;
                    pageHeight = Utils.getHeightScreen(activity);
                    break;
            }
        }
        return displayLocation;
    }
}
