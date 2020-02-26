package com.example;

import io.netty.util.concurrent.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gaozhuo
 * @date 2020/2/24
 */
public class NettyTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NettyTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(NettyTest.class);
    }


    public void testPromise() {
        EventExecutor executor = new DefaultEventExecutor();
        Promise<Integer> promise = new DefaultPromise<Integer>(executor);
        promise.addListener(new GenericFutureListener<Future<Integer>>() {
            @Override
            public void operationComplete(Future<Integer> future) throws Exception {
                if (future.isSuccess()) {
                    Integer result = future.get();
                    System.out.println(result);
                } else {
                    System.out.println("fail:" + future.cause());
                }
            }
        }).addListener(new GenericFutureListener<Future<Integer>>() {
            @Override
            public void operationComplete(Future future) throws Exception {
                System.out.println("finish");
            }
        });
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                // 设置 promise 的结果
                // promise.setFailure(new RuntimeException());
                promise.setSuccess(123456);
            }
        });
        try {
            promise.sync();
            System.out.println("8888888");
        } catch (InterruptedException e) {
        }
    }
}
