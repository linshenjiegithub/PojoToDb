package com.lsj.utils;

import com.lsj.MyEnum.DbType;
import com.lsj.pojo.ClassInfo;

import java.util.LinkedList;
import java.util.List;

public class PojoToMysql {

    public List<String> getsql(List<ClassInfo> classInfoList, DbType dbType) {
        List<String> sqlList = new LinkedList<>();
        for (ClassInfo classInfo : classInfoList) {
            sqlList.add(getsql(classInfo, dbType));
        }
        return sqlList;
    }

    public String getsql(ClassInfo classInfo, DbType dbType) {
        StringBuffer stringBuffer = new StringBuffer();
        return stringBuffer.toString();
    }
}
