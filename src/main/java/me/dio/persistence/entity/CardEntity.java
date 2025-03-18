package me.dio.persistence.entity;

import lombok.Data;

@Data
public class CardEntity {

    private Long id;
    private String tittle;
    private String description;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();

}
