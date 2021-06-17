package com.geekcarl.android.webview.example.ui;

/**
 * Created on 2021/4/9
 * <p>
 *
 * @author zengxiangxin
 */
public class TestObject {
    public String name;
    public String content;

    public TestObject(){}

    public TestObject(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
