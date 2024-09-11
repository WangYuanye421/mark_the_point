
<!-- Plugin description -->
**IntelliJ Platform Plugin Template** is a repository that provides a pure template to make it easier to create a new plugin project (check the [Creating a repository from a template][gh:template] article).

The main goal of this template is to speed up the setup phase of plugin development for both new and experienced developers by preconfiguring the project scaffold and CI, linking to the proper documentation pages, and keeping everything organized.

[gh:template]: https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template
<!-- Plugin description end -->

## mark的录入
- 打开交互窗口(默认高亮色,笔记内容可为空)
- 在选取开始和结束的位置上, 添加icon标记,点击标记可以显示交互窗口(取消/确认/删除)

## mark的回显
- 高亮色 + icon图标

## mark列表查看
- 源码 + mark条数,点击打开markline列表
- markline列表,可修改/删除,可导航到源码具体位置
- mark修改(note 颜色, 高亮区域重新选择(让用户重新进行selection,获取start和end对应的offset))
- mark删除

## todo
- head列表  line列表
- 设置界面
- 默认值(字体颜色, 背景颜色,国际化参数)
- 性能检测与优化





### 使用 Annotations 显示图标

另一种方法是使用注解（Annotations），它们可以在代码中添加图标或图形标记。这通常涉及到在代码中使用自定义注解来标记位置，然后在该位置显示图标。

#### 示例步骤

1. **实现自定义注解**：
   创建自定义注解，用于标记要显示图标的代码位置。

2. **创建 `AnnotationProvider`**：
   实现 `AnnotationProvider` 来在特定的代码元素旁边显示图标。

### 示例代码



### 注册 AnnotationProvider

在 `plugin.xml` 中注册你的 `Annotator` 实现，以确保 IDEA 知道在哪里应用你的注解。

```xml
<extensions defaultExtensionNs="com.intellij">
    <annotator language="JAVA" implementationClass="com.example.MyAnnotationProvider"/>
</extensions>
```

### 总结

要在代码中显示图标，可以使用 **Inlay Model** 或 **Annotations**。`Inlay Model` 允许你在代码的特定位置插入图标，而 `Annotations` 
允许你在代码旁边添加图标和其他注释。这些方法可以帮助你在 IntelliJ IDEA 中实现类似 `@RequestMapping` 的功能。
