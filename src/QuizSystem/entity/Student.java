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
public class Student extends Person {

    private String maSV;
    private String tenSV;
    private Date ngaySinh;
    private byte[] hinhAnh;

    public Student() {
    }

    public Student(String maSV, String tenSV, String taiKhoan, String matKhau, Date ngaySinh, byte[] hinhAnh) {
        super(taiKhoan, matKhau);
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.ngaySinh = ngaySinh;
        this.hinhAnh = hinhAnh;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getTenSV() {
        return tenSV;
    }

    public void setTenSV(String tenSV) {
        this.tenSV = tenSV;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public byte[] getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(byte[] hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public Object[] record() {
        return new Object[]{this.getMaSV(), this.tenSV, this.getTaiKhoan(), this.getMatKhau(), this.getNgaySinh()};
    }
}
