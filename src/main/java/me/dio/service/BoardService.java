package me.dio.service;

import lombok.AllArgsConstructor;
import me.dio.persistence.dao.BoardDAO;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
@Service
public class BoardService {

    private final Connection connection;

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            if(!dao.exists(id)) {
                return false;
            }
            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
