package com.lsj.MyEnum;

public enum DbType {
    MYSQL((Integer) 1, "mysql"),
    ORACLE((Integer) 1, "oracle"),
    SQLSERVER((Integer) 1, "mysql");

    public Integer value;

    public String name;

    DbType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
