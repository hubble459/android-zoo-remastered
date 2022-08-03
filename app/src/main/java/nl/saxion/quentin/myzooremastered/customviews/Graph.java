package nl.saxion.quentin.myzooremastered.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import nl.saxion.quentin.myzooremastered.lists.RegionList;
import nl.saxion.quentin.myzooremastered.models.Animal;

/**
 * Custom view with custom onDraw method
 * Draws a graph showing the animal population
 * (All types you own with the amount of animals you have of that corresponding type)
 */
public class Graph extends View {
    private Paint mPaint;
    private HashMap<String, Animal> animalHashMap;
    private HashMap<String, Integer> list;

    public Graph(Context context) {
        super(context);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initiate and sort all animals in list and animalHashMap
     * List keeps the count of a type
     * animalHashMap is used to get the image for a type
     * So there's one animal for each type in animalHashMap
     * And one number for each type in list
     */
    private void init() {
        mPaint = new Paint();
        list = new HashMap<>();
        animalHashMap = new HashMap<>();

        List<Animal> animals = RegionList.getAnimalList();

        for (Animal a : animals) {
            if (list.containsKey(a.getType())) {
                list.put(a.getType(), list.get(a.getType()) + 1);
            } else {
                list.put(a.getType(), 1);
            }

            if (!animalHashMap.containsKey(a.getType())) {
                animalHashMap.put(a.getType(), a);
            }
        }
    }

    /**
     * Here the graph is drawn
     *
     * @param canvas Canvas to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Standard Color
        mPaint.setColor(Color.BLACK);

        // Make sure the image does't stretch over the whole page by making 6 items the minimum
        int imageSize;
        if (animalHashMap.size() < 6) {
            imageSize = (getHeight() - 10) / 6;
        } else {
            imageSize = (getHeight() - 10) / animalHashMap.size();
        }

        int left = 10;
        int top = 10;
        int right = imageSize;
        int bottom = imageSize;

        // Get the maximum value
        int max = 0;
        for (Integer i : list.values()) {
            max = Math.max(max, i);
        }

        // For every type
        for (String s : animalHashMap.keySet()) {
            // Graph Color
            mPaint.setColor(Color.BLUE);

            // Draw the type image
            Animal a = animalHashMap.get(s);
            if (a != null) {
                Drawable d = getResources().getDrawable(a.getImageId(), null);
                d.setBounds(left, top, right, bottom);
                d.draw(canvas);
            }

            // Draw the Rectangle with the corresponding length (width)
            double percent = 100.0 / max * list.get(s);
            double width = ((percent * (getWidth() - (right + 10))) / 100);
            canvas.drawRect(right + 10, top + 10, (float) width, bottom - 10, mPaint);

            // Text Color, Size and Scale
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(42);
            mPaint.setTextScaleX(2);

            // Draw the text behind the progressbar (Text is the amount of animals for that type)
            canvas.drawText(String.format("[%s]", list.get(s)), (float) (width + 20), top + (int) (imageSize / 2.0), mPaint);

            // Go down a bar
            top += imageSize;
            bottom += imageSize;
        }
    }
}