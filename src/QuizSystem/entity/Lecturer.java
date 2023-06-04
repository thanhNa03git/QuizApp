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
public class Lecturer extends Person{

    private String maGV;
    private String tenGV;
    private Date ngaySinh;
    private String donViCongTac;

    public Lecturer() {
    }

    public Lecturer(String maGV, String tenGV, String taiKhoan, String matKhau, Date ngaySinh, String donViCongTac) {
        super(taiKhoan, matKhau);
        this.maGV = maGV;
        this.tenGV = tenGV;
        this.ngaySinh = ngaySinh;
        this.donViCongTac = donViCongTac;
    }

    public String getMaGV() {
        return maGV;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    public String getTenGV() {
        return tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDonViCongTac() {
        return donViCongTac;
    }

    public void setDonViCongTac(String donViCongTac) {
        this.donViCongTac = donViCongTac;
    }
}
