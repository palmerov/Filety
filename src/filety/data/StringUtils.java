package filety.data;


import java.util.regex.Matcher;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author palmerovich
 */
public class StringUtils {

    public static String toRead(String str) {
        return str.replaceAll("\n", Matcher.quoteReplacement("\\n"));
    }

    public static String toWork(String str) {
        return str.replaceAll(Matcher.quoteReplacement("\\n"), Matcher.quoteReplacement("\n"));
    }
}
