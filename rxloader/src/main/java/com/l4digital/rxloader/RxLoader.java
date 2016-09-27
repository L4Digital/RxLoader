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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class RxLoader<T> extends Loader<T> {

    private final Flowable<T> mFlowable;
    private DisposableSubscriber<T> mSubscriber;
    private Throwable mThrowable;
    private boolean mComplete;
    private T mDataCache;

    public RxLoader(Context context, Observable<T> observable) {
        this(context, observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public RxLoader(Context context, Flowable<T> flowable) {
        super(context);
        mFlowable = flowable;
    }

    @Override
    public void deliverResult(T data) {
        mDataCache = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    public Throwable getError() {
        return mThrowable;
    }

    public boolean hasComplete() {
        return mComplete;
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
        dispose();
    }

    @Override
    protected boolean onCancelLoad() {
        super.onCancelLoad();
        dispose();
        return mSubscriber == null || mSubscriber.isDisposed();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        dispose();
        subscribe();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mDataCache = null;
        mThrowable = null;
        mComplete = false;
    }

    private void subscribe() {
        mThrowable = null;
        mComplete = false;
        mFlowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSubscriber());
    }

    private void dispose() {
        if (mSubscriber != null && !mSubscriber.isDisposed()) {
            mSubscriber.dispose();
        }
    }

    private DisposableSubscriber<T> createSubscriber() {
        if (mSubscriber == null || mSubscriber.isDisposed()) {
            mSubscriber = new DisposableSubscriber<T>() {

                @Override
                public void onNext(T t) {
                    deliverResult(t);
                }

                @Override
                public void onError(Throwable t) {
                    mThrowable = t;
                    deliverResult(null);
                    dispose();
                }

                @Override
                public void onComplete() {
                    mComplete = true;
                    deliverResult(null);
                    dispose();
                }
            };
        }

        return mSubscriber;
    }
}
