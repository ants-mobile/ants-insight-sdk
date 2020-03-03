package ants.mobile.ants_insight;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CustomApiCallBack<R> implements Observer<R> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(R r) {
        Log.e("success", r.toString());
    }

    @Override
    public void onError(Throwable e) {
        Log.e("InsightErrorMessage", e.getMessage());
    }

    @Override
    public void onComplete() {

    }
}
