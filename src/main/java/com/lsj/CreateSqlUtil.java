package com.lsj;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.File;
import java.lang.reflect.*;
import java.util.Date;
import java.util.LinkedList;

public class CreateSqlUtil {


    public static void main(String[] args) throws ClassNotFoundException {

        String DBName = "auth"; //数据库
        String modelName = "dbo"; //模式
        String packPath = "/com/lsj/threeSql"; //需要建表的对象的包路径
        String before = ""; //表名和字段名前缀
        boolean isIdentity = true; //id自增

        CreateSqlUtil createSqlUtil = new CreateSqlUtil();
        String ClassLoaderPath = createSqlUtil.getClass().getResource("/").getPath();
        LinkedList<String> classPaths = new LinkedList<>();
        createSqlUtil.getAllClass(ClassLoaderPath + packPath, classPaths);

        if (classPaths.size() > 0) {
            for (String classPath : classPaths) {
//                //根据驼峰命名转建表语句
//                String sql = createSqlUtil.createSqlByName(classPath, DBName, modelName,before,isIdentity);

//                //根据注解转化建表语句
//                String sql = createSqlUtil.createSqlByAnnotation(classPath, DBName, modelName,before);

//                //根据驼峰命名生成对应表字段的注解
//                String sql = createSqlUtil.createNoteByName(classPath, DBName, modelName,before);

//                //根据注解生成对应表字段的注解
//                String sql = createSqlUtil.createNoteByAnnotation(classPath, DBName, modelName,before);

                String sql = createSqlUtil.createNoteByName(classPath, DBName, modelName,before);

                System.out.println(sql);
            }
        }

    }

    //根据属性名称（驼峰命名）before 前缀
    public String createSqlByName(String classPath, String DBName, String modelName, String before,boolean isIdentity) {

        try {
            String ClassLoaderPath = this.getClass().getResource("/").getPath();
            String path = classPath.substring(ClassLoaderPath.length() - 1, classPath.length() - 6);
            String newpath = path.replace("\\", ".");
            Class clazz = Class.forName(newpath);
            String tableName = before+underscoreName(clazz.getSimpleName());
            StringBuffer sql = new StringBuffer();
            sql.append(" CREATE TABLE [" + DBName + "].[" + modelName + "].[" + tableName + "] (  \n");

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                String columnName = underscoreName(field.getName());
                String type = getType(field);
                if(type==null){
                    continue;
                }
                sql.append(" [");
                sql.append(before);
                sql.append(columnName);
                sql.append("] ");
                sql.append(type);

                String isNull = " null ";
                if(field.getName().toLowerCase().equals("id")){
                    isNull = "not null ";
                    sql.append(" PRIMARY KEY ");
                    //id自增
                    if(isIdentity){
                        sql.append(" IDENTITY(1,1) ");
                    }
                }
                sql.append(isNull);
                sql.append(" , \n");

            }
            sql.append(")\n");
            return sql.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //根据注解生成建表语句
    public String createSqlByAnnotation(String classPath, String DBName, String modelName,String before) {

        try {
            String ClassLoaderPath = this.getClass().getResource("/").getPath();
            String path = classPath.substring(ClassLoaderPath.length() - 1, classPath.length() - 6);
            String newpath = path.replace("\\", ".");
            Class clazz = Class.forName(newpath);

            Table table = (Table) clazz.getDeclaredAnnotation(Table.class);
            if (table == null) {
                return "";
            }
            StringBuffer sql = new StringBuffer();
            sql.append(" CREATE TABLE [" + DBName + "].[" + modelName + "].[" +before+table.name() + "] (  \n");


            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String type = getType(field);
                    if(type==null){
                        continue;
                    }
                    sql.append(" [");
                    sql.append(before+column.name());
                    sql.append("] ");
                    sql.append(type);

                    String isNull = " null ";

                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        isNull = "not null ";
                        sql.append(" PRIMARY KEY ");
                    }
                    sql.append(isNull);
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    if (generatedValue != null && generatedValue.strategy() == GenerationType.IDENTITY) {
                        sql.append(" IDENTITY(1,1) ");
                    }
                    sql.append(" , \n");

                }

            }
            sql.append(")");
            return sql.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //根据注解生成表和字段的注释
    public String createNoteByAnnotation(String classPath, String DBName, String modelName,String before) {

        try {
            String ClassLoaderPath = this.getClass().getResource("/").getPath();
            String path = classPath.substring(ClassLoaderPath.length() - 1, classPath.length() - 6);
            String newpath = path.replace("\\", ".");
            Class clazz = Class.forName(newpath);

            Table table = (Table) clazz.getDeclaredAnnotation(Table.class);
            if (table == null) {
                return "";
            }
            String tableName = table.name();
            StringBuffer sql = new StringBuffer();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String columnName = before+column.name();
                    String type = getType(field);
                    if(type==null){
                        continue;
                    }
                    StringBuffer sql2 = new StringBuffer();
                    ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                    if (apiModelProperty != null && StringUtils.isNotEmpty(apiModelProperty.value())) {
                        sql2.append(" EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'");
                        sql2.append(apiModelProperty.value());
                        sql2.append("' , @level0type=N'");
                        sql2.append("user");
                        sql2.append("',@level0name=N'");
                        sql2.append(modelName);
                        sql2.append("', @level1type=N'TABLE',@level1name=N'");
                        sql2.append(tableName);
                        sql2.append("', @level2type=N'COLUMN',@level2name=N'");
                        sql2.append(before+columnName);
                        sql2.append("'");
                        sql.append(sql2.toString());
                        sql.append("\n");
                    }
                }
            }
            return sql.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //根据注解生成表和字段的注释
    public String createNoteByName(String classPath, String DBName, String modelName,String before) {

        try {
            String ClassLoaderPath = this.getClass().getResource("/").getPath();
            String path = classPath.substring(ClassLoaderPath.length() - 1, classPath.length() - 6);
            String newpath = path.replace("\\", ".");
            Class clazz = Class.forName(newpath);

            String tableName = before+underscoreName(clazz.getSimpleName());
            StringBuffer sql = new StringBuffer();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String columnName = underscoreName(field.getName());
                String type = getType(field);
                if(type==null){
                    continue;
                }
                StringBuffer sql2 = new StringBuffer();
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null && StringUtils.isNotEmpty(apiModelProperty.value())) {
                    sql2.append(" EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'");
                    sql2.append(apiModelProperty.value());
                    sql2.append("' , @level0type=N'");
                    sql2.append("user");
                    sql2.append("',@level0name=N'");
                    sql2.append(modelName);
                    sql2.append("', @level1type=N'TABLE',@level1name=N'");
                    sql2.append(tableName);
                    sql2.append("', @level2type=N'COLUMN',@level2name=N'");
                    sql2.append(before+columnName);
                    sql2.append("'");
                    sql.append(sql2.toString());
                    sql.append("\n");
                }
            }
            return sql.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //字段类型
    public String getType(Field field) {

        if (field.getType().isAssignableFrom(Float.class)) {
            return " REAL";
        }
        if (field.getType().getSimpleName().equals("byte[]")) {
            return " nvarchar(MAX)"; //sqlserver
        }
        if (field.getType().isAssignableFrom(Short.class)) {
            return " int";
        }
        if (field.getType().isAssignableFrom(Integer.class)) {
            return " int ";
        }
        if (field.getType().isAssignableFrom(Long.class)) {
            return " int ";
        }
        if (field.getType().isAssignableFrom(String.class)) {
            return " varchar(256) ";
        }
        if (field.getType().isAssignableFrom(Date.class)) {
            return " datetime ";
        }
//        if (field.getType().isAssignableFrom(Date.class)) {
//            return " TIMESTAMP ";
//        }
        if (field.getType().isAssignableFrom(Boolean.class)) {
            return " BIT";
        }
        return null;

    }

    //驼峰转下划线，abcAbcaBc->abc_abca_bc
    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if ((name != null) && (name.length() > 0)) {
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if ((s.equals(s.toUpperCase())) && (!Character.isDigit(s.charAt(0)))) {
                    result.append("_");
                }
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

    public void getAllClass(String path, LinkedList<String> classPath) {
        try {

            File fileRoot = new File(path);
            if (fileRoot.exists()) {
                File[] files = fileRoot.listFiles();
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        classPath.add(file.getPath());
                        System.out.println(file.getPath());
                    }
                    if (file.isDirectory()) {
                        getAllClass(file.getPath(), classPath);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("获取calss文件路径失败");
        }

    }
}
