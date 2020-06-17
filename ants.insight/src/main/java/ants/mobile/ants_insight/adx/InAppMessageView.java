package ants.mobile.ants_insight.adx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import ants.mobile.ants_insight.R;

import static ants.mobile.ants_insight.adx.Utils.dpToPx;

public class InAppMessageView implements View.OnTouchListener {

    private static final int ACTIVITY_BACKGROUND_COLOR_EMPTY = Color.parseColor("#00000000");
    private static final int ACTIVITY_BACKGROUND_COLOR_FULL = Color.parseColor("#BB000000");

    private static final int IN_APP_BANNER_ANIMATION_DURATION_MS = 100;
    private static final int IN_APP_CENTER_ANIMATION_DURATION_MS = 10;
    private static final int IN_APP_BACKGROUND_ANIMATION_DURATION_MS = 100;

    private static final int ACTIVITY_FINISH_AFTER_DISMISS_DELAY_MS = 10;
    private static final int ACTIVITY_INIT_DELAY = 200;
    private static final int BUTTON_CLOSE_INIT_DELAY = 200;
    private static final int MARGIN_PX_SIZE = dpToPx(16);
    private static final int DRAG_THRESHOLD_PX_SIZE = dpToPx(4);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
    private RelativeLayout parentRelativeLayout;
    private DraggableRelativeLayout draggableRelativeLayout;
    private InAppMessageViewListener messageController;
    private Runnable scheduleDismissRunnable;
    private AlertDialog mDialog;
    private WebViewManager.Position displayLocation;
    private View mView;
    private Button btnClickMe;

    InAppMessageView(@NonNull WebView webView, @NonNull WebViewManager.Position displayLocation,
                     int pageHeight, double dismissDuration) {
        this.mView = webView;
        this.displayLocation = displayLocation;
        this.pageHeight = pageHeight;
        this.pageWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        this.dismissDuration = Double.isNaN(dismissDuration) ? 0 : dismissDuration;
        this.hasBackground = !displayLocation.isBanner();
    }

    InAppMessageView(Activity activity, @NonNull WebViewManager.Position displayLocation,
                     int pageHeight, double dismissDuration, Campaign campaign) {
        this.displayLocation = displayLocation;
        this.pageHeight = pageHeight;
        this.pageWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        this.dismissDuration = Double.isNaN(dismissDuration) ? 0 : dismissDuration;
        this.hasBackground = !displayLocation.isBanner();
        initView(activity);
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        switch (displayLocation) {
            case FULL_SCREEN:
                this.mView = inflater.inflate(R.layout.adx_template_center, null);
                break;
            case TOP_BANNER:
            case BOTTOM_BANNER:
            case CENTER_MODAL:
                this.mView = inflater.inflate(R.layout.adx_template_top_bottom, null);
                break;
            default:
                this.mView = inflater.inflate(R.layout.adx_template_center, null);
                break;
        }

        mView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mView.setVerticalScrollBarEnabled(false);
        mView.setHorizontalScrollBarEnabled(false);

        btnClickMe = mView.findViewById(R.id.btn_click_me);
        RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        RelativeLayout mRootView = mView.findViewById(R.id.view_root);
        mRootView.setLayoutParams(layout_description);
        mRootView.setOnClickListener(v -> {
            //todo: not thing
            btnClickMe.setText("Tèo");
        });
    }

    void setMessageController(InAppMessageViewListener messageController) {
        this.messageController = messageController;
    }

    void setWebView(WebView webView) {
        this.mView = webView;
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

    void showInAppMessageView(Activity currentActivity) {
        /* IMPORTANT
         * The only place where currentActivity should be assigned to InAppMessageView */
        this.currentActivity = currentActivity;

        DraggableRelativeLayout.LayoutParams layoutParams = new DraggableRelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                pageHeight
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        LinearLayout.LayoutParams linearLayoutParams = hasBackground ? createParentLinearLayoutParams() : null;

        showDraggableView(
                displayLocation,
                layoutParams,
                linearLayoutParams,
                createDraggableLayoutParams(pageHeight, displayLocation)
        );
    }

    private int getDisplayYSize() {
        return Utils.getWindowHeight(currentActivity);
    }

    private LinearLayout.LayoutParams createParentLinearLayoutParams() {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(pageWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        switch (displayLocation) {
            case TOP_BANNER:
                linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case BOTTOM_BANNER:
                linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                break;
            case CENTER_MODAL:
            case FULL_SCREEN:
                linearLayoutParams.gravity = Gravity.CENTER;
        }

        return linearLayoutParams;
    }

    private DraggableRelativeLayout.Params createDraggableLayoutParams(int pageHeight, WebViewManager.Position displayLocation) {
        DraggableRelativeLayout.Params draggableParams = new DraggableRelativeLayout.Params();
        draggableParams.maxXPos = MARGIN_PX_SIZE;
        draggableParams.maxYPos = MARGIN_PX_SIZE;

        draggableParams.messageHeight = pageHeight;
        draggableParams.height = getDisplayYSize();

        switch (displayLocation) {
            case TOP_BANNER:
                draggableParams.dragThresholdY = MARGIN_PX_SIZE - DRAG_THRESHOLD_PX_SIZE;
                break;
            case BOTTOM_BANNER:
                draggableParams.posY = getDisplayYSize() - pageHeight;
                draggableParams.dragThresholdY = MARGIN_PX_SIZE + DRAG_THRESHOLD_PX_SIZE;
                break;
            case FULL_SCREEN:
                draggableParams.messageHeight = pageHeight = getDisplayYSize() - (MARGIN_PX_SIZE * 2);
                // fall through for FULL_SCREEN since it shares similar params to CENTER_MODAL
            case CENTER_MODAL:
                int y = (getDisplayYSize() / 2) - (pageHeight / 2);
                draggableParams.dragThresholdY = y + DRAG_THRESHOLD_PX_SIZE;
                draggableParams.maxYPos = y;
                draggableParams.posY = y;
                break;
        }

        draggableParams.dragDirection = displayLocation == WebViewManager.Position.TOP_BANNER ?
                DraggableRelativeLayout.Params.DRAGGABLE_DIRECTION_UP :
                DraggableRelativeLayout.Params.DRAGGABLE_DIRECTION_DOWN;

        return draggableParams;
    }

    private void showDraggableView(final WebViewManager.Position displayLocation,
                                   final RelativeLayout.LayoutParams relativeLayoutParams,
                                   final LinearLayout.LayoutParams linearLayoutParams,
                                   final DraggableRelativeLayout.Params webViewLayoutParams) {
        Utils.runOnMainUIThread(() -> {
            if (mView == null)
                return;

            mView.setLayoutParams(relativeLayoutParams);

            Context context = currentActivity.getApplicationContext();
            setUpDraggableLayout(context, linearLayoutParams, webViewLayoutParams);
            setUpParentLinearLayout(context);
            createAlertDialog(currentActivity, parentRelativeLayout);

            if (messageController != null) {
                animateInAppMessage(displayLocation, draggableRelativeLayout, parentRelativeLayout);
                messageController.onMessageWasShown();
            }

            startDismissTimerIfNeeded();
        });
    }

    /**
     * Create a new Android AlertDialog that draws over the current Activity
     *
     * @param parentRelativeLayout root layout to attach to the alert dialog
     */

    private void createAlertDialog(final Activity activity, @NonNull RelativeLayout parentRelativeLayout) {
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

        mDialog = new AlertDialog.Builder(activity).setCancelable(true).create();
        mDialog.show();
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        if (lp != null) {

            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = pageHeight;
            lp.gravity = gravity;
            mDialog.getWindow().setAttributes(lp);
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().setBackgroundDrawableResource(R.color.TRANSPARENT);
        mDialog.setContentView(parentRelativeLayout);
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

        if (mView.getParent() != null)
            ((ViewGroup) mView.getParent()).removeAllViews();

        CardView cardView = createCardView(context);
        cardView.addView(mView);

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
                WebViewManager.isShowingAds = false;
                cleanupViewsAfterDismiss();
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

        int height = displayLocation == WebViewManager.Position.FULL_SCREEN ?
                ViewGroup.LayoutParams.MATCH_PARENT :
                ViewGroup.LayoutParams.WRAP_CONTENT;
        RelativeLayout.LayoutParams cardViewLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        );
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
                dismissAndAwaitNextMessage(null);
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
    void dismissAndAwaitNextMessage(@Nullable WebViewManager.InsightsGenericCallback callback) {
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
    private void finishAfterDelay(final WebViewManager.InsightsGenericCallback callback) {
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

        if (mDialog != null) {
            mDialog.dismiss();
            WebViewManager.isShowingAds = false;
        }
        dereferenceViews();
    }

    /**
     * Cleans all layout references so this can be cleaned up in the next GC
     */
    private void dereferenceViews() {
        // Dereference so this can be cleaned up in the next GC
        parentRelativeLayout = null;
        draggableRelativeLayout = null;
        mView = null;
    }

    private void animateInAppMessage(WebViewManager.Position displayLocation, View messageView, View backgroundView) {
        // Based on the location of the in app message apply and animation to match
        switch (displayLocation) {
            case TOP_BANNER:
                View topBannerMessageViewChild = ((ViewGroup) messageView).getChildAt(0);
                animateTop(topBannerMessageViewChild, mView.getHeight());
                break;
            case BOTTOM_BANNER:
                View bottomBannerMessageViewChild = ((ViewGroup) messageView).getChildAt(0);
                animateBottom(bottomBannerMessageViewChild, mView.getHeight());
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

    private void animateAndDismissLayout(View backgroundView, final WebViewManager.InsightsGenericCallback callback) {
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
