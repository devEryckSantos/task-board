package me.dio.persistence.dao;

import lombok.AllArgsConstructor;
import me.dio.dto.CardDetails;

import java.sql.Connection;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public CardDetails findById(final Long id){
        return null;
    }
}
