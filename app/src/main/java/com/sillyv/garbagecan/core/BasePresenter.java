package com.sillyv.garbagecan.core;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public abstract class BasePresenter
        implements BaseContract.Presenter {

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void registerDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    public void detach() {
        disposables.clear();
        disposables.dispose();
    }

}
