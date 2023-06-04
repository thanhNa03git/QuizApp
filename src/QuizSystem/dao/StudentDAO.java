/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import QuizSystem.entity.Student;
import QuizSystem.utils.XJdbc;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author trant
 */
public class StudentDAO implements PersonDAO<Student, String> {

    public void insert(Student st) throws SQLException {
        String sql = "INSERT INTO SinhVien values(?, ?, ?, ?, ?, ?)";
        XJdbc.update(sql, st.getMaSV(), st.getTenSV(), st.getTaiKhoan(), st.getMatKhau(), st.getNgaySinh(), st.getHinhAnh());
    }

    public void remove(String maSV) throws SQLException {
        String sql = "DELETE FROM SinhVien WHERE MaSV=?";
        XJdbc.update(sql, maSV);
    }

    @Override
    public void repair(Student st) throws SQLException {
        String sql = "UPDATE SinhVien SET TenSV=?, TaiKhoan=?, MatKhau=?, NgaySinh=?, HinhAnh=? WHERE MaSV=?";
        XJdbc.update(sql, st.getTenSV(), st.getTaiKhoan(), st.getMatKhau(), st.getNgaySinh(), st.getHinhAnh(), st.getMaSV());
    }

    public Student selectByTK(String taiKhoan) {
        String sql = "SELECT * FROM SinhVien WHERE TaiKhoan=?";
        ArrayList<Student> listStudents = this.selectBySql(sql, taiKhoan);
        return listStudents.isEmpty() ? null : listStudents.get(0);
    }

    public Student selectByID(String maSV) {
        String sql = "SELECT * FROM SinhVien WHERE MaSV=?";
        ArrayList<Student> listStudents = this.selectBySql(sql, maSV);
        return listStudents.isEmpty() ? null : listStudents.get(0);
    }

    public ArrayList<Student> selectAll() {
        String sql = "SELECT * FROM SinhVien";
        ArrayList<Student> listStudents = this.selectBySql(sql);
        return listStudents;
    }

    public ArrayList<Student> selectBySql(String sql, Object... rest) {
        try {
            ArrayList<Student> listStudents = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, rest);
            while (rs.next()) {
                Student student = new Student();
                student.setMaSV(rs.getString("MaSV"));
                student.setTenSV(rs.getString("TenSV"));
                student.setTaiKhoan(rs.getString("TaiKhoan"));
                student.setMatKhau(rs.getString("MatKhau"));
                student.setNgaySinh(rs.getDate("NgaySinh"));
                student.setHinhAnh(rs.getBytes("HinhAnh"));

                listStudents.add(student);
            }

            return listStudents;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
}
