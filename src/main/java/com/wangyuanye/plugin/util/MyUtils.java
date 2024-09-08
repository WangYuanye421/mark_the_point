package com.wangyuanye.plugin.util;

import cn.hutool.core.io.FileUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.intellij.ui.SimpleTextAttributes.STYLE_ITALIC;

/**
 * 国际化工具类
 *
 * @author wangyuanye
 * @date 2024/8/15
 **/
public class MyUtils {
    public static Logger logger = new DefaultLogger("[MessagesUtil]");

    private static final String BUNDLE_MSG_PATH = "messages/messages"; // 不带扩展名的基础名称
    private static final String BUNDLE_DATA_PATH = "config_data";



    // 获取消息内容
    public static @NotNull String getMessage(String key) {
        // 支持动态切换,每次根据地区,重新读取
        ResourceBundle BUNDLE_MSG = ResourceBundle.getBundle(BUNDLE_MSG_PATH, Locale.getDefault());
        return BUNDLE_MSG.getString(key);
    }

    public static @NotNull String getConfigData(String key) {
        ResourceBundle BUNDLE_DATA = ResourceBundle.getBundle(BUNDLE_DATA_PATH, Locale.getDefault());
        return BUNDLE_DATA.getString(key);
    }

    public static void setEmptyText(JBTable table) {
        table.getEmptyText().setText(MyUtils.getMessage("no_data"));
        table.getEmptyText().appendLine(AllIcons.Modules.EditFolder, MyUtils.getMessage("no_data_tip1"),
                new SimpleTextAttributes(STYLE_ITALIC, JBColor.darkGray), null);
        table.getEmptyText().appendLine(AllIcons.General.Add, MyUtils.getMessage("no_data_tip2"),
                new SimpleTextAttributes(STYLE_ITALIC, JBColor.darkGray), null);
    }

    /**
     * 设置到剪切板
     *
     * @param text
     */
    public static void setClipboardContent(String text) {
        // 创建一个 StringSelection 对象，包含要复制的文本
        StringSelection stringSelection = new StringSelection(text);

        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 将字符串设置到剪贴板
        clipboard.setContents(stringSelection, null);
    }

    /**
     * 获取系统剪贴板中的文本内容
     *
     * @return 剪贴板中的文本内容，如果剪贴板不包含文本，则返回 null
     */
    public static String getClipboardContent() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪贴板内容
        Transferable contents = clipboard.getContents(null);
        // 检查剪贴板是否包含文本数据
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                // 从剪贴板中获取文本内容
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        // 如果剪贴板不包含文本内容，返回 null
        return null;
    }

    /**
     * 将剪贴板内容粘贴到获取焦点的终端窗口中
     */
    public static void pastToTerminal(Project project, ToolWindow openTerminal) {
        if (openTerminal == null) {
            logger.warn("终端窗口未获取");
            return;
        }
        openTerminal.activate(() -> {
            IdeFocusManager.getInstance(project).doWhenFocusSettlesDown(() -> {
                try {
                    Robot robot = new Robot();
                    robot.delay(50);  // 等待一下，确保焦点稳定
                    String os = System.getProperty("os.name");
                    logger.info("当前用户os : " + os);
                    if (os.toLowerCase().contains("mac")) {
                        robot.keyPress(KeyEvent.VK_META);
                        robot.delay(50);// 延迟,避免粘贴出现单独的V
                        robot.keyPress(KeyEvent.VK_V);
                        robot.keyRelease(KeyEvent.VK_V);
                        robot.keyRelease(KeyEvent.VK_META);
                    } else {
                        robot.keyPress(KeyEvent.VK_CONTROL);
                        robot.delay(50);
                        robot.keyPress(KeyEvent.VK_V);
                        robot.keyRelease(KeyEvent.VK_V);
                        robot.keyRelease(KeyEvent.VK_CONTROL);
                    }
                } catch (AWTException e) {
                    logger.error(e.getMessage());
                }
            });
        });
    }


    public static String buildBalloon(String msg) {
        return "Mark The Point : " + msg;
    }


    public static void main(String[] args) {
        // 测试国际化
//        System.out.println("当前地区:" + Locale.getDefault());
//        System.out.println(getMessage("sayHi"));
//        Locale.setDefault(Locale.US);
//        System.out.println("当前地区:" + Locale.getDefault());
//        System.out.println(getMessage("sayHi"));

        // 路径转文件
        String path = "/usr/local/Cellar/openjdk/22.0.2/libexec/openjdk.jdk/Contents/Home/lib/src.zip!/java.base/java/lang/String.java";
        System.out.println(genFilenameFromPath(path));
    }

    /**
     * 文件路径转为文件名称
     *
     * @param classPath 路径
     * @return 文件名
     */
    public static @NotNull String genFilenameFromPath(@NotNull String classPath) {
        String result = "";
        String[] split = classPath.split("\\.");
        for (int i = 0; i < split.length-1; i++) {
            if(i == 0 ){
                result = result.concat(split[i]);
            } else {
                result = result.concat(".").concat(split[i]);
            }
        }
        String replace = result.replace("/", "_");
        return replace + ".json";
    }
}
