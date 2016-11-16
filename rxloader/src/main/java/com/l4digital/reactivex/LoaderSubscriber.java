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

package com.l4digital.reactivex;

import android.support.annotation.CallSuper;

import rx.Subscriber;

public abstract class LoaderSubscriber<T> extends Subscriber<T> {

    private Throwable mThrowable;
    private boolean mCompleted;

    @CallSuper
    @Override
    public void onError(Throwable t) {
        mThrowable = t;
        unsubscribe();
    }

    @CallSuper
    @Override
    public void onCompleted() {
        mCompleted = true;
        unsubscribe();
    }

    public Throwable getError() {
        return mThrowable;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void reset() {
        unsubscribe();
        mThrowable = null;
        mCompleted = false;
    }
}
