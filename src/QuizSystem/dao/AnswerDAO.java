/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import QuizSystem.entity.Answer;
import QuizSystem.utils.XJdbc;
import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author trant
 */
public class AnswerDAO {

    public void insert(Answer answer) throws SQLException {
        String sql = "INSERT INTO DapAn (NoiDung, DungSai, MaCH) VALUES (?, ?, ?)";
        XJdbc.update(sql, answer.getNoiDung(), answer.isDungSai(), answer.getMaCH());
    }

    public void remove(String maCH) throws SQLException {
        String sql = "DELETE FROM DapAN WHERE MaDA=?";
        XJdbc.update(sql, maCH);
    }

    public void repair(Answer answer) throws SQLException {
        String sql = "UPDATE DapAn SET NoiDung=?, DungSai=?, MaCH=? WHERE MaDA=?";
        XJdbc.update(sql, answer.getNoiDung(), answer.isDungSai(), answer.getMaCH(), answer.getMaDA());
    }

    public String selectByDungSai(int maCH) {
        try {
            String correct = "";
            String sql = "SELECT NoiDung FROM DapAn Where MaCH=? AND DungSai=1;";
            ResultSet rs = XJdbc.query(sql, maCH);
            while (rs.next()) {
                correct = correct.concat(rs.getString("NoiDung"));
            }
            return correct;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }

    public ArrayList<Answer> selectByMaCH(int maCH) {
        String sql = "SELECT * FROM DapAn WHERE MaCH=?";
        ArrayList<Answer> listAnswers = this.selectBySql(sql, maCH);
        return !listAnswers.isEmpty() ? listAnswers : null;
    }

    public ArrayList<Answer> selectBySql(String sql, Object... rest) {
        try {
            ArrayList<Answer> listAnswers = new ArrayList<>();
            ResultSet rs = XJdbc.query(sql, rest);
            while (rs.next()) {
                Answer answer = new Answer();
                answer.setMaDA(rs.getInt("MaDA"));
                answer.setNoiDung(rs.getString("NoiDung"));
                answer.setDungSai(rs.getBoolean("DungSai"));
                answer.setMaCH(rs.getInt("MaCH"));

                listAnswers.add(answer);
            }

            return listAnswers;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
}
