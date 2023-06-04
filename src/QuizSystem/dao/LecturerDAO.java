/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import QuizSystem.entity.Lecturer;
import QuizSystem.utils.XJdbc;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author trant
 */
public class LecturerDAO implements PersonDAO<Lecturer, String> {

    @Override
    public void repair(Lecturer entity) throws SQLException {
        String sql = "UPDATE GiangVien SET MatKhau=? WHERE MaGV=?";
        XJdbc.update(sql, entity.getMatKhau(), entity.getMaGV());
    }

    public Lecturer selectByTK(String taiKhoan) {
        String sql = "SELECT * FROM GiangVien WHERE TaiKhoan=?";
        ArrayList<Lecturer> listLecturers = this.selectBySql(sql, taiKhoan);
        return listLecturers.isEmpty() ? null : listLecturers.get(0);
    }

    public ArrayList<Lecturer> selectBySql(String sql, Object... rest) {
        try {
            ArrayList<Lecturer> listLecturers = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, rest);
            while (rs.next()) {
                Lecturer lecturer = new Lecturer();
                lecturer.setMaGV(rs.getString("MaGV"));
                lecturer.setTenGV(rs.getString("TenGV"));
                lecturer.setTaiKhoan(rs.getString("TaiKhoan"));
                lecturer.setMatKhau(rs.getString("MatKhau"));
                lecturer.setNgaySinh(rs.getDate("NgaySinh"));
                lecturer.setDonViCongTac(rs.getString("DonViCongTac"));

                listLecturers.add(lecturer);
            }
            return listLecturers;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
}
