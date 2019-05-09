package com.lsj.utils;

import java.io.File;
import java.util.LinkedList;

public class AnalysisPath {
    //类加载的目录
    final static String ClassLoaderPath = AnalysisPath.class.getClass().getResource("/").getPath();

    public static String getPath(String packPath){

        return ClassLoaderPath+packPath;
    }

    public static void main(String[] args) {
        LinkedList<String> classPath = new LinkedList<>();

        getAllClass(getPath("com/lsj/Test"),classPath);
    }
    public static void getAllClass(String path, LinkedList<String> classPath) {
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
