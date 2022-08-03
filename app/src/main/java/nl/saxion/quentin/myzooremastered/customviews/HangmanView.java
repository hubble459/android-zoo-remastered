package nl.saxion.quentin.myzooremastered.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class HangmanView extends View {

    private Paint mPaint;
    private int lives;
    private float strokeWidth;

    public HangmanView(Context context) {
        super(context);
        init();
    }

    public HangmanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HangmanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        strokeWidth = mPaint.getStrokeWidth();
        lives = 10;
    }

    public void takeLife() {
        --lives;
        invalidate();
    }

    public void reset() {
        lives = 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float oneFourthWidth = getWidth() / 4;
        float oneFourthHeight = getHeight() / 4;
        float armX = oneFourthWidth * 2 + 4;
        float armY = (oneFourthHeight * 2 - 10) + oneFourthHeight / 2;

        switch (lives) {
            case 0: // Right Arm
                mPaint.setStrokeWidth(10);
                canvas.drawLine(
                        armX,
                        armY,
                        armX + 30,
                        armY + 30,
                        mPaint);
            case 1: // Left Arm
                mPaint.setStrokeWidth(10);
                canvas.drawLine(
                        armX,
                        armY,
                        armX - 30,
                        armY + 30,
                        mPaint);
            case 2: // Right Leg
                mPaint.setStrokeWidth(10);
                canvas.drawLine(
                        oneFourthWidth * 2 + 4,
                        oneFourthHeight * 3 - 10,
                        oneFourthWidth * 2 + 30,
                        oneFourthHeight * 3 + 30,
                        mPaint);
            case 3: // Left Leg
                mPaint.setStrokeWidth(10);
                canvas.drawLine(
                        oneFourthWidth * 2 + 4,
                        oneFourthHeight * 3 - 10,
                        oneFourthWidth * 2 - 30,
                        oneFourthHeight * 3 + 30,
                        mPaint);

                mPaint.setStrokeWidth(strokeWidth);
            case 4: // Body
                canvas.drawRect(
                        oneFourthWidth * 2,
                        oneFourthHeight * 2 - 10,
                        oneFourthWidth * 2 + 8,
                        oneFourthHeight * 3 - 10,
                        mPaint);
            case 5: // Head
                canvas.drawCircle(
                        oneFourthWidth * 2 + 3,
                        oneFourthHeight * 2 - 20,
                        40,
                        mPaint);
            case 6: // Rope
                canvas.drawRect(
                        oneFourthWidth * 2,
                        16,
                        oneFourthWidth * 2 + 7, // 7 is rope thickness
                        oneFourthHeight * 2 - 10,
                        mPaint);
            case 7: // Top beam
                canvas.drawRect(
                        oneFourthWidth - 6,
                        14,
                        oneFourthWidth * 3,
                        26,
                        mPaint);
            case 8: // Left beam
                canvas.drawRect(
                        oneFourthWidth - 6,
                        16,
                        oneFourthWidth + 6,
                        getHeight() - 16,
                        mPaint);
            case 9: // Bottom beam
                canvas.drawRect(
                        oneFourthWidth - oneFourthWidth / 2,
                        getHeight() - 24,
                        oneFourthWidth + oneFourthWidth / 2,
                        getHeight() - 16,
                        mPaint);
        }
    }
}
