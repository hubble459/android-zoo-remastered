package nl.saxion.quentin.myzooremastered.ui.home.games;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.ZooUtils;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;

/**
 * Higher or Lower game
 */
public class HigherOrLowerActivity extends AppCompatActivity {
    private NumberPicker betAmount;
    private TextView customBet, number, lastNumber;
    private MenuItem coinsMenu;
    private int num;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_higher_or_lower);

        betAmount = findViewById(R.id.betAmount);
        betAmount.setMinValue(10);
        betAmount.setMaxValue(200);
        betAmount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                customBet.setText(String.valueOf(newVal));
            }
        });

        customBet = findViewById(R.id.customBet);
        customBet.setText("10");
        customBet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) return;

                int num = Integer.parseInt(s.toString());
                if (num < 10) {
                    betAmount.setValue(10);
                } else if (num > 200) {
                    betAmount.setValue(200);
                    customBet.setText(String.valueOf(200));
                } else {
                    betAmount.setValue(num);
                }
            }
        });

        number = findViewById(R.id.number);
        lastNumber = findViewById(R.id.lastNumber);

        num = getRandomNumber(true);
        number.setText(String.valueOf(num));
    }

    private int getRandomNumber(boolean noOneTen) {
        int rand;
        do {
            rand = new Random().nextInt(10);
            if (noOneTen) rand = new Random().nextInt(9) + 1;
        } while (rand == num);
        return rand;
    }

    public void lower(View view) {
        showNext(true);
    }

    public void higher(View view) {
        showNext(false);
    }

    private void showNext(boolean lower) {
        int last = num;
        lastNumber.setText(String.valueOf(last));
        num = getRandomNumber(false);
        number.setText(String.valueOf(num));

        double score;
        int betAmount = Math.min(this.betAmount.getValue(), Math.max(CoinsAndFood.getCoins(), 0));
        int gain = 0 - betAmount;

        if (lower && num < last) {
            score = 1 - (last / 11.);
            gain = (int) (score * betAmount);
        } else if (!lower && num > last) {
            score = 1 - ((11 - last) / 11.);
            gain = (int) (score * betAmount);
        }

        if (gain >= 0)
            Toast.makeText(this, "Gained: " + gain, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Lost: " + betAmount, Toast.LENGTH_SHORT).show();

        setBackground(gain >= 0);
        CoinsAndFood.addCoins(gain);
        refreshCoins();
    }

    private void setBackground(boolean gained) {
        int red = 255;
        int green = 0;
        if (gained) {
            red = 0;
            green = 255;
        }
        lastNumber.setBackgroundColor(Color.argb(50, red, green, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_just_coins, menu);
        coinsMenu = menu.findItem(R.id.coins);
        refreshCoins();
        return true;
    }

    private void refreshCoins() {
        coinsMenu.setTitle(String.format(getString(R.string.coins_amount), CoinsAndFood.getCoins()));
        ZooUtils.saveCounters(this);
    }
}
