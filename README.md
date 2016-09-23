#RxLoader
An Android Loader that wraps an RxJava Observable.

RxLoader caches the data emitted by your Observable across orientation changes by utilizing an Android Loader, while also providing the results from an Observable preserving the RxJava pattern.



##Download

####Gradle:
~~~groovy
dependencies {
    compile 'com.l4digital.rxloader:rxloader:1.0.1'
}
~~~

####Maven:
~~~xml
<dependency>
  <groupId>com.l4digital.rxloader</groupId>
  <artifactId>rxloader</artifactId>
  <version>1.0.1</version>
</dependency>
~~~



##Usage
~~~java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    RxLoader<DataType> loader = new RxLoader<>(this, getObservable());
    RxLoaderCallbacks<DataType> callbacks = new RxLoaderCallbacks<>(loader);

    callbacks.getObservable().subscribe(this);

    getLoaderManager().initLoader(loaderId, Bundle.EMPTY, callbacks);
}
~~~



##License
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