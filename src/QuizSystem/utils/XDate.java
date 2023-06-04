/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author trant
 */
public class XDate {

    public static SimpleDateFormat formatDate = new SimpleDateFormat();

    public static String toString(String pattern, Date date) {
        formatDate.applyPattern(pattern);
        return formatDate.format(date);
    }

    public static Date toDate(String pattern, String date) {
        try {
            formatDate.applyPattern(pattern);
            return formatDate.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException();
        }
    }
}
