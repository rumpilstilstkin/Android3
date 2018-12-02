package com.example.rumpilstilstkin.lesson3;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = findViewById(R.id.am_tv_text);

        text.setOnClickListener(view -> {
            String d = "";
        });

        String[] strings = {"3", "4", "5"};

        Arrays.sort(strings, (o1, o2) -> Integer.compare(o1.length(), o2.length()));

        Arrays.sort(strings, (s, t1) -> {
            String t = "";
            return Integer.compare(s.length(), t1.length());
        });

        Arrays.sort(strings, new LengthComp());

        Arrays.sort(strings, (firstString, secondString) -> Integer.compare(firstString.length(), secondString.length()));

        Comparator<String> lengthComp = (firstString, secondString) -> Integer.compare(firstString.length(), secondString.length());

        Arrays.sort(strings, lengthComp);

        repeat();
    }


    class LengthComp implements Comparator<String> {

        public int compare(String firstString, String secondString) {
            return Integer.compare(firstString.length(), secondString.length());
        }
    }

    private void disposable() {
        Disposable disposable = Observable.just(1, 2, 3).subscribeWith(new DisposableObserver<Integer>() {

            @Override
            public void onNext(@NonNull Integer integer) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        disposable.dispose();
    }

    private void flowable() {
        Flowable.just(1, 2, 3).subscribe(new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });

        Disposable disposable = Flowable.just(1, 2, 3).subscribeWith(new DisposableSubscriber<Integer>() {

            @Override
            public void onNext(Integer integer) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        disposable.dispose();
    }

    private void single() {
        Disposable disposable = Single.just("1").subscribeWith(new DisposableSingleObserver<String>() {

            @Override
            public void onSuccess(@NonNull String str) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }

    private void comp() {
        Disposable disposable = Completable.fromAction(
                () -> {
                    int a = 6 * 33;
                }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
    }

    private void maybe() {

        Disposable d = Maybe.just(1).subscribeWith(new DisposableMaybeObserver<Integer>() {

            @Override
            public void onSuccess(Integer t) {
                System.out.println(t);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
        });

        d.dispose();
    }

    @SuppressLint("CheckResult")
    private void createFl() {
        Flowable.fromCallable(new Callable<String>() {

            @Override
            public String call() {
                return "sret";
            }
        });
        Flowable<Integer> flowable = Flowable.create(emitter -> {
            for (int i = 1; i <= 20; i++) {
                Log.d("Dto", "send " + i);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("Dto", "interrupted " + e);
                    emitter.onError(e);
                    return;
                }
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);

        flowable
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d("Dto", "get " + integer);
                        try {
                            TimeUnit.SECONDS.sleep(20);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d("Dto", "interrupted " + e);
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void some() {
        Observable.just(1, 2, 3, 4)
                .take(3)
                .map(new Function<Integer, Integer>() {

                    @Override
                    public Integer apply(Integer integer) {
                        return integer * 2;
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void flapMap() {
        getData()
                .flatMap((Function<List<String>, ObservableSource<String>>) Observable::fromIterable)
                .subscribe(s -> Log.d("Dto", s));
    }

    @SuppressLint("CheckResult")
    private void contact() {
        Observable.concat(getData(), getData2(), getData3())
                .filter(strings -> !strings.isEmpty())
                .first(Collections.emptyList())
                .subscribe(s -> {
                    for (String item : s) {
                        Log.d("Dto", item);
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void dependence() {
        getData()
                .flatMap((Function<List<String>, ObservableSource<String>>) Observable::fromIterable)
                .subscribeOn(Schedulers.computation())
                .flatMap((Function<String, ObservableSource<Integer>>) this::getDetailsData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.d("Dto", "details " + s));
    }

    @SuppressLint("CheckResult")
    private void dependenceReverce() {
        getData()
                .flatMap((Function<List<String>, ObservableSource<String>>) Observable::fromIterable)
                .flatMap((Function<String, ObservableSource<Integer>>) this::getDetailsData, (s, integer) -> {
                    s = s + " " + integer;
                    return s;
                })
                .subscribe(s -> Log.d("Dto", s));
    }

    @SuppressLint("CheckResult")
    private void zip() {
        Observable.zip(getData(), getData2(),
                (strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                })
                .subscribe(s -> {
                    for (String item : s) {
                        Log.d("Dto", item);
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void retry() {
        getData()
                .retryWhen(throwableObservable -> throwableObservable.take(3).delay(1, TimeUnit.SECONDS));
    }

    @SuppressLint("CheckResult")
    private void retryError() {
        getData()
                .retryWhen(throwableObservable -> throwableObservable
                        .zipWith(
                                Observable.range(1, 3),
                                (BiFunction<Throwable, Integer, Observable>) (throwable, integer) -> {
                                    if (integer < 3) {
                                        return Observable.just(0L);
                                    }
                                    else {
                                        return Observable.error(throwable);
                                    }
                                })
                        .flatMap((Function<Observable, ObservableSource<?>>) observable -> observable));
    }

    @SuppressLint("CheckResult")
    private void repeat() {
        getData()
                .repeatWhen(objectObservable -> objectObservable.delay(1, TimeUnit.MINUTES).take(4))
                .subscribe(s -> {
                    for (String item : s) {
                        Log.d("Dto", item);
                    }
                });

        getData()
                .repeatWhen(objectObservable -> objectObservable.delay(1, TimeUnit.MINUTES))
                .takeUntil((Predicate<List<String>>) List::isEmpty);
    }

    private void compoDisp() {
        CompositeDisposable disposables = new CompositeDisposable();

        disposables.add(getData().subscribeWith(new DisposableObserver<List<String>>() {

            @Override
            public void onNext(List<String> strings) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));

        disposables.dispose();

        Flowable<String> flowable = Flowable.just("some");

        Disposable d = flowable.subscribeWith(new DisposableSubscriber<String>() {

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        d.dispose();
    }

    private Observable<List<String>> getData() {
        List<String> result = new ArrayList<>();
        result.add("getData1");
        result.add("emitter");
        result.add("rxJava");
        result.add("Maybe");
        result.add("Disposable");
        result.add("Single");
        result.add("Completable");
        return Observable.just(result);
    }

    private Observable<List<String>> getData2() {
        List<String> result = new ArrayList<>();
        result.add("getData2");
        result.add("emitter");
        result.add("rxJava");
        result.add("Maybe");
        result.add("Disposable");
        result.add("Single");
        result.add("Completable");
        return Observable.just(result);
    }

    private Observable<List<String>> getData3() {
        List<String> result = new ArrayList<>();
        result.add("getData2");
        result.add("emitter");
        result.add("rxJava");
        result.add("Maybe");
        result.add("Disposable");
        result.add("Single");
        result.add("Completable");
        return Observable.just(result);
    }

    private Observable<Integer> getDetailsData(String s) {
        return Observable.just(s.length());
    }
}
