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
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class RxLoader<T> extends Loader<T> {

    private final Observable<T> mObservable;
    private Throwable mError;
    private T mDataCache;

    public RxLoader(Context context, Observable<T> observable) {
        super(context);
        mObservable = observable;
    }

    @Override
    public void deliverResult(T data) {
        mDataCache = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    public Throwable getError() {
        return mError;
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
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mDataCache = null;
        mError = null;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();

        mObservable.subscribeOn(Schedulers.io()).subscribe(new Subscriber<T>() {

            @Override
            public void onCompleted() {
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
        });
    }
}
