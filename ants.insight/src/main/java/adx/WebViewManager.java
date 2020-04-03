package adx;

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

import static adx.Utils.dpToPx;

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
    private ISWebView webView;
    @Nullable
    private InAppMessageView messageView;

    @Nullable
    private static WebViewManager lastInstance = null;

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

    private void showMessageView() {
        if (webView == null)
            return;
        messageView = new InAppMessageView(webView, getDisplayLocation(), pageHeight, Double.parseDouble(campaign.getTimeDelay()), campaign);
//        ActivityLifecycleHandler.setActivityAvailableListener(TAG + campaign.getCampaignId(), this);
        messageView.setWebView(webView);
        messageView.showView(activity);
        messageView.checkIfShouldDismiss();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setupWebView(@NonNull final Activity currentActivity) {
        webView = new ISWebView(currentActivity);
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
                showMessageView();
            }
        });

    }

    private String htmlData() {
        String styleCss = "<style>" + campaign.getCss() + "</style>";
        String javascript = "<style>" + campaign.getJavascript() + "</style>";
        String content = "<div>" + campaign.getContent() + "</div>";

        return content + javascript + styleCss;

//        return "\n" +
//                "<style>#bc-5e0d6670ce792bd79d3719ea h1 { text-align: center;font-family: Tahoma, Arial, sans-serif;color: #06D85F;margin: 80px 0; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .box { width: 40%;margin: 0 auto;background: rgba(255,255,255,0.2);padding: 35px;border: 2px solid #fff;border-radius: 20px/50px;background-clip: padding-box;text-align: center; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .button { font-size: 1em;padding: 10px;color: #fff;border: 2px solid #06D85F;border-radius: 20px/50px;text-decoration: none;cursor: pointer;transition: all 0.3s ease-out; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .button:hover { background: #06D85F; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .overlay { position: fixed;top: 0;bottom: 0;left: 0;right: 0;background: rgba(0, 0, 0, 0.7);transition: opacity 500ms;visibility: visible;opacity: 1;z-index: 1111; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .overlay:target { visibility: visible;opacity: 1; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .popup { margin: 70px auto;padding: 20px;background: #fff;border-radius: 5px;width: 30%;position: relative;transition: all 5s ease-in-out; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .popup h2 { margin-top: 0;color: #333;font-family: Tahoma, Arial, sans-serif; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .popup .close { position: absolute;top: 20px;right: 30px;transition: all 200ms;font-size: 30px;font-weight: bold;text-decoration: none;color: #333; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .popup .close:hover { color: #06D85F; }\n" +
//                "#bc-5e0d6670ce792bd79d3719ea .popup .content { max-height: 30%;overflow: auto; }\n" +
//                "@media screen and (max-width: 700px) { #bc-5e0d6670ce792bd79d3719ea .box { width: 70%; } #bc-5e0d6670ce792bd79d3719ea .popup { width: 70%; }  }\n" +
//                "</style>\n" +
//                "<script src='/assets/js/cdp_analytics.js'></script>\n" +
//                "<div id='bc-5e0d6670ce792bd79d3719ea'>\n" +
//                "  <div id=\"popup1\" class=\"overlay\"><div class=\"popup\"><h2 style=\"color: #006eaa; text-align: center;\">You just got a gift!</h2><a class=\"close\">×</a><p class=\"content\" style=\"text-align: center;\"><span style=\"font-size: 14pt;\">Use coupon code for 20% discount: </span></p><p class=\"content\" style=\"text-align: center;\"><span style=\"font-size: 14pt;\"><strong>NCDIS20</strong></span></p><p class=\"content\" style=\"text-align: center;\"><span style=\"font-size: 14pt;\">Please copy and <a href='//a.cdp.asia/event?portal_id=564244922&prop_id=564244924&uid=877535&aid=877535&ea=click&en=click_tracking&ec=advertising&items=[]&dims={\"mg_campaign\":{\"id\":\"5e0d6ad5898f47d7768a7f9c\"}}&context={\"campaign\":{\"source_prop_id\":\"564244923\",\"name\":\"Popup: Coupon 20% - New Customer when view Cart List\",\"source\":\"delivery\"}}&resp_type=redirect&redirect_url=https%3A%2F%2Fmagento.antsomi.com%2Findex.php%2Fcheckout%2F%23shipping'>use it now</a>!</span></p><div class=\"content\"><img id=\"1579236899302_5e213e0c55ccc9327e222647\" style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"http://app.cdp.asia/hub/assets/uploads/561369401/1579236876370_20.jpg\" alt=\"20.jpg\" width=\"324\" height=\"324\"></div></div></div> <iframe class='creative_link' width='0px' height='0px' style='display:none; visibility:hidden' src='//a.cdp.asia/event?portal_id=564244922&prop_id=564244924&uid=877535&aid=877535&ea=impression&en=impression_tracking&ec=advertising&items=[]&dims={\"mg_campaign\":{\"id\":\"5e0d6ad5898f47d7768a7f9c\"}}&context={\"campaign\":{\"source_prop_id\":\"564244923\",\"name\":\"Popup: Coupon 20% - New Customer when view Cart List\",\"source\":\"delivery\"}}'></iframe>\n" +
//                "</div>\n" +
//                "<script>setTimeout(function(){\n" +
//                "jQuery('.popup .close').on('click', function(){jQuery(this).parent().parent().parent().parent().hide()})\n" +
//                "},2000);</script>";
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
                    pageHeight = Utils.getHeightScreen(activity) / 4;
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
