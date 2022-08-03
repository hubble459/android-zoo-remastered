package nl.saxion.quentin.myzooremastered.ui.home;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.coinsandfood.CoinsAndFood;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Move;
import nl.saxion.quentin.myzooremastered.models.MoveSet;

/**
 * Activity to show a list of all moves of the currently selected animal
 */
public class MovesActivity extends AppCompatActivity {
    private View lastView;
    private Drawable defBG;
    private Animal animal;
    private ArrayAdapter<String> movesAdapter;
    private List<String> moves;

    /**
     * Fill movesListView with moves from animal
     * Uses the basic String Adapter with layout simple_list_item_1
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moves);

        animal = SelectedAnimal.getMyAnimal();

        ListView listView = findViewById(R.id.movesListView);

        // Move to string
        moves = new ArrayList<>();
        for (Move m : animal.getMoveSet()) {
            moves.add(m.getName());
        }

        // Adapter with Moves
        movesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moves);

        listView.setAdapter(movesAdapter);

        // On click; show move information
        // And change background color of selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.infoLayout).setVisibility(View.VISIBLE);

                if (lastView != null) {
                    lastView.setBackground(defBG);
                }
                lastView = view;
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent, null));

                String move = movesAdapter.getItem(position);
                for (Move m : animal.getMoveSet()) {
                    if (m.getName().equals(move)) {
                        updateValues(m);
                        break;
                    }
                }
            }
        });

        // On long click ask for move change
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String move = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                for (Move m : animal.getMoveSet()) {
                    if (m.getName().equals(move)) {
                        changeMove(m, position);
                        break;
                    }
                }
                return true;
            }
        });

        // Get standard background of view
        View firstItem = listView.getAdapter().getView(0, null, null);
        defBG = firstItem.getBackground();
    }

    /**
     * Change the long clicked move
     *
     * @param m   Move to change
     * @param pos Position of move to change
     */
    private void changeMove(final Move m, final int pos) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.change_move)
                .setMessage(String.format(getString(R.string.change_move_to_random), m.getName()))
                .setPositiveButton(String.format(getString(R.string.coin_price), 50), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If enough money
                        if (CoinsAndFood.getCoins() >= 50) {
                            Move randomMove;
                            do {
                                // Get random move
                                randomMove = new MoveSet().getRandomMove();
                            } while (randomMove == m);

                            // Change selected move to new random move
                            animal.getMoveSet().set(pos, randomMove);
                            moves.set(pos, randomMove.getName());

                            // Refresh
                            movesAdapter.notifyDataSetChanged();
                            updateValues(randomMove);
                        } else {
                            Toast.makeText(MovesActivity.this, R.string.n_coins, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Update Values
     * When you select a move the information will be shown above the move list
     *
     * @param m selected Move
     */
    private void updateValues(Move m) {
        TextView damageText = findViewById(R.id.damageTextView);
        if (m.getMoveType() == Move.ATK)
            damageText.setText(String.format("%s DMG", m.getDamage())); // <- HardCoded but same in every language
        else
            damageText.setText(Move.getMoveTypeName(m.getMoveType()));
        ((TextView) findViewById(R.id.descriptionTextView)).setText(String.valueOf(m.getDescription()));

        TextView ppText = findViewById(R.id.usesTextView);
        if (m.getMaxPP() == -1) {
            ppText.setText(R.string.infinite_pp);
        } else {
            ppText.setText(String.format("PP %s/%s", m.getLeftPP(), m.getMaxPP())); // <- HardCoded but same in every language
        }
    }
}
