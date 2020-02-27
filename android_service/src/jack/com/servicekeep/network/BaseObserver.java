package jack.com.servicekeep.network;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable disposable) {
        addDisposableManager(disposable);
    }

    @Override
    public void onNext(T t) {
        if (t != null) {
            onResponse(t);
        } else {
            onFailure();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        onFailure();
    }

    @Override
    public void onComplete() {

    }

    protected abstract void onResponse(T t);

    protected abstract void onFailure();

    protected abstract void addDisposableManager(Disposable disposable);

}
