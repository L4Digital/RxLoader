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

package com.l4digital.support.rxloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import com.l4digital.reactivex.LoaderSubscriber;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxLoader<T> extends Loader<T> {

    private final Flowable<T> mFlowable;
    private LoaderSubscriber<T> mSubscriber;
    private T mDataCache;

    public RxLoader(Context context, @NonNull Observable<T> observable) {
        this(context, observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public RxLoader(Context context, @NonNull Flowable<T> flowable) {
        super(context);

        //noinspection ConstantConditions
        if (flowable == null) {
            throw new NullPointerException("flowable cannot be null");
        }

        mFlowable = flowable;
    }

    @Override
    public void deliverResult(T data) {
        if (data != null) {
            mDataCache = data;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    public Throwable getError() {
        return mSubscriber != null ? mSubscriber.getError() : null;
    }

    public boolean hasComplete() {
        return mSubscriber != null && mSubscriber.isComplete();
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

        if (mSubscriber != null) {
            mSubscriber.reset();
        }
    }

    private void subscribe() {
        if (mSubscriber != null) {
            mSubscriber.reset();
        }

        mFlowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSubscriber());
    }

    private void dispose() {
        if (mSubscriber != null && !mSubscriber.isDisposed()) {
            mSubscriber.dispose();
        }
    }

    private synchronized LoaderSubscriber<T> createSubscriber() {
        if (mSubscriber == null || mSubscriber.isDisposed()) {
            mSubscriber = new LoaderSubscriber<T>() {

                @Override
                public void onNext(T t) {
                    deliverResult(t);
                }

                @Override
                public void onError(Throwable t) {
                    super.onError(t);
                    deliverResult(null);
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    deliverResult(null); // signal for onComplete
                    deliverResult(mDataCache); // reset data
                }
            };
        }

        return mSubscriber;
    }
}
