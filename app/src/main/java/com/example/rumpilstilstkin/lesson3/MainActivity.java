package com.example.rumpilstilstkin.lesson3;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
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

        Arrays.sort(strings, (s, t1) -> {
            String t = "";
            return Integer.compare(s.length(), t1.length());
        });

        Arrays.sort(strings, new LengthComp());

        Arrays.sort(strings, (firstString, secondString) -> Integer.compare(firstString.length(), secondString.length()));

        Comparator<String> lengthComp = (firstString, secondString) -> Integer.compare(firstString.length(), secondString.length());

        Arrays.sort(strings, lengthComp);

        createFl();
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

    private void createFl() {
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
}
