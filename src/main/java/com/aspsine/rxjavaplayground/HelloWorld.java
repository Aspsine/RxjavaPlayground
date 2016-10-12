package com.aspsine.rxjavaplayground;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

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
                subscriber.onNext("Hello word" + "; " + Thread.currentThread());
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


    }

}
