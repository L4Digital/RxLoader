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

import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class LoaderSubject<T> {

    protected BehaviorSubject<T> mSubject;

    public Observable<T> getObservable() {
        return createSubject();
    }

    protected synchronized BehaviorSubject<T> createSubject() {
        if (mSubject == null || mSubject.hasCompleted()) {
            mSubject = BehaviorSubject.create();
        }

        return mSubject;
    }
}
