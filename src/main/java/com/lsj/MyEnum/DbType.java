package com.lsj.MyEnum;

public enum DbType {
    MYSQL(1, "mysql"),
    ORACLE(1, "oracle"),
    SQLSERVER(1, "mysql");

    public Integer value;

    public String name;

    DbType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
