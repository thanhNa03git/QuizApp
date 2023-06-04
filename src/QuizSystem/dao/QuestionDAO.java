/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import QuizSystem.entity.Question;
import QuizSystem.utils.XJdbc;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author trant
 */
public class QuestionDAO {
    
    AnswerDAO answerDAO = new AnswerDAO();

    public void insert(Question question) throws SQLException {
        if (question.getHinhAnh() == null) {
            String sql = "INSERT INTO CauHoi (TenCH, LoaiCH, MaDe) VALUES (?, ?, ?)";
            XJdbc.update(sql, question.getTenCH(), question.isLoaiCH(), question.getMaDe());
        } else {
            String sql = "INSERT INTO CauHoi (TenCH, HinhAnh, LoaiCH, MaDe) VALUES (?, ?, ?, ?)";
            XJdbc.update(sql, question.getTenCH(), question.getHinhAnh(), question.isLoaiCH(), question.getMaDe());
        }
    }

    public void remove(int maCH) throws SQLException {
        String sql = "DELETE FROM CauHoi WHERE MaCH=?";
        XJdbc.update(sql, maCH);
    }

    public void repair(Question question) throws SQLException {
        if (question.getHinhAnh() == null) {
            String sql = "UPDATE CauHoi SET TenCH=?, LoaiCH=?, MaDe=? WHERE MaCH=?";
            XJdbc.update(sql, question.getTenCH(), question.isLoaiCH(), question.getMaDe(), question.getMaCH());
        } else {
            String sql = "UPDATE CauHoi SET TenCH=?, HinhAnh=?, LoaiCH=?, MaDe=? WHERE MaCH=?";
            XJdbc.update(sql, question.getTenCH(), question.getHinhAnh(), question.isLoaiCH(), question.getMaDe(), question.getMaCH());
        }
    }

    public Question selectQuestion() {
        String sql = "SELECT * FROM CauHoi WHERE MaCH = (SELECT MAX(MaCH) FROM CauHoi)";
        ArrayList<Question> listQuestions = this.selectBySql(sql);
        return listQuestions.get(0);
    }

    public Question selectByID(int maCH) {
        String sql = "SELECT * FROM CauHoi WHERE MaCH=?";
        ArrayList<Question> listQuestions = this.selectBySql(sql, maCH);
        return listQuestions.isEmpty() ? null : listQuestions.get(0);
    }

    public ArrayList<Question> selectByMaDe(String maDe) {
        String sql = "SELECT * FROM CauHoi WHERE MaDe=?";
        ArrayList<Question> listQuestions = this.selectBySql(sql, maDe);
        return listQuestions;
    }

    public ArrayList<Question> selectBySql(String sql, Object... rest) {
        try {
            ArrayList<Question> listQuestions = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, rest);
            while (rs.next()) {
                Question question = new Question();
                question.setMaCH(rs.getInt("MaCH"));
                question.setTenCH(rs.getString("TenCH"));
                question.setHinhAnh(rs.getBytes("HinhAnh"));
                question.setLoaiCH(rs.getBoolean("LoaiCH"));
                question.setMaDe(rs.getString("MaDe"));
                question.setAnswers(answerDAO.selectByMaCH(rs.getInt("MaCH")));

                listQuestions.add(question);
            }

            return listQuestions;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
}
