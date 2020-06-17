package ants.mobile.ants_insight;
import android.util.Log;

import androidx.multidex.BuildConfig;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CustomApiCallBack<R> implements Observer<R> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(R r) {
        if (BuildConfig.DEBUG) {
            Log.e("INSIGHT_SUCCESS", r.toString());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (BuildConfig.DEBUG)
            Log.e("INSIGHT_ERROR", e.getMessage());
    }

    @Override
    public void onComplete() {
    }
}
