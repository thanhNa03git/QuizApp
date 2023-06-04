package QuizSystem.entity;

import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author trant
 */
public class Question {

    private int maCH;
    private String tenCH;
    private byte[] hinhAnh;
    private boolean loaiCH;
    private String maDe;
    private ArrayList<Answer> answers;

    public Question() {
    }

    public Question(int maCH, String tenCH, byte[] hinhAnh, boolean loaiCH, String maDe, ArrayList<Answer> answers) {
        this.maCH = maCH;
        this.tenCH = tenCH;
        this.hinhAnh = hinhAnh;
        this.loaiCH = loaiCH;
        this.maDe = maDe;
        this.answers = answers;
    }

    public int getMaCH() {
        return maCH;
    }

    public void setMaCH(int maCH) {
        this.maCH = maCH;
    }

    public String getTenCH() {
        return tenCH;
    }

    public void setTenCH(String tenCH) {
        this.tenCH = tenCH;
    }

    public byte[] getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(byte[] hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public boolean isLoaiCH() {
        return loaiCH;
    }

    public void setLoaiCH(boolean loaiCH) {
        this.loaiCH = loaiCH;
    }

    public String getMaDe() {
        return maDe;
    }

    public void setMaDe(String maDe) {
        this.maDe = maDe;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
    
    public Object[] getRecord() {
        return new Object[] {this.getMaCH(), this.getTenCH(), this.isLoaiCH() ? "Nhiều lựa chọn" : "Một lựa chọn"};
    }
}
