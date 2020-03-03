package adx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.widget.PopupWindowCompat;

import ants.mobile.ants_insight.R;

public class InAppMessageView implements View.OnClickListener {
    private static final int ACTIVITY_INIT_DELAY = 200;
    private WebView webView;
    private Activity activity;
    private Campaign campaign;
    private View popupView;
    private PopupWindow popupWindow;
    private AdListener adListener;
    private WebViewListener webViewListener;

    @SuppressLint("ClickableViewAccessibility")
    public InAppMessageView(@NonNull Activity activity, @NonNull Campaign campaign) {
        this.activity = activity;
        this.campaign = campaign;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.item_popup_windown_custom, null);

        ImageView btnClose = popupView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);

        webView = popupView.findViewById(R.id.web_view);
        webView.setOnClickListener(this);
//        webView.setOnTouchListener(new OnSwipeTouchListener(activity) {
//            public void onSwipeTop() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    popupView.animate()
//                            .translationY(-popupView.getHeight())
//                            .alpha(0.0f)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                    popupWindow.dismiss();
//                                }
//                            });
//                }
//            }
//
//            public void onSwipeRight() {
//                popupView.animate()
//                        .translationX(popupView.getWidth())
//                        .alpha(0.0f)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                popupWindow.dismiss();
//                            }
//                        });
//            }
//
//            public void onSwipeLeft() {
//                popupView.animate()
//                        .translationX(-popupView.getWidth())
//                        .alpha(0.0f)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                popupWindow.dismiss();
//                            }
//                        });
//            }
//
//            public void onSwipeBottom() {
//                popupView.animate()
//                        .translationY(popupView.getHeight())
//                        .alpha(0.0f)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                popupWindow.dismiss();
//                            }
//                        });
//            }
//        });
        setupWebView();
    }

    public void setWebViewListener(WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }

    public void showView() {
        delayShowUntilAvailable();
    }

    private void delayShowUntilAvailable() {
        if (Utils.isActivityFullyReady(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showInAppMessageView();
            }
            return;
        }
        new Handler().postDelayed(this::delayShowUntilAvailable, ACTIVITY_INIT_DELAY);
    }

    private void showInAppMessageView() {
        RelativeLayout.LayoutParams webViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showDraggableView(webViewLayoutParams);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showDraggableView(final RelativeLayout.LayoutParams relativeLayoutParams) {
        Utils.runOnMainUIThread(() -> {
            if (webView == null)
                return;
            animateInAppMessage(popupView);
            webView.setLayoutParams(relativeLayoutParams);
            createPopupWindow();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createPopupWindow() {
        popupWindow = new PopupWindow(popupView, getPopupViewWidth(), getPopupViewHeight());
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }

        int gravity = 0;
        switch (campaign.getPositionId()) {
            case 1:
                gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case 2:
                gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                break;
            case 3:
            case 4:
                gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
                break;
        }

        PopupWindowCompat.setWindowLayoutType(popupWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);

        popupWindow.showAtLocation(activity.getWindow().getDecorView(), gravity, 0, 0);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setupWebView() {
        setWebViewToMaxSize();
        if (webView.getParent() != null)
            webView.removeAllViews();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
//                adListener.onLoadAd();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                webViewListener.onPageLoadingSuccess();
            }
        });

        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(htmlData(), "text/html", "charset=UTF-8");
    }

    private void animateInAppMessage(View messageView) {
        // Based on the location of the in app message apply and animation to match

        switch (campaign.getPositionId()) {
            case 1:
                View topBannerMessageViewChild = ((ViewGroup) messageView).getChildAt(0);
                animateTop(topBannerMessageViewChild, webView.getHeight());
                break;
            case 2:
                View bottomBannerMessageViewChild = ((ViewGroup) messageView).getChildAt(0);
                animateBottom(bottomBannerMessageViewChild, webView.getHeight());
                break;
            case 3:
            case 4:
                animateCenter(messageView);
                break;
        }
    }

    private void setWebViewToMaxSize() {
        webView.layout(0, 0, getWebViewMaxSizeX(activity), getWebViewMaxSizeY(activity));
    }


    private static int getWebViewMaxSizeX(Activity activity) {
        return Utils.getWidthScreen(activity) - Utils.dpToPx(48);
    }

    private static int getWebViewMaxSizeY(Activity activity) {
        return Utils.getHeightScreen(activity) - Utils.dpToPx(48);
    }

    private String htmlData() {
//        String styleCss = "<style>" + campaign.getCss() + "</style>";
//        String javascript = "<style>" + campaign.getJavascript() + "</style>";
//        String content = "<div>" + campaign.getContent() + "</div>";
//
//        return content + javascript + styleCss;

        return "\n" +
                "<style>#bc-5e0d6670ce792bd79d3719ea h1 { text-align: center;font-family: Tahoma, Arial, sans-serif;color: #06D85F;margin: 80px 0; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .box { width: 40%;margin: 0 auto;background: rgba(255,255,255,0.2);padding: 35px;border: 2px solid #fff;border-radius: 20px/50px;background-clip: padding-box;text-align: center; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .button { font-size: 1em;padding: 10px;color: #fff;border: 2px solid #06D85F;border-radius: 20px/50px;text-decoration: none;cursor: pointer;transition: all 0.3s ease-out; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .button:hover { background: #06D85F; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .overlay { position: fixed;top: 0;bottom: 0;left: 0;right: 0;background: rgba(0, 0, 0, 0.7);transition: opacity 500ms;visibility: visible;opacity: 1;z-index: 1111; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .overlay:target { visibility: visible;opacity: 1; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .popup { margin: 70px auto;padding: 20px;background: #fff;border-radius: 5px;width: 30%;position: relative;transition: all 5s ease-in-out; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .popup h2 { margin-top: 0;color: #333;font-family: Tahoma, Arial, sans-serif; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .popup .close { position: absolute;top: 20px;right: 30px;transition: all 200ms;font-size: 30px;font-weight: bold;text-decoration: none;color: #333; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .popup .close:hover { color: #06D85F; }\n" +
                "#bc-5e0d6670ce792bd79d3719ea .popup .content { max-height: 30%;overflow: auto; }\n" +
                "@media screen and (max-width: 700px) { #bc-5e0d6670ce792bd79d3719ea .box { width: 70%; } #bc-5e0d6670ce792bd79d3719ea .popup { width: 70%; }  }\n" +
                "</style>\n" +
                "<script src='/assets/js/cdp_analytics.js'></script>\n" +
                "<div id='bc-5e0d6670ce792bd79d3719ea'>\n" +
                "  <div id=\"popup1\" class=\"overlay\"><div class=\"popup\"><h2 style=\"color: #006eaa; text-align: center;\">You just got a gift!</h2><a class=\"close\">×</a><p class=\"content\" style=\"text-align: center;\"><span style=\"font-size: 14pt;\">Use coupon code for 20% discount: </span></p><p class=\"content\" style=\"text-align: center;\"><span style=\"font-size: 14pt;\"><strong>NCDIS20</strong></span></p><p class=\"content\" style=\"text-align: center;\"><span style=\"font-size: 14pt;\">Please copy and <a href='//a.cdp.asia/event?portal_id=564244922&prop_id=564244924&uid=877535&aid=877535&ea=click&en=click_tracking&ec=advertising&items=[]&dims={\"mg_campaign\":{\"id\":\"5e0d6ad5898f47d7768a7f9c\"}}&context={\"campaign\":{\"source_prop_id\":\"564244923\",\"name\":\"Popup: Coupon 20% - New Customer when view Cart List\",\"source\":\"delivery\"}}&resp_type=redirect&redirect_url=https%3A%2F%2Fmagento.antsomi.com%2Findex.php%2Fcheckout%2F%23shipping'>use it now</a>!</span></p><div class=\"content\"><img id=\"1579236899302_5e213e0c55ccc9327e222647\" style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"http://app.cdp.asia/hub/assets/uploads/561369401/1579236876370_20.jpg\" alt=\"20.jpg\" width=\"324\" height=\"324\"></div></div></div> <iframe class='creative_link' width='0px' height='0px' style='display:none; visibility:hidden' src='//a.cdp.asia/event?portal_id=564244922&prop_id=564244924&uid=877535&aid=877535&ea=impression&en=impression_tracking&ec=advertising&items=[]&dims={\"mg_campaign\":{\"id\":\"5e0d6ad5898f47d7768a7f9c\"}}&context={\"campaign\":{\"source_prop_id\":\"564244923\",\"name\":\"Popup: Coupon 20% - New Customer when view Cart List\",\"source\":\"delivery\"}}'></iframe>\n" +
                "</div>\n" +
                "<script>setTimeout(function(){\n" +
                "jQuery('.popup .close').on('click', function(){jQuery(this).parent().parent().parent().parent().hide()})\n" +
                "},2000);</script>";
    }

    private int getPopupViewHeight() {
        int height = 0;
        switch (campaign.getPositionId()) {
            case 1:
            case 2:
            case 4:
                height = Utils.getHeightScreen(activity.getApplicationContext()) / 3;
                break;
            case 3:
                height = WindowManager.LayoutParams.MATCH_PARENT;
                break;
        }
        return height;
    }

    private int getPopupViewWidth() {
        return Utils.getWidthScreen(activity) - 64;
    }

    @Override
    public void onClick(View v) {
        if (popupView == null)
            return;
        if (v.getId() == R.id.btn_close) {
            popupWindow.dismiss();
            animateAndDismissLayout(popupView);
        }
        if (v.getId() == R.id.web_view)
            adListener.onAdxClick();
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, activity.getResources().getDisplayMetrics());
        return actionBarHeight + 20;
    }

    private void animateTop(View messageView, int height) {
        // Animate the message view from above the screen downward to the top
        InsightAnimate.animateViewByTranslation(
                messageView,
                -height - Utils.dpToPx(24),
                0f,
                IN_APP_BANNER_ANIMATION_DURATION_MS,
                new InsightBounceInterpolator(0.1, 8.0),
                null)
                .start();
    }

    private void animateCenter(View messageView) {
        // Animate the message view by scale since it settles at the center of the screen
        Animation messageAnimation = InsightAnimate.animateViewSmallToLarge(
                messageView,
                IN_APP_CENTER_ANIMATION_DURATION_MS,
                new InsightBounceInterpolator(0.1, 8.0),
                null);

        // Animate background behind the message so it doesn't just show the dark transparency
//        ValueAnimator backgroundAnimation = animateBackgroundColor(
//                backgroundView,
//                IN_APP_BACKGROUND_ANIMATION_DURATION_MS,
//                ACTIVITY_BACKGROUND_COLOR_EMPTY,
//                ACTIVITY_BACKGROUND_COLOR_FULL,
//                null);

        messageAnimation.start();
//        backgroundAnimation.start();
    }

    private void animateBottom(View messageView, int height) {
        // Animate the message view from under the screen upward to the bottom
        InsightAnimate.animateViewByTranslation(
                messageView,
                height + Utils.dpToPx(24),
                0f,
                IN_APP_BANNER_ANIMATION_DURATION_MS,
                new InsightBounceInterpolator(0.1, 8.0),
                null)
                .start();
    }

    private static final int IN_APP_BANNER_ANIMATION_DURATION_MS = 1000;
    private static final int IN_APP_CENTER_ANIMATION_DURATION_MS = 1000;
    private static final int IN_APP_BACKGROUND_ANIMATION_DURATION_MS = 400;
    private static final int ACTIVITY_BACKGROUND_COLOR_EMPTY = Color.parseColor("#00000000");
    private static final int ACTIVITY_BACKGROUND_COLOR_FULL = Color.parseColor("#BB000000");

    private void animateAndDismissLayout(View backgroundView) {
        Animator.AnimatorListener animCallback = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        };

        // Animate background behind the message so it hides before being removed from the view
        animateBackgroundColor(
                backgroundView,
                IN_APP_BACKGROUND_ANIMATION_DURATION_MS,
                ACTIVITY_BACKGROUND_COLOR_FULL,
                ACTIVITY_BACKGROUND_COLOR_EMPTY,
                animCallback)
                .start();
    }

    private ValueAnimator animateBackgroundColor(View backgroundView, int duration, int startColor, int endColor, Animator.AnimatorListener animCallback) {
        return InsightAnimate.animateViewColor(
                backgroundView,
                duration,
                startColor,
                endColor,
                animCallback);
    }

}

