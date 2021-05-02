package com.test.service;

import com.test.exception.InvalidGameException;
import com.test.exception.InvalidParamException;
import com.test.exception.NotFoundException;
import com.test.model.Game;
import com.test.model.GamePlay;
import com.test.model.Player;
import com.test.model.TicToe;
import com.test.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.test.model.GameStatus.*;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(Player player, int size) {
        Game game = new Game();
        game.setBoard(new int[size][size]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);
        game.setSize(size);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToGame(Player player2, String gameId, int size) throws InvalidParamException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided id doesn't exist");
        }
        Game game = GameStorage.getInstance().getGames().get(gameId);

        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game is not valid anymore");
        }

        if (game.getSize() != size) {
            throw new InvalidGameException("Wrong Game Size");
        }

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(Player player2, int size) throws NotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW) && it.getSize() == size)
                .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();

        Boolean draw = getDraw(game.getBoard());
        int winner = getWinner(game.getBoard());
        if (winner == TicToe.X.getValue()) {
            game.setWinner(TicToe.X);
            game.setStatus(FINISHED);
        } else if (winner == TicToe.O.getValue()) {
            game.setWinner(TicToe.O);
            game.setStatus(FINISHED);
        }
        else if(draw){
            game.setWinner(TicToe.D);
            game.setStatus(FINISHED);
        }

        return game;
    }


    public int getWinner(int[][] board) {
        int winner = 0;
        boolean isWon = false;
        int check = -1;

        // check columns (same x)
        for (int x = 0; x < board.length; x++) {
            int value = board[x][0];

            if (value == 0) {
                continue;
            }
            for (int y = 1; y < board[x].length; y++) {
                int current = board[x][y];
                if (current == 0 || current != value) {
                    check = 0;
                    break;
                }
                if (y == board[x].length -1) {
                    isWon = true;
                    winner = value;
                }
            }
            if(isWon) {
                break;
            }
        }

        if (! isWon) {
            // check rows (same y)

            for (int y = 0; y < board[0].length; y++) {
                int value = board[0][y];
                if (value == 0) {
                    continue;
                }
                for (int x = 1; x < board.length; x++) {
                    int current = board[x][y];
                    if (current == 0 || current !=value) {
                        break;
                    }
                    if (x == board.length -1) {
                        isWon = true;
                        winner = value;
                    }
                }
                if(isWon) {
                    break;
                }
            }

        }
        if (! isWon) {
            // check diagonal (bottom left to top right

            int value = board[0][0];
            if (value != 0) {
                for (int i = 1; i < board.length; i++) {
                    if (board[i][i] != value) {
                        break;
                    }
                    if (i == board.length -1) {
                        isWon = true;
                        winner = value;
                    }
                }
            }
        }

        if (! isWon) {
            // check anti-diagonal (top left to bottom right)
            int length = board.length;
            int value = board[0][length-1];
            if (value != 0) {
                for (int i = 1; i < length; i++) {
                    if (board[i][length-i-1] != value) {
                        break;
                    }
                    if (i == length -1) {
                        winner = value;
                    }
                }
            }
        }
        return winner;
    }

    public boolean getDraw(int[][] board) {
        boolean status = false;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                int current = board[x][y];
                if (current == 0) {
                    return false;
                } else {
                    status = true;
                }
            }
        }
        return status;
    }
}
