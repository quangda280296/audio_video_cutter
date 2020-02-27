package com.vietmobi.mobile.audiovideocutter.data.remote.api;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable disposable) {
        addDisposableManager(disposable);
    }

    @Override
    public void onNext(T t) {
        System.out.println("BaseObserver1" + t);
        if (t != null) {
            onResponse(t);
        } else {
            onFailure();
        }
    }

    @Override
    public void onError(Throwable e) {
        onFailure();
    }

    @Override
    public void onComplete() {

    }

    protected abstract void onResponse(T t);

    protected abstract void onFailure();

    protected void onFailure(String error) {

    }

    protected abstract void addDisposableManager(Disposable disposable);

}