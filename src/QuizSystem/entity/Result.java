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
public class Result {

    private int maKQ;
    private String maSV;
    private String tenSV;
    private String maDe;
    private String tenDe;
    private int soCauDung;
    private float diem;
    private int tongThoiGianThi;
    private Date ngayThi;

    public Result() {
    }

    public Result(int maKQ, String maSV, String tenSV, String maDe, String tenDe, int soCauDung, float diem, int tongThoiGianThi, Date ngayThi) {
        this.maKQ = maKQ;
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.maDe = maDe;
        this.tenDe = tenDe;
        this.soCauDung = soCauDung;
        this.diem = diem;
        this.tongThoiGianThi = tongThoiGianThi;
        this.ngayThi = ngayThi;
    }

    public int getMaKQ() {
        return maKQ;
    }

    public void setMaKQ(int maKQ) {
        this.maKQ = maKQ;
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

    public int getSoCauDung() {
        return soCauDung;
    }

    public void setSoCauDung(int soCauDung) {
        this.soCauDung = soCauDung;
    }

    public float getDiem() {
        return diem;
    }

    public void setDiem(float diem) {
        this.diem = diem;
    }

    public int getTongThoiGianThi() {
        return tongThoiGianThi;
    }

    public void setTongThoiGianThi(int tongThoiGianThi) {
        this.tongThoiGianThi = tongThoiGianThi;
    }

    public Date getNgayThi() {
        return ngayThi;
    }

    public void setNgayThi(Date ngayThi) {
        this.ngayThi = ngayThi;
    }
}
