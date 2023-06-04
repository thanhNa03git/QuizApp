/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import QuizSystem.entity.Result;
import QuizSystem.utils.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author trant
 */
public class ResultDAO {

    public void insert(Result result) throws SQLException {
        String sql = "INSERT INTO KetQua (MaSV, TenSV, MaDe, TenDe, SoCauDung, Diem, TongThoiGianThi, NgayThi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        XJdbc.update(sql, result.getMaSV(), result.getTenSV(), result.getMaDe(), result.getTenDe(), result.getSoCauDung(), result.getDiem(), result.getTongThoiGianThi(), result.getNgayThi());
    }

    // Kiểm tra học sinh đã từng làm bài chưa?
    public boolean selectExist(String maSV, String maDe) {
        String sql = "SELECT * FROM KetQua WHERE MaSV=? AND MaDe=?";
        ArrayList<Result> list = this.selectBySql(sql, maSV, maDe);
        return list.isEmpty();
    }
    
    public ArrayList<Result> selectByMaDe(String maDe) {
        String sql = "SELECT * FROM KetQua WHERE MaDe=?";
        ArrayList<Result> list = this.selectBySql(sql, maDe);
        return list.isEmpty() ? new ArrayList<>() : list;
    }

    public ArrayList<Result> selectByMaSV(String maSV) {
        String sql = "SELECT * FROM KetQua WHERE MaSV=?";
        ArrayList<Result> list = this.selectBySql(sql, maSV);
        return list.isEmpty() ? new ArrayList<>() : list;
    }

    public ArrayList<Result> selectBySql(String sql, Object... rest) {
        try {
            ArrayList<Result> listResults = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, rest);
            while (rs.next()) {
                Result result = new Result();
                result.setMaKQ(rs.getInt("MaKQ"));
                result.setMaSV(rs.getString("MaSV"));
                result.setTenSV(rs.getString("TenSV"));
                result.setMaDe(rs.getString("MaDe"));
                result.setTenDe(rs.getString("TenDe"));
                result.setSoCauDung(rs.getInt("SoCauDung"));
                result.setDiem(rs.getFloat("Diem"));
                result.setTongThoiGianThi(rs.getInt("TongThoiGianThi"));
                result.setNgayThi(rs.getDate("NgayThi"));

                listResults.add(result);
            }

            return listResults;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
}
