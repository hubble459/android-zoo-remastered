package nl.saxion.quentin.myzooremastered.ui.home.games;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.customviews.HangmanView;
import nl.saxion.quentin.myzooremastered.ui.home.GamesActivity;

/**
 * Hangman game
 */
public class HangmanActivity extends AppCompatActivity {

    private String randomWord;
    private int live = 10;
    private int time = 30;
    private int coins = 0;
    private double xp = 0;
    private boolean guessIsWord;
    private boolean gameOver;
    private boolean[] guessedChars;
    private List<String> words;
    private List<String> wrongCharacters;

    private Button guessButton;
    private TextView guessChar;
    private TextView guessWord;
    private TextView tvWord;
    private TextView tvWrong;
    private TextView textTimer;
    private HangmanView hmView;
    private CountDownTimer countDownTimer;
    private TextWatcher textWatcherChar = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("")) {
                guessWord.setEnabled(true);
                guessButton.setEnabled(false);
            } else {
                guessWord.setEnabled(false);
                guessButton.setEnabled(true);
                guessIsWord = false;
            }

            if (s.length() > 1) {
                guessChar.setText(String.valueOf(s.charAt(0)));
            }
        }
    };
    private TextWatcher textWatcherWord = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("")) {
                guessChar.setEnabled(true);
                guessButton.setEnabled(false);
            } else {
                guessChar.setEnabled(false);
                guessButton.setEnabled(true);
                guessIsWord = true;
            }
        }
    };

    /**
     * Get WordList and start game
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman);

        int language = getIntent().getIntExtra("language", -1);
        if (language == -1) finish();

        // Get WordList
        getWordList(language);
        // get a Word from WordList
        getRandomWord();

        // Set vars
        tvWord = findViewById(R.id.word);
        tvWrong = findViewById(R.id.wrong);
        hmView = findViewById(R.id.hangmanView);
        guessButton = findViewById(R.id.guess);
        guessedChars = new boolean[randomWord.length()];
        wrongCharacters = new ArrayList<>();
        textTimer = findViewById(R.id.timer);

        if (savedInstanceState != null) reset();

        // Show bars for letters
        // eg. "word" becomes "_ _ _ _ "
        refreshHidden();

        // When you want to guess a character, you cannot fill in the guessWord EditText
        disableOtherWhenTyping();

        // Start 30s Timer
        countDownTimer(30000);
    }

    /**
     * Timer
     *
     * @param millis milliseconds
     */
    private void countDownTimer(int millis) {
        countDownTimer = new CountDownTimer(millis, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                int green = 255 / 30 * time;
                if (green > 255) green = 255;
                int red = 255 - green;
                textTimer.setTextColor(Color.rgb(red, green, 0));

                textTimer.setText(time + "s");
                time--;
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                if (time != 0) countDownTimer(time * 1000);
                else {
                    textTimer.setText(time + "s");
                    checkWin();
                }
            }
        }.start();
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
    private void getRandomWord() {
        randomWord = words.get(new Random().nextInt(words.size())).toLowerCase();
    }

    /**
     * Turn text into _ _ _ _ and show it on screen
     */
    private void refreshHidden() {
        StringBuilder randomWordHidden = new StringBuilder();
        for (int i = 0; i < randomWord.length(); i++) {
            if (guessedChars[i]) {
                randomWordHidden.append(randomWord.charAt(i)).append(" ");
            } else {
                randomWordHidden.append("_ ");
            }
        }
        tvWord.setText(randomWordHidden.toString());
    }

    /**
     * Check if game ended
     */
    private void checkWin() {
        gameOver = true;
        if (live != 0 && time != 0)
            for (boolean b : guessedChars) {
                if (!b) {
                    gameOver = false;
                    break;
                }
            }

        if (gameOver) {
            countDownTimer.cancel();

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        reset();
                    } else {
                        Intent data = new Intent();
                        data.putExtra("coins", coins);
                        data.putExtra("xp", xp);
                        setResult(GamesActivity.COINS_AND_EXPERIENCE, data);
                        finish();
                    }
                }
            };

            String msg;
            if (time == 0) {
                msg = getString(R.string.you_ran_out_of_time) + "\n" + String.format(getString(R.string.word_is), randomWord);
            } else if (live == 0) {
                msg = getString(R.string.you_died) + "\n" + String.format(getString(R.string.word_is), randomWord);
            } else {
                msg = getString(R.string.you_guessed_the_word);
                coins += time / 2;
                xp += time / 3.;
            }
            new AlertDialog.Builder(this)
                    .setTitle(R.string.play_again)
                    .setMessage(msg)
                    .setPositiveButton(R.string.yes, listener)
                    .setNegativeButton(R.string.no, listener)
                    .show();
        }
    }

    /**
     * Reset all values
     */
    private void reset() {
        wrongCharacters.clear();
        hmView.reset();
        gameOver = false;
        live = 10;
        time = 30;
        getRandomWord();
        guessedChars = new boolean[randomWord.length()];
        refreshHidden();
        countDownTimer(30000);
    }

    /**
     * When you press the Guess Button the filled in EditText will be used
     *
     * @param view Button
     */
    public void guess(View view) {
        if (gameOver) {
            checkWin();
            return;
        }
        if (guessIsWord) {
            if (randomWord.equalsIgnoreCase(guessWord.getText().toString())) {
                Arrays.fill(guessedChars, true);
                refreshHidden();
            } else {
                hmView.takeLife();
            }
        } else {
            char c = guessChar.getText().toString().toLowerCase().charAt(0);
            if (charFound(c)) {
                refreshHidden();
                time += 10;
            } else {
                if (!wrongCharacters.contains(c + "")) {
                    wrongCharacters.add(c + "");
                    --live;
                    tvWrong.setText(wrongCharacters.toString());
                    hmView.takeLife();
                }
            }
        }

        checkWin();
    }

    /**
     * Check if a character is found in a word and add this position to guessedChars[] boolean array
     *
     * @param c character
     * @return found; yes or no
     */
    private boolean charFound(char c) {
        boolean result = false;
        for (int i = 0; i < randomWord.length(); i++) {
            if (randomWord.charAt(i) == c) {
                guessedChars[i] = true;
                result = true;
            }
        }
        return result;
    }

    /**
     * When typing in one EditText the other will be disabled
     */
    private void disableOtherWhenTyping() {
        guessChar = findViewById(R.id.guessChar);
        guessWord = findViewById(R.id.guessWord);

        guessChar.addTextChangedListener(textWatcherChar);
        guessWord.addTextChangedListener(textWatcherWord);
    }

    /**
     * Stop counter
     */
    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        super.onBackPressed();
    }
}
