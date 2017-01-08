package com.demo.maat.hello_rxjava;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 熟悉的基础
 */
public class BaseDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_demo);

//        这种在 subscribe() 之前写上两句
//        subscribeOn(Scheduler.io()) 和 observeOn(AndroidSchedulers.mainThread()) 的使用方式非常常见，
//        它适用于多数的 『后台线程取数据，主线程显示』的程序策略。
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer number) {
                        Log.d("shitou", "number:" + number);
                    }
                });
    }

    //Observer 即观察者，它决定事件触发的时候将有怎样的行为。
    Observer<String> observer = new Observer<String>() {

        @Override
        public void onNext(String s) {
            //普通事件 onNext() （相当于 onClick() / onEvent()）
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

    };
    //    实现了 Observer 的抽象类  他们的基本使用方式是完全一样的
//    实质上，在 RxJava 的 subscribe 过程中，Observer 也总是会先被转换成一个 Subscriber 再使用
    Subscriber<String> subscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {

        }

        @Override
        public void onStart() {
            super.onStart();
            //注意线程的使用，默认在
            // 可以用于做一些准备工作，例如数据的清零或重置 总是在 subscribe 所发生的线程被调用，而不能指定线程
            // 要在指定的线程来做准备工作，可以使用 doOnSubscribe() 方法
        }

    };

    //Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件
    Observable observable100 = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? super String> subscriber) {
            subscriber.onNext("01");
            subscriber.onNext("02");
            subscriber.onNext("03");
            subscriber.onCompleted();
        }
    });
    /**
     * 简介写法,和上面写法一样
     * just(T...): 将传入的参数依次发送出来
     * from(T[]) / from(Iterable<? extends T>) : 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
     */
    Observable observable01 = Observable.just("Hello", "Hi", "Aloha");
    String[] words = {"Hello", "Hi", "Aloha"};
    Observable observable02 = Observable.from(words);

    /**
     * Subscribe (订阅)----类似杂志订阅读者！！！！
     * observable.subscribe(observer);        observable.subscribe(subscriber);
     */


    Action1<String> onNextAction = new Action1<String>() {
        // onNext()
        @Override
        public void call(String s) {
            Log.d("shitou", s);
        }
    };
    Action1<Throwable> onErrorAction = new Action1<Throwable>() {
        // onError()
        @Override
        public void call(Throwable throwable) {
            // Error handling
        }
    };
    Action0 onCompletedAction = new Action0() {
        // onCompleted()
        @Override
        public void call() {
            Log.d("shitou", "completed");
        }
    };

// 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
//    observable.subscribe(onNextAction);
// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
//    observable.subscribe(onNextAction, onErrorAction);
// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
//    observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    /**
     * a. 打印字符串数组
     */
    String[] names ={"Hello", "Hi", "Aloha"};
    Observable observable= (Observable) Observable.from(names).subscribe(new Action1<String>() {
        @Override
        public void call(String s) {
            Log.d("", s);
        }
    });
    /**
     * b. 由 id 取得图片并显示
     * 注意：如果from()里面执行了耗时操作，即使使用了subscribeOn(Schedulers.io())，仍然是在主线程执行，
     * 可能会造成界面卡顿甚至崩溃，所以耗时操作还是使用Observable.create(…);
     */
    int drawableRes = 0;
    ImageView imageView =null;
    Observable observable1000 = (Observable) Observable.create(new Observable.OnSubscribe<Drawable>() {
        @Override
        public void call(Subscriber<? super Drawable> subscriber) {
            Drawable drawable =  ContextCompat.getDrawable(BaseDemoActivity.this, R.mipmap.ic_launcher);
            subscriber.onNext(drawable);  // 把Drawable对象发送出去
            subscriber.onCompleted();
        }
    })      // 指定 subscribe() 所在的线程，也就是上面call()方法调用的线程
            .subscribeOn(Schedulers.io())
            // 指定 Subscriber 回调方法所在的线程，也就是onCompleted, onError, onNext回调的线程
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Drawable>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(BaseDemoActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Drawable drawable) {
                    imageView.setImageDrawable(drawable);
                }
            });

    /**
     * RxJava 遵循的是线程不变的原则，即：在哪个线程调用 subscribe()，就在哪个线程生产事件；
     * 在哪个线程生产事件，就在哪个线程消费事件。如果需要切换线程，就需要用到 Scheduler （调度器）。
     */



}







































































































