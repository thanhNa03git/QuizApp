/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.utils;

import QuizSystem.entity.Student;

/**
 *
 * @author trant
 */
public class AuthOfStudent {

    public static Student student = null;
    
    public static void clear() {
        AuthOfStudent.student = null;
    }

    public static String getInfo() {
        return "Sinh viên: " + AuthOfStudent.student.getTenSV();
    }
}
