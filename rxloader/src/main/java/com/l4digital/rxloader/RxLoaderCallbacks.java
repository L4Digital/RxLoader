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

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.processors.BehaviorProcessor;

public class RxLoaderCallbacks<T> implements LoaderManager.LoaderCallbacks<T> {

    private final RxLoader<T> mLoader;
    private BehaviorProcessor<T> mProcessor;

    public Flowable<T> getFlowable() {
        return createProcessor();
    }

    public Observable<T> getObservable() {
        return createProcessor().toObservable();
    }

    public RxLoaderCallbacks(RxLoader<T> loader) {
        mLoader = loader;
    }

    @Override
    public RxLoader<T> onCreateLoader(int id, Bundle bundle) {
        createProcessor();
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T t) {
        if (loader instanceof RxLoader && t == null) {
            RxLoader rxLoader = (RxLoader) loader;
            Throwable error = rxLoader.getError();

            if (error != null) {
                mProcessor.onError(error);
                return;
            }

            if (rxLoader.hasComplete()) {
                mProcessor.onComplete();
                return;
            }
        }

        mProcessor.onNext(t);
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        mProcessor.onComplete();
    }

    private synchronized BehaviorProcessor<T> createProcessor() {
        if (mProcessor == null || mProcessor.hasComplete()) {
            mProcessor = BehaviorProcessor.create();
        }

        return mProcessor;
    }
}
