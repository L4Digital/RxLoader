/*
 * Copyright 2016 L4 Digital LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.l4digital.rxloader;

import android.content.Context;
import android.content.Loader;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxLoader<T> extends Loader<T> implements Observer<T> {

    private final Observable<T> mObservable;
    private Subscription mSubscription;
    private Throwable mError;
    private boolean mCompleted;
    private T mDataCache;

    public RxLoader(Context context, Observable<T> observable) {
        super(context);

        if (observable == null) {
            throw new NullPointerException("observable cannot be null");
        }

        mObservable = observable;
    }

    @Override
    public void deliverResult(T data) {
        mDataCache = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    public void onCompleted() {
        mCompleted = true;
        deliverResult(null);
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        mError = e;
        deliverResult(null);
        unsubscribe();
    }

    @Override
    public void onNext(T t) {
        deliverResult(t);
    }

    public Throwable getError() {
        return mError;
    }

    public boolean hasCompleted() {
        return mCompleted;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mDataCache != null) {
            // send cached data immediately
            deliverResult(mDataCache);
        }

        if (takeContentChanged() || mDataCache == null) {
            forceLoad();
        } else {
            subscribe();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        unsubscribe();
    }

    @Override
    protected boolean onCancelLoad() {
        super.onCancelLoad();
        unsubscribe();
        return mSubscription == null || mSubscription.isUnsubscribed();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        unsubscribe();
        subscribe();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mDataCache = null;
        mError = null;
        mCompleted = false;
    }

    private void subscribe() {
        mError = null;
        mCompleted = false;
        mSubscription = mObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
