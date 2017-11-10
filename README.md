# RxLoader
[![License](http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat-square)](https://developer.android.com/about/versions/android-4.1.html)
[![Download](https://img.shields.io/badge/JCenter-2.1.0-brightgreen.svg?style=flat-square)](https://bintray.com/l4digital/maven/RxLoader/_latestVersion)

An Android Loader that wraps an RxJava Observable.

RxLoader caches the data emitted by your Observable across orientation changes by utilizing an Android Loader, while also providing the results from an Observable preserving the RxJava pattern.



## Download

#### Gradle:
~~~groovy
dependencies {
    compile 'com.l4digital.rxloader:rxloader:2.1.0'
}
~~~

#### Maven:
~~~xml
<dependency>
  <groupId>com.l4digital.rxloader</groupId>
  <artifactId>rxloader</artifactId>
  <version>2.1.0</version>
</dependency>
~~~



## Usage
~~~java
import com.l4digital.rxloader.RxLoader;
import com.l4digital.rxloader.RxLoaderCallbacks;

...

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    RxLoader<DataType> loader = new RxLoader<>(this, getObservable());
    RxLoaderCallbacks<DataType> callbacks = new RxLoaderCallbacks<>(loader);

    callbacks.getFlowable().subscribe(this);

    getLoaderManager().initLoader(loaderId, Bundle.EMPTY, callbacks);
}
~~~

RxLoader can also be used with the [Android Support Library](https://developer.android.com/topic/libraries/support-library/index.html):

~~~java
import com.l4digital.support.rxloader.RxLoader;
import com.l4digital.support.rxloader.RxLoaderCallbacks;

...

getSupportLoaderManager().initLoader(loaderId, Bundle.EMPTY, callbacks);
~~~



## License
    Copyright 2016 L4 Digital LLC. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.