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
import android.support.annotation.NonNull;

import com.l4digital.reactivex.LoaderSubject;

public class RxLoaderCallbacks<T> extends LoaderSubject<T> implements LoaderManager.LoaderCallbacks<T> {

    private final RxLoader<T> mLoader;

    public RxLoaderCallbacks(@NonNull RxLoader<T> loader) {
        //noinspection ConstantConditions
        if (loader == null) {
            throw new NullPointerException("loader cannot be null");
        }

        mLoader = loader;
    }

    @Override
    public RxLoader<T> onCreateLoader(int id, Bundle bundle) {
        createSubject();
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T t) {
        if (loader instanceof RxLoader && t == null) {
            RxLoader rxLoader = (RxLoader) loader;
            Throwable error = rxLoader.getError();

            if (error != null) {
                mSubject.onError(error);
                return;
            }

            if (rxLoader.hasCompleted()) {
                mSubject.onCompleted();
                return;
            }
        }

        if (t != null) {
            mSubject.onNext(t);
        }
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        mSubject.onCompleted();
    }
}
