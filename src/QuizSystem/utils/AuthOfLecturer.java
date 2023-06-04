/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.utils;

import QuizSystem.entity.Lecturer;

/**
 *
 * @author trant
 */
public class AuthOfLecturer {

    public static Lecturer lecturer = null;

    public static void clear() {
        AuthOfLecturer.lecturer = null;
    }

    public static String getInfo() {
        return "Giảng viên: " + AuthOfLecturer.lecturer.getTenGV()+ " | " + "ĐVCT: " + AuthOfLecturer.lecturer.getDonViCongTac();
    }
}
