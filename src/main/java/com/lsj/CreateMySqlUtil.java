package com.lsj;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;

public class CreateMySqlUtil {


    public static void main(String[] args)  {


        String packPath = "/com/lsj/twoSql"; //需要建表的对象的包路径
        String before = "_"; //表名和字段名前缀
        boolean isIdentity = true; //id自增

        CreateMySqlUtil createSqlUtil = new CreateMySqlUtil();
        String ClassLoaderPath = createSqlUtil.getClass().getResource("/").getPath();
        LinkedList<String> classPaths = new LinkedList<>();
        createSqlUtil.getAllClass(ClassLoaderPath + packPath, classPaths);

        if (classPaths.size() > 0) {
            for (String classPath : classPaths) {
//                //根据驼峰命名转建表语句
//                String sql = createSqlUtil.createSqlByName(classPath,before,isIdentity);

//                //根据注解转化建表语句
//                String sql = createSqlUtil.createSqlByAnnotation(classPath,before);


                String sql = createSqlUtil.createSqlByName(classPath,before,isIdentity);

                System.out.println(sql);
            }
        }

    }

    //根据属性名称（驼峰命名）before 前缀
    public String createSqlByName(String classPath, String before,boolean isIdentity) {

        try {
            String ClassLoaderPath = this.getClass().getResource("/").getPath();
            String path = classPath.substring(ClassLoaderPath.length() - 1, classPath.length() - 6);
            String newpath = path.replace("\\", ".");
            Class clazz = Class.forName(newpath);

            String tableName = before+underscoreName(clazz.getSimpleName());

            StringBuffer sql = new StringBuffer();
            sql.append(" CREATE TABLE `" + tableName + "`(  \n");

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                String columnName = underscoreName(field.getName());
                String type = getType(field);
                if(type==null){
                    continue;
                }
                sql.append(" `");
                sql.append(before);
                sql.append(columnName);
                sql.append("` ");
                sql.append(type);

                String isNull = " null ";
                if(field.getName().toLowerCase().equals("id")){
                    isNull = "not null ";
                    sql.append(" PRIMARY KEY ");
                    //id自增
                    if(isIdentity){
                        sql.append(" AUTO_INCREMENT ");
                    }
                }
                sql.append(isNull);
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null && StringUtils.isNotEmpty(apiModelProperty.value())) {
                    sql.append("COMMENT '"+apiModelProperty.value()+"'");
                }
                sql.append(" , \n");

            }
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ApiModel apiModel = (ApiModel) clazz.getAnnotation(ApiModel.class);
            if (apiModel != null && StringUtils.isNotEmpty(apiModel.value())) {
                sql.append(" COMMENT '"+apiModel.value()+"'");
            }
            sql.append(" ;\n");
            return sql.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //根据注解生成建表语句
    public String createSqlByAnnotation(String classPath,String before) {

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
            sql.append(" CREATE TABLE `" +before+table.name() + "` (  \n");


            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String type = getType(field);
                    if(type==null){
                        continue;
                    }
                    sql.append(" `");
                    sql.append(before+column.name());
                    sql.append("` ");
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
                        sql.append(" AUTO_INCREMENT ");
                    }
                    ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                    if (apiModelProperty != null && StringUtils.isNotEmpty(apiModelProperty.value())) {
                        sql.append("COMMENT '"+apiModelProperty.value()+"'");
                    }
                    sql.append(" , \n");

                }

            }
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            ApiModel apiModel = (ApiModel) clazz.getAnnotation(ApiModel.class);
            if (apiModel != null && StringUtils.isNotEmpty(apiModel.value())) {
                sql.append(" COMMENT '"+apiModel.value()+"'");
            }
            sql.append(" ;\n");
            return sql.toString();
        } catch (ClassNotFoundException e) {
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
            return " smallint";
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
            return " tinyint(1)";
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
