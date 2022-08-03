package nl.saxion.quentin.myzooremastered.ui.home.games;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ui.home.GamesActivity;

/**
 * Four in a Row game
 * <p>
 * Extremely messy bot
 */
public class FourInARowActivity extends AppCompatActivity {
    private int coins;
    private double xp;

    private int[] board;
    private int[] winButtons;
    private int win;
    private boolean bot;
    private boolean playerTurn;
    private boolean gameOver;

    private List<String> mBoard;
    private ArrayAdapter<String> adapter;
    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_in_a_row);

        playerTurn = true;
        board = new int[42];
        for (int i = 0; i < 42; i++) board[i] = i;
        mBoard = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            mBoard.add("");
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBoard) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position % 2 == 0)
                    view.setBackgroundColor(Color.argb(50, 211, 211, 211));
                else
                    view.setBackgroundColor(Color.TRANSPARENT);

                if (winButtons != null) {
                    int red;
                    int green;
                    if (board[winButtons[0]] == 1) {
                        red = 0;
                        green = 255;
                    } else {
                        red = 255;
                        green = 0;
                    }

                    for (Integer i : winButtons) {
                        if (position == i) {
                            view.setBackgroundColor(Color.argb(50, red, green, 0));
                        }
                    }
                }

                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setTextSize(24);
                return view;
            }
        };

        grid = findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doTurn(position);

                if (gameOver) return;

                if (bot) {
                    doTurn(getBestMove());
                }
            }
        });
    }

    private void doTurn(int position) {
        if ((position = placement(position, board)) == -1) return;

        mBoard.set(position, playerTurn ? "X" : "O");
        board[position] = playerTurn ? 1 : 2;
        playerTurn = !playerTurn;

        checkGameOver();

        adapter.notifyDataSetChanged();
    }

    private void checkGameOver() {
        gameOver = false;
        if (draw(board)) {
            gameOver = true;
        } else if (won(board)) {
            adapter.notifyDataSetChanged();
            gameOver = true;
        }

        if (gameOver) {
            // If I won against bot give coins
            if (board[winButtons[0]] == 1 && bot) {
                coins += 15;
                xp += 10;

                Intent data = new Intent();
                data.putExtra("coins", coins);
                data.putExtra("xp", xp);
                setResult(GamesActivity.COINS_AND_EXPERIENCE, data);
            }
            findViewById(R.id.gameOver).setVisibility(View.VISIBLE);
            grid.setEnabled(false);
        }
    }

    private boolean draw(int[] board) {
        for (int i = 0; i < 9; i++) {
            if (board[i] == i) {
                return false;
            }
        }
        return true;
    }

    private int placement(int input, int[] board) {
        input %= 7;
        int i = input + 35;
        while (board[i] != i) {
            if (i == input) return -1;
            i -= 7;
        }
        return i;
    }

    public void clear(View view) {
        findViewById(R.id.gameOver).setVisibility(View.INVISIBLE);
        grid.setEnabled(true);
        playerTurn = true;
        board = new int[42];
        for (int i = 0; i < 42; i++) board[i] = i;
        winButtons = null;

        mBoard.clear();
        for (int i = 0; i < 42; i++) {
            mBoard.add("");
        }

        adapter.notifyDataSetChanged();
    }

    public void bot(View view) {
        Button botButton = (Button) view;
        if (botButton.getText().equals(getString(R.string.play_with_bot)))
            botButton.setText(R.string.stop_bot);
        else
            botButton.setText(R.string.play_with_bot);

        bot = !bot;

        clear(null);
    }

    private boolean won(int[] board) {
        for (int i = 0; i < 42; i += 7) {
            for (int j = i; j < i + 4; j++) {
                if ((board[j] == board[j + 1] && board[j + 1] == board[j + 2] && board[j + 2] == board[j + 3])) {
                    win = board[j];
                    winButtons = new int[]{j, j + 1, j + 2, j + 3};
                    return true;
                }
            }
        }
        int[] vertical = {0, 7, 14, 21, 28, 35};
        for (int i = 0; i < 7; i++) {
            int k = 0;
            for (int j = 0; j < 3; j++) {
                if ((board[i + vertical[k]] == board[i + vertical[k + 1]] && board[i + vertical[k + 1]] == board[i + vertical[k + 2]] && board[i + vertical[k + 2]] == board[i + vertical[k + 3]])) {
                    win = board[i + vertical[k]];
                    winButtons = new int[]{i + vertical[k], i + vertical[k + 1], i + vertical[k + 2], i + vertical[k + 3]};
                    return true;
                }
                k++;
            }
        }
        int[] diagonal = {14, 15, 16, 17};
        for (int j = 0; j < 4; j++) {
            for (int i = j; i <= diagonal[j]; i += 7) {
                if ((board[i] == board[i + 8] && board[i + 8] == board[i + 16] && board[i + 16] == board[i + 24])) {
                    win = board[i];
                    winButtons = new int[]{i, i + 8, i + 16, i + 24};
                    return true;
                }
            }
        }
        int[] diagonal2 = {20, 19, 18, 17};
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= diagonal2[i]; j += 7) {
                if ((board[6 + j - i] == board[12 + j - i] && board[12 + j - i] == board[18 + j - i] && board[18 + j - i] == board[24 + j - i])) {
                    win = board[6 + j - i];
                    winButtons = new int[]{6 + j - i, 12 + j - i, 18 + j - i, 24 + j - i};
                    return true;
                }
            }
        }
        return false;
    }

    private int getBestMove() {
        int move = -1;

        for (int i = 0; i < 50; i++) {
            int[] botBoard = Arrays.copyOf(board, 42);
            int p1 = bot(botBoard);
            if (p1 == -1) p1 = getRandom(botBoard);
            botBoard[placement(p1, botBoard)] = 2;
            move = p1;
            if (won(botBoard) && win == 2) {
                break;
            }
            if (!draw(botBoard)) {
                int p2 = bot(botBoard);
                if (p2 == -1) p2 = getRandom(botBoard);
                botBoard[placement(p2, botBoard)] = 1;
                if (!draw(botBoard)) {
                    int p3 = bot(botBoard);
                    if (p3 == -1) p3 = getRandom(botBoard);
                    botBoard[placement(p3, botBoard)] = 2;
                    if (won(botBoard) && win == 2) {
                        break;
                    }
                    if (!draw(botBoard)) {
                        int p4 = bot(botBoard);
                        if (p4 == -1) p4 = getRandom(botBoard);
                        botBoard[placement(p4, botBoard)] = 1;

                        int p5 = bot(botBoard);
                        if (p5 == -1) p5 = getRandom(botBoard);
                        botBoard[placement(p5, botBoard)] = 2;
                        if (won(botBoard) && win == 2) {
                            break;
                        }

                    }
                }
            }
        }

        winButtons = null;
        return move;
    }

    private int bot(int[] board) {
        // Horizontal
        for (int i = 0; i < 42; i += 7) {
            for (int j = i; j < i + 4; j++) {
                boolean one = board[j] == board[j + 1];
                boolean two = board[j + 1] == board[j + 2];
                boolean three = board[j + 2] == board[j + 3];
                boolean four = board[j] == board[j + 3];
                if (one == two && one) {    // 1 1 1 0
                    if (j + 3 == placement(j + 3, board)) {
                        return j + 3;
                    }
                }
                if (two == three && two) {  // 0 1 1 1
                    if (j == placement(j, board)) {
                        return j;
                    }
                }
                if (one && four) {  // 1 1 0 1
                    if (j + 2 == placement(j + 2, board)) {
                        return j + 2;
                    }
                }
                if (three && four) { // 1 0 1 1
                    if (j + 1 == placement(j + 1, board)) {
                        return j + 1;
                    }
                }
            }
        }

        // Vertical
        int[] vertical = {0, 7, 14, 21, 28, 35};
        for (int i = 0; i < 7; i++) {
            int k = 0;
            for (int j = 0; j < 4; j++) {
                if ((board[i + vertical[k]] == board[i + vertical[k + 1]] && board[i + vertical[k + 1]] == board[i + vertical[k + 2]])) {
                    if ((i + vertical[k]) - 7 > 0 && board[i + vertical[k] - 7] == ((i + vertical[k]) - 7)) {
                        return (i + vertical[k]) - 7;
                    }
                }
                k++;
            }
        }

        // Diagonal NORTH_WEST
        int[] diagonal1 = {14, 15, 16, 17};
        for (int j = 0; j < 4; j++) {
            for (int i = j; i <= diagonal1[j]; i += 7) {
                boolean one = board[i] == board[i + 8];
                boolean two = board[i + 8] == board[i + 16];
                boolean three = board[i + 16] == board[i + 24];
                boolean four = board[i] == board[i + 24];
                if (one == two && one) {   // 1 1 1 0 == true
                    if (i + 24 == placement(i + 24, board)) {
                        return i + 24;
                    }
                }
                if (two == three && two) { // 0 1 1 1 == true
                    if (i == placement(i, board)) {
                        return i;
                    }
                }
                if (one && four) {  // 1 1 0 1 == true
                    if (i + 16 == placement(i + 16, board)) {
                        return i + 16;
                    }
                }
                if (three && four) {// 1 0 1 1 == true
                    if (i + 8 == placement(i + 8, board)) {
                        return i + 8;
                    }
                }
            }
        }

        // Diagonal NORTH_EAST
        int[] diagonal2 = {20, 19, 18, 17};
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= diagonal2[i]; j += 7) {
                boolean one = board[6 + j - i] == board[12 + j - i];
                boolean two = board[12 + j - i] == board[18 + j - i];
                boolean three = board[18 + j - i] == board[24 + j - i];
                boolean four = board[6 + j - i] == board[24 + j - i];
                if (one == two && one) {   // 1 1 1 0 == true
                    if (24 + j - i == placement(24 + j - i, board)) {
                        return 24 + j - i;
                    }
                }
                if (two == three && two) { // 0 1 1 1 == true
                    if (6 + j - i == placement(6 + j - i, board)) {
                        return 6 + j - i;
                    }
                }
                if (one && four) {  // 1 1 0 1 == true
                    if (18 + j - i == placement(18 + j - i, board)) {
                        return 18 + j - i;
                    }
                }
                if (three && four) {// 1 0 1 1 == true
                    if (12 + j - i == placement(12 + j - i, board)) {
                        return 12 + j - i;
                    }
                }
            }
        }

        return -1;
    }

    public int getRandom(int[] board) {
        int random;
        int placement;
        do {
            random = (int) (Math.random() * 7);
            placement = placement(random, board);
        } while (placement == -1);
        return random;
    }
}
