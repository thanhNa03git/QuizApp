/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.dao;

import java.sql.SQLException;

/**
 *
 * @author trant
 * @param <E> Entity
 * @param <T> KeyType
 */
public interface PersonDAO<E, T> {

    public void repair(E entity) throws SQLException;
}
