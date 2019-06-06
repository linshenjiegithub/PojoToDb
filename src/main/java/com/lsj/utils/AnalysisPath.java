package com.lsj.utils;

import com.alibaba.fastjson.JSON;
import com.lsj.pojo.ClassInfo;
import com.lsj.pojo.FiledInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AnalysisPath {
    //类加载的目录
    final static String ClassLoaderPath = AnalysisPath.class.getClass().getResource("/").getPath().substring(1);

    public static String getPath(String packPath){
        return ClassLoaderPath+packPath;
    }

    public static void main(String[] args) {
        LinkedList<String> classPath = new LinkedList<>();

        getAllClass(getPath("com/lsj/Test"),classPath);

        List<ClassInfo> classInfoList = getClassInFo(classPath);
        System.out.println(JSON.toJSONString(classInfoList));
    }

    private static List<ClassInfo> getClassInFo(LinkedList<String> classPath) {
        List<ClassInfo> classInfos = new LinkedList<>();
        try {
            for(String path:classPath){
                Class clazz = Class.forName(path);
                ClassInfo classInfo = new ClassInfo();
                classInfo.setName(clazz.getSimpleName());
                classInfo.setAnnotationList(Arrays.asList(clazz.getDeclaredAnnotations()));
                Field[] fields = clazz.getDeclaredFields();
                if(fields!=null && fields.length>0){
                    List<FiledInfo> filedInfos = new ArrayList<>(fields.length);
                    for(Field field:fields){
                        FiledInfo filedInfo = new FiledInfo();
                        filedInfo.setName(field.getName());
                        filedInfo.setAnnotationList(Arrays.asList(field.getDeclaredAnnotations()));
                        filedInfo.setFieldType(field.getType().getSimpleName());
                        filedInfos.add(filedInfo);
                    }
                    classInfo.setFiledInfoList(filedInfos);
                }
                classInfos.add(classInfo);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classInfos;
    }

    public static void getAllClass(String path, LinkedList<String> classPath) {
        try {

            File fileRoot = new File(path);
            if (fileRoot.exists()) {
                File[] files = fileRoot.listFiles();
                for (File file : files) {
                    String filePath = file.getPath();
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        //去除加载路径、斜杠转点
                        String classP = filePath.substring(ClassLoaderPath.length()).replace("\\",".");
                        //去除.class
                        classP = classP.replace(".class","");
                        classPath.add(classP);
                        System.out.println(classP);
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
