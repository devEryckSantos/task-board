package me.dio.ui;

import lombok.AllArgsConstructor;
import me.dio.persistence.entity.BoardEntity;

import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu {

    private final  BoardEntity entity;

    private final Scanner scanner = new Scanner(System.in);

    public void execute() {
        System.out.printf("Bem vindo ao board $s, selecione a operação desejada:", entity.getId());
        var option = -1;
        while (option != 9){
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
}

    private void createCard() {
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() {
    }

    private void showColumn() {
    }

    private void showCard() {
    }
