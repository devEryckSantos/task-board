package me.dio.ui;

import lombok.AllArgsConstructor;
import me.dio.dto.BoardColumnInfoDTO;
import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.BoardEntity;
import me.dio.persistence.entity.CardEntity;
import me.dio.service.BoardColumnQueryService;
import me.dio.service.BoardQueryService;
import me.dio.service.CardQueryService;
import me.dio.service.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static me.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final BoardEntity entity;

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() {
        try {

            System.out.printf("Bem vindo ao board %s, selecione a operação desejada:\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card.");
                System.out.println("2 - Mover um card.");
                System.out.println("3 - Bloquear um card.");
                System.out.println("4 - Desbloquear um card.");
                System.out.println("5 - Cancelar um card.");
                System.out.println("6 - Visualizar board.");
                System.out.println("7 - Visualizar colunas com cards.");
                System.out.println("8 - Visualizar card.");
                System.out.println("9 - Voltar para o Menu anterior.");
                System.out.println("0 - Sair.");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para o Menu anterior");
                    case 0 -> System.exit(0);
                    default -> System.out.println("OPÇÃO INVÁLIDA! Escolha uma opção do menu.");

                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Inforome o título do card");
        card.setTittle(scanner.next());
        System.out.println("Inforome a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()){
            new CardService(connection).create(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException{
        System.out.println("Informe o id do card que deseja mover para a próxima coluna.");
        var cardId = scanner.nextLong();
        var boardColumnsIndo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsIndo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException{
        System.out.println("Informe o id do card que será bloqueado.");
        var cardId = scanner.nextLong();
        System.out.println("Iforme o motivo do bloqueio do card.");
        var reason = scanner.next();
        var boardColumnsIndo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsIndo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException{
        System.out.println("Informe o id do card que será desbloqueado.");
        var cardId = scanner.nextLong();
        System.out.println("Iforme o motivo do desbloqueio do card.");
        var reason = scanner.next();
        try(var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason  );
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException{
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento.");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsIndo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(),boardColumnsIndo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                    System.out.printf("Coluna: [%s] tipo: [%s] tem %s cards.\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void showColumn() throws SQLException{
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)){
            System.out.printf("Escolha uma coluna do board %s:\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s\n",
                        ca.getId(), ca.getTittle(), ca.getDescription()));
            });

        }
    }

    private void showCard() throws SQLException{
        System.out.println("Informe o id do card que deseja visualizar.");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c-> {
                        System.out.printf("Card %s - %s.\n", c.id(), c.title());
                        System.out.printf("Descrição: %s\n", c.description());
                        System.out.println(c.blocked() ? "Está bloqueado. Motivo:" + c.blockedReason() : "Não está bloqueado.");
                        System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                        System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));

        }
    }
}