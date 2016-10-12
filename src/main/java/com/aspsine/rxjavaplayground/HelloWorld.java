package com.aspsine.rxjavaplayground;

import com.oracle.tools.packager.Log;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.OnErrorFailedException;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangfan10 on 16/10/12.
 */
public class HelloWorld {
    public static void main(String[] args) {
        // 0
        Observable.create(subscriber -> {
            subscriber.onNext("hello world!");
            subscriber.onCompleted();
        }).subscribe(System.out::println);

        // 1
        Observable.just("Hello!", "World!").subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done"));

        // 2
        Observable.just("Hello world!").subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done"));

        //3
        Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello,, World!!");
                subscriber.onCompleted();
            }
        }).subscribe(new Subscriber<String>() {

            @Override
            public void onNext(String arg0) {
                System.out.println(arg0);
            }

            @Override
            public void onCompleted() {
                System.out.println("Done");
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }
        });

        // 4
        Observable.create(subscriber -> {
            try {
                subscriber.onNext("Hello world!");
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribe(
                System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done"));

        //5
        Observable.create(subscriber -> {
            new Thread(() -> {
                try {
                    subscriber.onNext("Hello world:" + Thread.currentThread().toString());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }).start();
        }).subscribe(System.out::println);

        //6
        Observable.create(subscriber -> {
            try {
                subscriber.onNext("Hello world:" + Thread.currentThread());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(System.out::println);

        //7
        Observable.create(subscriber -> {
            try {
                subscriber.onNext("Hello word" + "; " + Thread.currentThread());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .map(string -> string + "; " + Thread.currentThread())
                .subscribe(System.out::println);


        //8
        Observable.create(subscriber -> {
            try {
                // here will throw a null point exception
                String s = null;
                subscriber.onNext(s.length() + "Hello word" + "; " + Thread.currentThread());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .map(string -> string + "; " + Thread.currentThread())
                .onErrorResumeNext(e -> Observable.just("Fallback Data: " + e))
                .subscribe(System.out::println);

        //9
        Observable.create(subscriber -> {
            int i = 0;
            while (!subscriber.isUnsubscribed()) {
                subscriber.onNext(i++);
            }
        }).take(10)
                .subscribe(System.out::println);

        //10
        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorResumeNext(throwable -> Observable.just("onErrorResumeNext"))
                .subscribe(System.out::println);

        //TODO what's the difference between onErrorResumeNext & onErrorReturn?

        //11
        Observable.create(subscriber -> {
            throw new RuntimeException("failed1!");
        }).onErrorResumeNext(Observable.just("onErrorResumeNext1"))
                .subscribe(System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("done1"));

        //12
        Observable.create(subscriber -> {
            throw new RuntimeException("failed2!");
        }).onErrorReturn(throwable -> "onErrorReturn2")
                .subscribe(
                        System.out::println,
                        Throwable::printStackTrace,
                        () -> System.out.println("done2"));

        //13
        Observable.create(subscriber -> {
            throw new RuntimeException("failed2!");
        }).retryWhen(observable ->
                observable.zipWith(Observable.range(1, 3), (throwable, i) -> i)
                        .flatMap(i -> {
                            System.out.println("retry:" + i);
                            return Observable.timer(i, TimeUnit.SECONDS);
                        })
                        .concatWith(Observable.error(new RuntimeException("Executed 3 retries!")))
        ).subscribe(System.out::println, Throwable::printStackTrace);

        //14
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                throw new RuntimeException("Failed3!");
            }
        }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<Long>>() {

            @Override
            public Observable<Long> call(Observable<? extends Throwable> observable) {
                return observable.zipWith(Observable.range(1, 4), new Func2<Throwable, Integer, Integer>() {

                    @Override
                    public Integer call(Throwable throwable, Integer integer) {
                        return integer;
                    }
                }).flatMap(new Func1<Integer, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Integer integer) {
                        System.out.println("retry:" + integer);
                        return Observable.timer(integer, TimeUnit.SECONDS);
                    }
                }).concatWith(Observable.error(new RuntimeException("Executed 4 retries!")));
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("Done!");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        });

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

    }

}
