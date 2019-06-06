package com.lsj.Test.test2;

import com.lsj.Annotations.Level;
import com.lsj.Annotations.Name;
import com.lsj.Test.Test;
import lombok.Data;

@Data
@Name("Test2")
public class Test2 extends Test {
    @Name("cccc")
    @Level(100)
    private String cccc;
}
