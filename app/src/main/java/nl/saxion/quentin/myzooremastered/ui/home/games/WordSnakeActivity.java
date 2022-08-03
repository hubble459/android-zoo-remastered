package nl.saxion.quentin.myzooremastered.ui.home.games;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ui.home.GamesActivity;

/**
 * Word Snake game
 */
public class WordSnakeActivity extends AppCompatActivity {
    private int time = 20;
    private int score;
    private int coins;
    private double xp;
    private List<String> words;
    private List<String> wordLog;
    private TextView tvTimer;
    private TextView tvScore;
    private EditText etWord;
    private CountDownTimer countDownTimer;

    private ArrayAdapter<String> adapter;

    /**
     * Starts the Word Snake Game
     *
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_snake);

        int language = getIntent().getIntExtra("language", -1);
        if (language == -1) finish();

        wordLog = new ArrayList<>();
        tvTimer = findViewById(R.id.timer);
        tvScore = findViewById(R.id.score);

        if (savedInstanceState != null) reset();

        ListView wordLogList = findViewById(R.id.wordLogList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wordLog) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position % 2 == 0) {
                    view.setBackgroundColor(Color.argb(50, 255, 0, 0));
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                return view;
            }
        };
        wordLogList.setAdapter(adapter);

        // Get WordList
        getWordList(language);
        // get a Word from WordList
        String startingWord = getRandomWord();

        wordLog.add(startingWord);

        etWord = findViewById(R.id.word);
        etWord.setHint(startingWord.charAt(startingWord.length() - 1) + "");
        etWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String word = etWord.getText().toString().replace(" ", "").toLowerCase();
                if (!isCorrect(word)) {
                    return true;
                }
                countDownTimer.cancel();

                word = capitalize(word);
                wordLog.add(word);
                adapter.notifyDataSetChanged();

                word = word.toLowerCase();
                score += countWordPoints(word);
                tvScore.setText(String.format(getString(R.string.score), score));

                word = capitalize(getBotWord(word.charAt(word.length() - 1)));
                wordLog.add(word);
                word = word.toLowerCase();
                etWord.setHint(word.charAt(word.length() - 1) + "");
                etWord.setText("");

                countDownTimer((time += 5) * 1000);
                return true;
            }
        });

        countDownTimer(20000);
    }

    /**
     * Does the word given follow the rules of Word Snake
     *
     * @param word word to check
     * @return is correct
     */
    private boolean isCorrect(String word) {
        if (word.length() == 0) return false;
        if (word.charAt(0) != etWord.getHint().charAt(0)) return false;
        if (!words.contains(word)) return false;
        if (wordLog.contains(capitalize(word))) return false;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < 'a' || c > 'z') return false;
        }
        return true;
    }

    /**
     * Capitalize first letter of word for aesthetics
     *
     * @param word word to capatilze
     * @return capitalized word
     */
    private String capitalize(String word) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (i == 0) result.append(String.valueOf(word.charAt(i)).toUpperCase());
            else result.append(word.charAt(i));
        }
        return result.toString();
    }

    /**
     * Give points based on word
     *
     * @param word word to grade
     */
    private int countWordPoints(String word) {
        int score = 0;
        score += word.length() * 10;
        if (word.contains("y")) score += 5;
        if (word.contains("x")) score += 10;
        if (word.contains("z")) score += 10;
        if (word.contains("q")) score += 10;
        return score;
    }

    /**
     * Get a word from bot
     *
     * @param startingLetter letter bot has to start a word with
     * @return Bot word
     */
    private String getBotWord(char startingLetter) {
        String word;
        do {
            word = getRandomWord();
        } while (word.charAt(0) != startingLetter || wordLog.contains(capitalize(word)));
        return word;
    }

    /**
     * Timer
     *
     * @param millis milliseconds
     */
    private void countDownTimer(int millis) {
        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                int green = 255 / 20 * time;
                if (green > 255) green = 255;
                int red = 255 - green;
                tvTimer.setTextColor(Color.rgb(red, green, 0));

                tvTimer.setText(String.valueOf(time));

                time--;
            }

            public void onFinish() {
                tvTimer.setText(String.valueOf(time));

                etWord.setEnabled(false);
                // Dialog listeners
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            coins = score / 100 * 3;
                            xp = score / 100. * 2;
                            reset();
                        } else {
                            resultSet();
                            finish();
                        }
                    }
                };

                // Play again
                new AlertDialog.Builder(WordSnakeActivity.this)
                        .setTitle(R.string.game_over)
                        .setMessage(R.string.play_again)
                        .setPositiveButton(R.string.yes, listener)
                        .setNegativeButton(R.string.no, listener)
                        .setCancelable(false)
                        .show();
            }
        }.start();
    }

    /**
     * Reset Word Snake
     */
    private void reset() {
        time = 20;
        score = 0;
        wordLog.clear();
        adapter.notifyDataSetChanged();

        String startingWord = getRandomWord();
        wordLog.add(startingWord);
        etWord.setHint(startingWord.charAt(startingWord.length() - 1) + "");

        etWord.setEnabled(true);

        countDownTimer(20000);
    }

    /**
     * Get dictionary from raw resource
     *
     * @param language raw resource id
     */
    private void getWordList(int language) {
        InputStreamReader isr = new InputStreamReader(getResources().openRawResource(language));
        words = new ArrayList<>();
        BufferedReader reader = new BufferedReader(isr);
        try {
            String word;
            while ((word = reader.readLine()) != null) words.add(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a word from Dictionary list
     */
    private String getRandomWord() {
        return words.get(new Random().nextInt(words.size())).toLowerCase();
    }

    /**
     * Fill in result with data
     */
    private void resultSet() {
        coins = score / 100 * 3;
        xp = score / 100. * 2;
        Intent data = new Intent();
        data.putExtra("coins", coins);
        data.putExtra("xp", xp);
        setResult(GamesActivity.COINS_AND_EXPERIENCE, data);
        countDownTimer.cancel();
    }

    /**
     * Give coins and xp when you leave
     */
    @Override
    public void onBackPressed() {
        resultSet();
        super.onBackPressed();
    }
}
