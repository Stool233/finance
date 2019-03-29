package org.stool.myserver.example.simple;

import org.stool.myserver.core.AsyncResult;
import org.stool.myserver.core.Context;
import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.Future;

import java.io.IOException;

public class FutureMain {

    Context context;

    public static void main(String[] args) {

        new FutureMain().testCompose2();

    }

    private static void testMap() {
        Future<String> future = Future.future();

        future
                .map(str -> str + "b")
                .map(str -> str + "c")
                .setHandler(ar -> {
                    if (ar.succeeded()) {
                        System.out.println(ar.result());
                    }
                });

        future.complete("a");
    }


    private static void testCompose() {
        Future<String> future = Future.future();
        Future<String> future2 = Future.future();

        future.next(str -> {
            future2.complete(str + "another");
            return future2;
        }).setHandler(ar -> {
            if (ar.succeeded()) {
                System.out.println(ar.result());
            }
        });

        future.complete("this ");

    }

    private Future<String> future1() {
        Future<String> future = Future.future();
        context.executeAsync(v -> {
            // get some data
            String data = "data1";
            future.complete(data);
        });
        return future;
    }

    private Future<String> future2(String data) {
        Future<String> future = Future.future();
        context.executeAsync(v -> {
            // get data2 with data
            String data2 = data + "2";
            future.complete(data2);
        });
        return future;
    }

    private void testCompose2() {

        EntryPoint entryPoint = EntryPoint.entryPoint();

        context = entryPoint.getOrCreateContext();

        future1()
                .next(this::future2)
                .setHandler(ar -> {
                    if (ar.succeeded()) {
                        System.out.println(ar.result());
                    }
                });
    }

    private void test3() {

        // 从远程数据库中获取财务数据。
        String financeData = getFinanceDataFromDatabase();

        // 将财务数据写入excel表格
        try {
            writeToExcel(financeData);
            System.out.println("获取财务数据并写入Excel成功");
        } catch (IOException e) {
            // 写入失败
            e.printStackTrace();
        }

    }

    private void test() {
        EntryPoint entryPoint = EntryPoint.entryPoint();
        Context context = entryPoint.getOrCreateContext();

        getFinanceData(context)
                .next(financeData -> printExcel(context, financeData))
                .setHandler(ar -> solveAsyncResult(ar));
    }

    private Future<String> getFinanceData(Context context) {
        Future<String> future = Future.future();
        context.<String>executeBlocking(blockFuture -> {
            String financeData = getFinanceDataFromDatabase();
            blockFuture.complete(financeData);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                future.complete(asyncResult.result());
            }
        });
        return future;
    }

    private String getFinanceDataFromDatabase() {
        return null;
    }

    private Future<Void> printExcel(Context context, String financeData) {
        Future<Void> future = Future.future();
        context.executeAsync(v -> {
            try {
                // 将财务数据写入excel表格
                writeToExcel(financeData);
                future.complete();
            } catch (IOException e) {
                // 写入失败
                future.fail(e);
            }

        });
        return future;
    }

    private void writeToExcel(String financeData) throws IOException {
    }

    private <T> void solveAsyncResult(AsyncResult<T> ar) {
        if (ar.succeeded()) {
            System.out.println("获取财务数据并写入Excel成功");
        }
    }

}
