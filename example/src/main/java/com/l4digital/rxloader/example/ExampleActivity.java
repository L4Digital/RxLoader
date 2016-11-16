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

package com.l4digital.rxloader.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.l4digital.support.rxloader.RxLoader;
import com.l4digital.support.rxloader.RxLoaderCallbacks;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class ExampleActivity extends AppCompatActivity implements Observer<String> {

    private static final String[] sVersionNames = new String[]{
            "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich",
            "Jelly Bean", "KitKat", "Lollipop", "Marshmallow", "Nougat"
    };

    private Disposable mDisposable;
    private TextView mExampleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        mExampleText = (TextView) findViewById(R.id.example_text);

        RxLoader<String> loader = new RxLoader<>(this, getObservable());
        RxLoaderCallbacks<String> callbacks = new RxLoaderCallbacks<>(loader);

        callbacks.getObservable().subscribe(this);

        getSupportLoaderManager().initLoader(1, Bundle.EMPTY, callbacks);
    }

    @Override
    protected void onDestroy() {
        unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        unsubscribe();
        mDisposable = disposable;
    }

    @Override
    public void onNext(String value) {
        mExampleText.append(value + "\n");
    }

    @Override
    public void onError(Throwable e) {
        mExampleText.setText(e.getMessage());
    }

    @Override
    public void onComplete() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    private Observable<String> getObservable() {
        return Observable.interval(500, TimeUnit.MILLISECONDS)
                .takeWhile(new Predicate<Long>() {

                    @Override
                    public boolean test(Long tick) throws Exception {
                        return tick < sVersionNames.length;
                    }
                })
                .map(new Function<Long, String>() {

                    @Override
                    public String apply(Long tick) throws Exception {
                        return sVersionNames[tick.intValue()];
                    }
                });
    }

    private void unsubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}
