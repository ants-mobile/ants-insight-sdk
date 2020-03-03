package adx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.PopupWindowCompat;

import ants.mobile.ants_insight.R;

import static adx.Utils.dpToPx;
import static adx.WebViewManager.Position.FULL_SCREEN;
import static adx.WebViewManager.Position.TOP_BANNER;

public class InAppView implements View.OnTouchListener {

    private static final int ACTIVITY_BACKGROUND_COLOR_EMPTY = Color.parseColor("#00000000");
    private static final int ACTIVITY_BACKGROUND_COLOR_FULL = Color.parseColor("#BB000000");

    private static final int IN_APP_BANNER_ANIMATION_DURATION_MS = 1000;
    private static final int IN_APP_CENTER_ANIMATION_DURATION_MS = 1000;
    private static final int IN_APP_BACKGROUND_ANIMATION_DURATION_MS = 400;

    private static final int ACTIVITY_FINISH_AFTER_DISMISS_DELAY_MS = 600;
    private static final int ACTIVITY_INIT_DELAY = 200;
    private static final int BUTTON_CLOSE_INIT_DELAY = 200;
    private static final int MARGIN_PX_SIZE = dpToPx(16);
    private static final int DRAG_THRESHOLD_PX_SIZE = dpToPx(4);
    private PopupWindow popupWindow;
    private WebViewManager.Position displayLocation;

    @NonNull
    WebViewManager.Position getDisplayPosition() {
        return displayLocation;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == webView.getId() && event.getAction() == MotionEvent.ACTION_DOWN)
            adListener.onAdxClick();
        return false;
    }

    interface InAppMessageViewListener {
        void onMessageWasShown();

        void onMessageWasDismissed();
    }

    private Activity currentActivity;
    private final Handler handler = new Handler();
    private int pageWidth;
    private int pageHeight;
    private double dismissDuration;
    private boolean hasBackground;
    private boolean shouldDismissWhenActive = false;
    private WebView webView;
    private RelativeLayout parentRelativeLayout;
    private DraggableRelativeLayout draggableRelativeLayout;
    private InAppMessageViewListener messageController;
    private Runnable scheduleDismissRunnable;
    private Campaign campaign;
    private AdListener adListener;

    @SuppressLint("ClickableViewAccessibility")
    InAppView(@NonNull WebView webView, @NonNull WebViewManager.Position displayLocation, int pageHeight,
              double dismissDuration, Campaign campaign) {
        this.webView = webView;
        this.pageHeight = pageHeight;
        this.pageWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        this.dismissDuration = Double.isNaN(dismissDuration) ? 0 : dismissDuration;
        this.campaign = campaign;
        this.displayLocation = displayLocation;
        this.hasBackground = !displayLocation.isBanner();
        webView.setOnTouchListener(this);

    }

    void setMessageController(InAppMessageViewListener messageController) {
        this.messageController = messageController;
    }


    void setWebView(WebView webView) {
        this.webView = webView;
    }

    void setAdsClickListener(AdListener adsClickListener) {
        this.adListener = adsClickListener;
    }

    void showView(Activity activity) {
        delayShowUntilAvailable(activity);
    }

    void checkIfShouldDismiss() {
        if (shouldDismissWhenActive) {
            shouldDismissWhenActive = false;
            finishAfterDelay(null);
        }
    }

    private void showInAppMessageView(Activity currentActivity) {
        /* IMPORTANT
         * The only place where currentActivity should be assigned to InAppMessageView */
        this.currentActivity = currentActivity;

        DraggableRelativeLayout.LayoutParams webViewLayoutParams = new DraggableRelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                pageHeight
        );
        webViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        LinearLayout.LayoutParams linearLayoutParams = hasBackground ? createParentLinearLayoutParams() : null;

        showDraggableView(
                webViewLayoutParams,
                linearLayoutParams,
                createDraggableLayoutParams(pageHeight)
        );
    }

    private int getDisplayYSize() {
        return Utils.getWindowHeight(currentActivity);
    }

    private LinearLayout.LayoutParams createParentLinearLayoutParams() {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(pageWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        switch (campaign.getPositionId()) {
            case 1:
                linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case 2:
                linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                break;
            case 3:
            case 4:
                linearLayoutParams.gravity = Gravity.CENTER;
                break;
        }

        return linearLayoutParams;
    }

    private DraggableRelativeLayout.Params createDraggableLayoutParams(int pageHeight) {
        DraggableRelativeLayout.Params draggableParams = new DraggableRelativeLayout.Params();
        draggableParams.maxXPos = MARGIN_PX_SIZE;
        draggableParams.maxYPos = MARGIN_PX_SIZE;

        draggableParams.messageHeight = pageHeight;
        draggableParams.height = getDisplayYSize();

        switch (pageHeight) {
            case 1:
                draggableParams.dragThresholdY = MARGIN_PX_SIZE - DRAG_THRESHOLD_PX_SIZE;
                break;
            case 2:
                draggableParams.posY = getDisplayYSize() - pageHeight;
                draggableParams.dragThresholdY = MARGIN_PX_SIZE + DRAG_THRESHOLD_PX_SIZE;
                break;
            case 3:
                draggableParams.messageHeight = pageHeight = getDisplayYSize() - (MARGIN_PX_SIZE * 2);
                // fall through for FULL_SCREEN since it shares similar params to CENTER_MODAL
            case 4:
                int y = (getDisplayYSize() / 2) - (pageHeight / 2);
                draggableParams.dragThresholdY = y + DRAG_THRESHOLD_PX_SIZE;
                draggableParams.maxYPos = y;
                draggableParams.posY = y;
                break;
        }

        draggableParams.dragDirection = displayLocation == TOP_BANNER ?
                DraggableRelativeLayout.Params.DRAGGABLE_DIRECTION_UP :
                DraggableRelativeLayout.Params.DRAGGABLE_DIRECTION_DOWN;

        return draggableParams;
    }

    private void showDraggableView(
            final RelativeLayout.LayoutParams relativeLayoutParams,
            final LinearLayout.LayoutParams linearLayoutParams,
            final DraggableRelativeLayout.Params webViewLayoutParams) {
        Utils.runOnMainUIThread(() -> {
            if (webView == null)
                return;

            webView.setLayoutParams(relativeLayoutParams);

            Context context = currentActivity.getApplicationContext();
            setUpDraggableLayout(context, linearLayoutParams, webViewLayoutParams);
            setUpParentLinearLayout(context);
            createPopupWindow(parentRelativeLayout);

            if (messageController != null) {
                animateInAppMessage(draggableRelativeLayout, parentRelativeLayout);
                messageController.onMessageWasShown();
            }

            startDismissTimerIfNeeded();
        });
    }

    /**
     * Create a new Android PopupWindow that draws over the current Activity
     *
     * @param parentRelativeLayout root layout to attach to the pop up window
     */
    private void createPopupWindow(@NonNull RelativeLayout parentRelativeLayout) {
        popupWindow = new PopupWindow(parentRelativeLayout, WindowManager.LayoutParams.MATCH_PARENT,
                hasBackground ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);

        int gravity = 0;
        if (!hasBackground) {
            switch (displayLocation) {
                case TOP_BANNER:
                    gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    break;
                case BOTTOM_BANNER:
                    gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                    break;
            }
        }

        // Using this instead of TYPE_APPLICATION_PANEL so the layout background does not get
        //  cut off in immersive mode.
        PopupWindowCompat.setWindowLayoutType(
                popupWindow,
                WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG
        );

        popupWindow.showAtLocation(currentActivity.getWindow().getDecorView().getRootView(), gravity, 0, 0);
    }

    private void setUpParentLinearLayout(Context context) {
        parentRelativeLayout = new RelativeLayout(context);
        parentRelativeLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        parentRelativeLayout.setClipChildren(false);
        parentRelativeLayout.setClipToPadding(false);
        parentRelativeLayout.addView(draggableRelativeLayout);

    }

    private void setUpDraggableLayout(final Context context,
                                      LinearLayout.LayoutParams linearLayoutParams,
                                      DraggableRelativeLayout.Params draggableParams) {
        draggableRelativeLayout = new DraggableRelativeLayout(context);
        if (linearLayoutParams != null)
            draggableRelativeLayout.setLayoutParams(linearLayoutParams);
        draggableRelativeLayout.setParams(draggableParams);
        draggableRelativeLayout.setListener(new DraggableRelativeLayout.DraggableListener() {
            @Override
            public void onDismiss() {
                finishAfterDelay(null);
            }

            @Override
            public void onDragStart() {
            }

            @Override
            public void onDragEnd() {

            }
        });

        if (webView.getParent() != null)
            ((ViewGroup) webView.getParent()).removeAllViews();

        CardView cardView = createCardView(context);
        cardView.addView(webView);
        delayShowBtnClose(cardView, context);
        draggableRelativeLayout.setPadding(MARGIN_PX_SIZE, MARGIN_PX_SIZE, MARGIN_PX_SIZE, MARGIN_PX_SIZE);
        draggableRelativeLayout.setClipChildren(false);
        draggableRelativeLayout.setClipToPadding(false);
        draggableRelativeLayout.addView(cardView);
    }

    private void delayShowBtnClose(CardView cardView, Context context) {
        if (cardView.getWidth() != 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.width = Utils.dpToPx(32);
            params.height = Utils.dpToPx(32);
            params.addRule(RelativeLayout.ALIGN_BASELINE, cardView.getId());
            params.setMargins(cardView.getWidth() - (draggableRelativeLayout.getWidth() - cardView.getWidth()), 0, 0, 0);

            ImageView imageView = new ImageView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                imageView.setImageDrawable(context.getDrawable(R.drawable.icon_close));

            imageView.setLayoutParams(params);
            imageView.setOnClickListener(v -> {
                WebViewManager.setIsShowingAds(false);
                cleanupViewsAfterDismiss();
                adListener.onCloseView();
            });
            cardView.addView(imageView);
            return;
        }
        new Handler().postDelayed(() -> delayShowBtnClose(cardView, context), BUTTON_CLOSE_INIT_DELAY);
    }

    /**
     * To show drop shadow on WebView
     * Layout container for WebView is needed
     */
    private CardView createCardView(Context context) {
        CardView cardView = new CardView(context);

        int height = displayLocation == FULL_SCREEN ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        RelativeLayout.LayoutParams cardViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        cardViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setRadius(dpToPx(8));
        cardView.setCardElevation(dpToPx(5));
        cardView.setClipChildren(false);
        cardView.setClipToPadding(false);
        cardView.setPreventCornerOverlap(false);
        return cardView;
    }

    /**
     * Schedule dismiss behavior, if IAM has a dismiss after X number of seconds timer.
     */
    private void startDismissTimerIfNeeded() {
        if (dismissDuration <= 0)
            return;

        if (scheduleDismissRunnable != null)
            return;

        scheduleDismissRunnable = () -> {
            if (currentActivity != null) {
//                    dismissAndAwaitNextMessage(null);
                scheduleDismissRunnable = null;
            } else {
                // For cases when the app is on background and the dismiss is triggered
                shouldDismissWhenActive = true;
            }
        };
        handler.postDelayed(scheduleDismissRunnable, (long) dismissDuration * 1_000);
    }

    // Do not add view until activity is ready
    private void delayShowUntilAvailable(final Activity currentActivity) {
        if (Utils.isActivityFullyReady(currentActivity) && parentRelativeLayout == null) {
            showInAppMessageView(currentActivity);
            return;
        }
        new Handler().postDelayed(() -> delayShowUntilAvailable(currentActivity), ACTIVITY_INIT_DELAY);
    }

    /**
     * Trigger the {@link #draggableRelativeLayout} dismiss animation
     */
    void dismissAndAwaitNextMessage(@Nullable WebViewManager.OneSignalGenericCallback callback) {
        if (draggableRelativeLayout == null) {
            dereferenceViews();
            if (callback != null)
                callback.onComplete();
            return;
        }

        draggableRelativeLayout.dismiss();
        finishAfterDelay(callback);
    }

    /**
     * Finishing on a timer as continueSettling does not return false
     * when using smoothSlideViewTo on Android 4.4
     */
    private void finishAfterDelay(final WebViewManager.OneSignalGenericCallback callback) {
        Utils.runOnMainThreadDelayed(() -> {
            if (hasBackground && parentRelativeLayout != null)
                animateAndDismissLayout(parentRelativeLayout, callback);
            else {
                cleanupViewsAfterDismiss();
                if (callback != null)
                    callback.onComplete();
            }
        }, ACTIVITY_FINISH_AFTER_DISMISS_DELAY_MS);
    }

    /**
     * IAM has been fully dismissed, remove all views and call the onMessageWasDismissed callback
     */
    private void cleanupViewsAfterDismiss() {
        removeAllViews();
        if (messageController != null)
            messageController.onMessageWasDismissed();
    }

    /**
     * Remove all views and dismiss PopupWindow
     */
    void removeAllViews() {
        if (scheduleDismissRunnable != null) {
            // Dismissed before the dismiss delay
            handler.removeCallbacks(scheduleDismissRunnable);
            scheduleDismissRunnable = null;
        }
        if (draggableRelativeLayout != null)
            draggableRelativeLayout.removeAllViews();

        if (popupWindow != null)
            popupWindow.dismiss();
        dereferenceViews();
    }

    /**
     * Cleans all layout references so this can be cleaned up in the next GC
     */
    private void dereferenceViews() {
        // Dereference so this can be cleaned up in the next GC
        parentRelativeLayout = null;
        draggableRelativeLayout = null;
        webView = null;
    }

    private void animateInAppMessage(View messageView, View backgroundView) {
        // Based on the location of the in app message apply and animation to match
        switch (displayLocation) {
            case TOP_BANNER:
                View topBannerMessageViewChild = ((ViewGroup) messageView).getChildAt(0);
                animateTop(topBannerMessageViewChild, webView.getHeight());
                break;
            case BOTTOM_BANNER:
                View bottomBannerMessageViewChild = ((ViewGroup) messageView).getChildAt(0);
                animateBottom(bottomBannerMessageViewChild, webView.getHeight());
                break;
            case CENTER_MODAL:
            case FULL_SCREEN:
                animateCenter(messageView, backgroundView);
                break;
        }
    }

    private void animateTop(View messageView, int height) {
        // Animate the message view from above the screen downward to the top
        InsightAnimate.animateViewByTranslation(
                messageView,
                -height - MARGIN_PX_SIZE,
                0f,
                IN_APP_BANNER_ANIMATION_DURATION_MS,
                new InsightBounceInterpolator(0.1, 8.0),
                null)
                .start();
    }

    private void animateBottom(View messageView, int height) {
        // Animate the message view from under the screen upward to the bottom
        InsightAnimate.animateViewByTranslation(
                messageView,
                height + MARGIN_PX_SIZE,
                0f,
                IN_APP_BANNER_ANIMATION_DURATION_MS,
                new InsightBounceInterpolator(0.1, 8.0),
                null)
                .start();
    }

    private void animateCenter(View messageView, final View backgroundView) {
        // Animate the message view by scale since it settles at the center of the screen
        Animation messageAnimation = InsightAnimate.animateViewSmallToLarge(
                messageView,
                IN_APP_CENTER_ANIMATION_DURATION_MS,
                new InsightBounceInterpolator(0.1, 8.0),
                null);

        // Animate background behind the message so it doesn't just show the dark transparency
        ValueAnimator backgroundAnimation = animateBackgroundColor(
                backgroundView,
                IN_APP_BACKGROUND_ANIMATION_DURATION_MS,
                ACTIVITY_BACKGROUND_COLOR_EMPTY,
                ACTIVITY_BACKGROUND_COLOR_FULL,
                null);

        messageAnimation.start();
        backgroundAnimation.start();
    }

    private void animateAndDismissLayout(View backgroundView, final WebViewManager.OneSignalGenericCallback callback) {
        Animator.AnimatorListener animCallback = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cleanupViewsAfterDismiss();
                if (callback != null)
                    callback.onComplete();
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
