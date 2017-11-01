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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxLoader<T> extends Loader<T> {

    private final Observable<T> mObservable;
    private LoaderSubscriber<T> mSubscriber;
    private T mDataCache;

    public RxLoader(Context context, @NonNull Observable<T> observable) {
        super(context);

        //noinspection ConstantConditions
        if (observable == null) {
            throw new NullPointerException("observable cannot be null");
        }

        mObservable = observable;
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

    public boolean hasCompleted() {
        return mSubscriber != null && mSubscriber.isCompleted();
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
        return mSubscriber == null || mSubscriber.isUnsubscribed();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
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

        mObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSubscriber());
    }

    private void unsubscribe() {
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
            mSubscriber.unsubscribe();
        }
    }

    private synchronized LoaderSubscriber<T> createSubscriber() {
        if (mSubscriber == null || mSubscriber.isUnsubscribed()) {
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
                public void onCompleted() {
                    super.onCompleted();
                    deliverResult(null); // signal for onComplete
                    deliverResult(mDataCache); // reset data

                }
            };
        }

        return mSubscriber;
    }
}
