package com.lsj.pojo;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.List;
@Data
public class Info {
    private String name;
    private List<Annotation> annotationList;
}
