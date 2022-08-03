package nl.saxion.quentin.myzooremastered.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;

/**
 * Custom View for choosing an amount of items
 */
public class AmountPicker extends ConstraintLayout {
    private final int MIN = 1;
    private final int MAX = 99;
    private Context mContext;
    private NumberPicker np;
    private SeekBar sb;
    private TextView tv;
    private int amount = 1;
    private int price;
    private int finalPrice;
    private OnEnoughMoneyListener listener;

    public AmountPicker(Context context) {
        super(context);
        init(context);
    }

    public AmountPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AmountPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Initiate the View
     * <p>
     * Amount Picker uses three separate Views
     * - NumberPicker
     * - SeekBar
     * - TextView
     * <p>
     * The max of both the SeekBar and NumberPicker is 99
     * The TextView translates the amount to the actual Price
     *
     * @param context context
     */
    private void init(Context context) {
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.amount_picker, this);

        np = view.findViewById(R.id.numberPicker);
        sb = view.findViewById(R.id.numberSeekBar);
        tv = view.findViewById(R.id.amount);

        np.setMinValue(MIN);
        np.setMaxValue(MAX);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateView(newVal);
            }
        });

        sb.setProgress(MIN);
        sb.setMax(MAX);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    seekBar.setProgress(1);
                } else {
                    updateView(progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * When one value changes, everything gets updated
     *
     * @param amount The currently selected amount
     */
    private void updateView(int amount) {
        if (this.amount != amount) this.amount = amount;
        if (finalPrice != price * amount) finalPrice = price * amount;
        if (np.getValue() != amount) np.setValue(amount);
        if (sb.getProgress() != amount) sb.setProgress(amount);
        if (!tv.getText().toString().equals("Price: " + amount)) {
            tv.setText(String.format(mContext.getString(R.string.price), finalPrice));
        }

        // Custom Listener for when enough money
        if (listener != null) {
            if (CoinsAndFood.getCoins() < finalPrice)
                listener.onEnoughMoney(false);
            else
                listener.onEnoughMoney(true);
        }
    }

    /**
     * Set the Price
     *
     * @param price price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Price * amount = finalPrice
     *
     * @return this.finalPrice
     */
    public int getFinalPrice() {
        return finalPrice;
    }

    /**
     * The amount of product selected
     *
     * @return this.amount
     */
    public int getAmount() {
        return amount;
    }

    public void setListener(OnEnoughMoneyListener listener) {
        this.listener = listener;
        updateView(MIN);
    }

    /**
     * Custom listener for enough money
     */
    public interface OnEnoughMoneyListener {
        void onEnoughMoney(boolean enough);
    }
}
