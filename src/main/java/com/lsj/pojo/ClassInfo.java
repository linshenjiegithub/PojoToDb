package com.lsj.pojo;

import lombok.Data;

import java.util.List;


@Data
public class ClassInfo extends Info {
    private List<FiledInfo> filedInfoList;

}
