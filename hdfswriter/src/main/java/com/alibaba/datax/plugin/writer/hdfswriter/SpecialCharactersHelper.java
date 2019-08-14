package com.alibaba.datax.plugin.writer.hdfswriter;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 特殊字符处理
 * 2019年08月13日 superz add
 */
public class SpecialCharactersHelper
{
    private List<String> specialCharacters;
    private String specialCharactersMode;

    public SpecialCharactersHelper(List<String> specialCharacters, String specialCharactersMode) {
        this.specialCharacters = specialCharacters;
        this.specialCharactersMode = specialCharactersMode;
    }

    public String deal(String origin) {
        if (null == specialCharacters || specialCharacters.size() == 0)
            return origin;

        if (null == origin || origin.length() == 0)
            return origin;

        String preOrigin = dealOrigin(origin);
        StringBuilder result = new StringBuilder();
        for (char c : preOrigin.toCharArray()) {
            dealChar(result, c);
        }

        return result.toString();
    }

    private String dealOrigin(String origin) {
        return origin // 目前用替换，此处需要收集
                .replace("    ", "\t")// 转换键盘输入的tab为Java中的 \t,一般 Java 中为 4 个空格
        ;
    }

    private void dealChar(StringBuilder sb, Character ch) {
        if (this.specialCharacters.contains(Character.toString(ch))) {
            if ("drop".equalsIgnoreCase(this.specialCharactersMode)) {
                return;
            }
            else if ("replace".equalsIgnoreCase(this.specialCharactersMode)) {
                if (ch < 32) {
                    switch (ch) {
                        case '\b':
                            sb.append('\\').append('b');
                            break;
                        case '\n':
                            sb.append('\\').append('n');
                            break;
                        case '\t':
                            sb.append('\\').append('t');
                            break;
                        case '\f':
                            sb.append('\\').append('f');
                            break;
                        case '\r':
                            sb.append('\\').append('r');
                            break;
                        default:
                            if (ch > 0xf) {
                                sb.append("\\u00").append(Integer.toHexString(ch));
                            }
                            else {
                                sb.append("\\u000").append(Integer.toHexString(ch));
                            }
                    }
                }
                else {
                    sb.append('\\').append(ch);
                }
            }
        }
        else {
            sb.append(ch);
        }

    }

    public static void main(String[] args) {
        SpecialCharactersHelper helper = new SpecialCharactersHelper(Arrays.asList("\t"), "replace");
        String ss = "我是测试语句，包含特殊符号：[\t],[\n],[\r],[\001],[    ],[\\],[/]";
        System.out.println(helper.deal(ss));
    }
}
