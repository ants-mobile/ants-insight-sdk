package ants.mobile.insight;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.core.widget.PopupWindowCompat;

import ants.mobile.insights.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class InAppView extends PopupWindow {

    private Context mContext;
    private Activity activity;

    public InAppView(Context context) {
        super(context);
        this.mContext = context;
    }

    public InAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InAppView(Activity activity) {
        this.activity = activity;
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.item_popup_windown_custom, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, false);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);

        int gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;


        PopupWindowCompat.setWindowLayoutType(
                popupWindow,
                WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG
        );
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), gravity, 0, 0
        );
    }
}
