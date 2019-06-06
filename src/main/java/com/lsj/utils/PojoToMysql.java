package com.lsj.utils;

import com.lsj.MyEnum.DbType;
import com.lsj.pojo.ClassInfo;

import java.util.LinkedList;
import java.util.List;

public class PojoToMysql {

    public static List<String> getsql(List<ClassInfo> classInfoList, DbType dbType) {
        List<String> sqlList = new LinkedList<>();
        for (ClassInfo classInfo : classInfoList) {
            sqlList.add(getsql(classInfo, dbType));
        }
        return sqlList;
    }

    public static String getsql(ClassInfo classInfo, DbType dbType) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table ");
        stringBuffer.append(classInfo.getName()+" {");
        stringBuffer.append("}");
        return stringBuffer.toString();
    }
}
