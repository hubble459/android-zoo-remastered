package nl.saxion.quentin.myzooremastered.ui.home.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.ui.home.GamesActivity;

/**
 * Tic Tac Toe game with bot
 * cba to comment as this is unneeded for the project
 */
public class TicTacToeActivity extends AppCompatActivity {
    private final int BOT_LOOP = 50;
    private TextView tv;
    private String[] board = new String[]{"", "", "", "", "", "", "", "", ""};
    private String player = "X";
    private boolean finished;
    private boolean bot;
    private int setCount;
    private int coins;
    private int xp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        tv = findViewById(R.id.winner);
    }

    public void clickButton(View view) {
        if (finished) return;

        int input = Integer.parseInt((String) view.getTag());
        if (board[input].equals("")) {
            Button button = (Button) view;
            button.setText(player);

            board[input] = player;
            setCount++;

            String gameOver = gameOver();
            if (gameOver != null) {
                tv.setText(gameOver);
                finished = true;
            } else {
                switchTurn();
                if (bot) {
                    input = doBotTurn(board, "O");
                    if (input == -1) {
                        do {
                            input = new Random().nextInt(9);
                        } while (!board[input].equals(""));
                    }
                    board[input] = "O";
                    setCount++;
                    botInput(input, "O");

                    gameOver = gameOver();
                    if (gameOver != null) {
                        tv.setText(gameOver);
                        finished = true;

                        // if bot, award points
                        if (bot) {
                            coins += 3;
                            xp += 1;
                            setResult();
                        }
                    }

                    switchTurn();
                }
            }
        }
    }

    public void stop(View view) {
        finish();
    }

    public void restart(View view) {
        tv.setText("");
        player = "X";
        board = new String[]{"", "", "", "", "", "", "", "", ""};
        finished = false;
        setCount = 0;
        for (int i = 0; i < 9; i++) {
            botInput(i, "");
        }
    }

    public void playWithBot(View view) {
        bot = !bot;
        Button button = (Button) view;
        String botText;
        if (bot) botText = "Stop Bot";
        else botText = "Play with Bot";
        button.setText(botText);
        restart(view);
    }

    private boolean draw() {
        return setCount == 9;
    }

    private String win(String[] board) {
        if (!board[0].equals("") && (board[0].equals(board[3]) && board[0].equals(board[6])))
            return board[0];
        if (!board[1].equals("") && (board[1].equals(board[4]) && board[1].equals(board[7])))
            return board[1];
        if (!board[2].equals("") && (board[2].equals(board[5]) && board[2].equals(board[8])))
            return board[2];
        if (!board[0].equals("") && (board[0].equals(board[1]) && board[0].equals(board[2])))
            return board[0];
        if (!board[3].equals("") && (board[3].equals(board[4]) && board[3].equals(board[5])))
            return board[3];
        if (!board[6].equals("") && (board[6].equals(board[7]) && board[6].equals(board[8])))
            return board[6];
        if (!board[0].equals("") && (board[0].equals(board[4]) && board[0].equals(board[8])))
            return board[0];
        if (!board[2].equals("") && (board[2].equals(board[4]) && board[2].equals(board[6])))
            return board[2];
        return "";
    }

    private String gameOver() {
        if (win(board).equals("X")) {
            if (bot) CoinsAndFood.addCoins(5);
            return "You Win!";
        }
        if (win(board).equals("O")) return "Opponent Wins!";
        if (draw()) return "It's a Draw!";
        return null;
    }

    private void switchTurn() {
        if (player.equals("X")) player = "O";
        else player = "X";
    }

    private int doBotTurn(String[] board, String player) {
        String[] botBoard = new String[9];
        System.arraycopy(board, 0, botBoard, 0, board.length);

        for (int i = 0; i < BOT_LOOP; i++) {
            int input = new Random().nextInt(9);
            if (board[input].equals("")) {
                botBoard[input] = player;
                if (win(botBoard).equals(player)) return input;

                int input2 = doBotTurn(botBoard, player.equals("X") ? "O" : "X");
                if (input2 != -1) return input2;
            }
        }

        return -1;
    }

    private void botInput(int input, String player) {
        Button button = null;
        switch (input) {
            case 0:
                button = findViewById(R.id.button1);
                break;
            case 1:
                button = findViewById(R.id.button2);
                break;
            case 2:
                button = findViewById(R.id.button3);
                break;
            case 3:
                button = findViewById(R.id.button4);
                break;
            case 4:
                button = findViewById(R.id.button5);
                break;
            case 5:
                button = findViewById(R.id.button6);
                break;
            case 6:
                button = findViewById(R.id.button7);
                break;
            case 7:
                button = findViewById(R.id.button8);
                break;
            case 8:
                button = findViewById(R.id.button9);
                break;
        }

        if (button != null) {
            button.setText(player);
        }
    }

    private void setResult() {
        Intent data = new Intent();
        data.putExtra("coins", coins);
        data.putExtra("xp", xp);
        setResult(GamesActivity.COINS_AND_EXPERIENCE, data);
    }
}
