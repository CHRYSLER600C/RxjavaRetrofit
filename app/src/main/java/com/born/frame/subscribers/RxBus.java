package com.born.frame.subscribers;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RxBus并不是一个库，而是一种模式。相信大多数开发者都使用过EventBus，作为事件总线通信库，
 * 如果你的项目已经加入RxJava和EventBus，不妨用RxBus代替EventBus，以减少库的依赖。
 *
 * Created by min on 2016/12/16.
 */

public class RxBus {

    private final Subject<Object, Object> mBus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    private static class SingletonHolder {
        private static volatile RxBus INSTANCE = new RxBus();
    }

    /**
     * 获取单例
     */
    public static RxBus getInstance() {
        return RxBus.SingletonHolder.INSTANCE;
    }

    // 发送一个新的事件
    public void post(Object o) {
        mBus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mBus.ofType(eventType);
//        这里感谢小鄧子的提醒: ofType = filter + cast
//        return mBus.filter(new Func1<Object, Boolean>() {
//            @Override
//            public Boolean call(Object o) {
//                return eventType.isInstance(o);
//            }
//        }) .cast(eventType);
    }
}