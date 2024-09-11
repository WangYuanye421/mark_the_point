package com.wangyuanye.plugin;


import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wangyuanye.plugin.util.IdeaMessageUtil;
import com.wangyuanye.plugin.util.MyUtils;
import org.junit.Test;

import java.util.Locale;

/**
 * @author wangyuanye
 *  2024/8/27
 **/
public class MyTest extends BasePlatformTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * 文件路径转为文件名称
     */
    @Test
    public void testPath2Name(){
        String path = "/usr/local/Cellar/openjdk/22.0.2/libexec/openjdk.jdk/Contents/Home/lib/src.zip!/java.base/java/lang/String.java";
        String replace = MyUtils.genFilenameFromPath(path);
        System.out.println(replace);
    }

    /**
     * 测试国际化
     */
    @SuppressWarnings("all")
    public void testI18n(){
        System.out.println("当前地区:" + Locale.getDefault());
        System.out.println(IdeaMessageUtil.getMessage("key.for.test"));
        Locale.setDefault(Locale.US);
        System.out.println("当前地区:" + Locale.getDefault());
        System.out.println(IdeaMessageUtil.getMessage("key.for.test"));
    }


}
