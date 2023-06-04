/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.entity;

import java.util.Date;

/**
 *
 * @author trant
 */
public class Exam {

    private String maDe;
    private String tenDe;
    private Date thoiGianBatDau;
    private Date thoiGianKetThuc;
    private int thoiLuong;
    private int soLuongCH;
    private String maGV;

    public Exam() {
    }

    public Exam(String maDe, String tenDe, Date thoiGianBatDau, Date thoiGianKetThuc, int thoiLuong, int soLuongCH, String maGV) {
        this.maDe = maDe;
        this.tenDe = tenDe;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.thoiLuong = thoiLuong;
        this.soLuongCH = soLuongCH;
        this.maGV = maGV;
    }

    public String getMaDe() {
        return maDe;
    }

    public void setMaDe(String maDe) {
        this.maDe = maDe;
    }

    public String getTenDe() {
        return tenDe;
    }

    public void setTenDe(String tenDe) {
        this.tenDe = tenDe;
    }

    public Date getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(Date thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public Date getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(Date thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public int getThoiLuong() {
        return thoiLuong;
    }

    public void setThoiLuong(int thoiLuong) {
        this.thoiLuong = thoiLuong;
    }

    public int getSoLuongCH() {
        return soLuongCH;
    }

    public void setSoLuongCH(int soLuongCH) {
        this.soLuongCH = soLuongCH;
    }

    public String getMaGV() {
        return maGV;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    @Override
    public String toString() {
        return this.getMaDe() + " - " + this.getTenDe();
    }
}
