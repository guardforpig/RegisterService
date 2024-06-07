package com.middleware.server.util;

import lombok.Data;

@Data
public class Log {

    private String belongClass;

    private String module;

    public Log(String belongClass, String module) {
        this.belongClass = belongClass;
        this.module = module;
    }


}
