package com.stool.finance.demo;

import org.stool.myserver.core.EntryPoint;

public class Bootstrap {

    public static void main(String[] args) {

        EntryPoint.entryPoint()
                .createHttpServer()
                .requestHandler(request -> {
                    request.response().end("Hello World");
                }).listen(8085);


    }

}
