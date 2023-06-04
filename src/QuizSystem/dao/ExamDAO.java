/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import QuizSystem.entity.Exam;
import QuizSystem.utils.XJdbc;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author trant
 */
public class ExamDAO {

    public void insert(Exam exam) throws SQLException {
        String sql = "INSERT INTO DeThi values (?, ?, ?, ?, ?, ?, ?)";
        XJdbc.update(sql, exam.getMaDe(), exam.getTenDe(), exam.getThoiGianBatDau(), exam.getThoiGianKetThuc(), exam.getThoiLuong(), exam.getSoLuongCH(), exam.getMaGV());
    }

    public void remove(String maDe) throws SQLException {
        String sql = "DELETE FROM DeThi WHERE MaDe=?";
        XJdbc.update(sql, maDe);
    }
    
    public void repair(Exam exam) throws SQLException {
        String sql = "UPDATE DeThi SET TenDe=?, ThoiGianBatDau=?, ThoiGianKetThuc=?, ThoiLuong=?, SoLuongCH=?, MaGV=? WHERE MaDe=?";
        XJdbc.update(sql, exam.getTenDe(), exam.getThoiGianBatDau(), exam.getThoiGianKetThuc(), exam.getThoiLuong(), exam.getSoLuongCH(), exam.getMaGV(), exam.getMaDe());
    }
    
    public Exam selectByID(String maDe) {
        String sql = "SELECT * FROM DeThi WHERE MaDe=?";
        ArrayList<Exam> listExams = this.selectBySql(sql, maDe);
        return listExams.isEmpty() ? null : listExams.get(0);
    }
    
    public ArrayList<Exam> selectAll() {
        String sql = "SELECT * FROM DeThi";
        ArrayList<Exam> listExams = this.selectBySql(sql);
        return listExams;
    }

    public ArrayList<Exam> selectBySql(String sql, Object... rest) {
        try {
            ArrayList<Exam> listExams = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, rest);
            while (rs.next()) {
                Exam exam = new Exam();
                exam.setMaDe(rs.getString("MaDe"));
                exam.setTenDe(rs.getString("TenDe"));
                exam.setThoiGianBatDau(rs.getTimestamp("ThoiGianBatDau"));
                exam.setThoiGianKetThuc(rs.getTimestamp("ThoiGianKetThuc"));
                exam.setThoiLuong(rs.getInt("ThoiLuong"));
                exam.setSoLuongCH(rs.getInt("SoLuongCH"));
                exam.setMaGV(rs.getString("MaGV"));
                
                listExams.add(exam);
            }
            
            return listExams;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
}
