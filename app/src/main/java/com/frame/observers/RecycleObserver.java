package com.frame.observers;

import io.reactivex.observers.ResourceObserver;

public class RecycleObserver<T> extends ResourceObserver<T> {

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable e) {
        if (!this.isDisposed()) this.dispose();
    }

    @Override
    public void onComplete() {
        if (!this.isDisposed()) this.dispose();
    }
}
