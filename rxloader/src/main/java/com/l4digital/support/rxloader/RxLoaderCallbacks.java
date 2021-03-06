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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.l4digital.reactivex.LoaderProcessor;

public class RxLoaderCallbacks<T> extends LoaderProcessor<T> implements LoaderManager.LoaderCallbacks<T> {

    private final RxLoader<T> mLoader;

    public RxLoaderCallbacks(@NonNull RxLoader<T> loader) {
        //noinspection ConstantConditions
        if (loader == null) {
            throw new NullPointerException("loader cannot be null");
        }

        mLoader = loader;
    }

    @NonNull
    @Override
    public RxLoader<T> onCreateLoader(int id, Bundle bundle) {
        createProcessor();
        return mLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<T> loader, T t) {
        if (t != null) {
            mProcessor.onNext(t);
        }

        if (loader instanceof RxLoader) {
            RxLoader rxLoader = (RxLoader) loader;
            Throwable error = rxLoader.getError();

            if (error != null) {
                mProcessor.onError(error);
            } else if (rxLoader.hasComplete()) {
                mProcessor.onComplete();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<T> loader) {
        mProcessor.onComplete();
    }
}
