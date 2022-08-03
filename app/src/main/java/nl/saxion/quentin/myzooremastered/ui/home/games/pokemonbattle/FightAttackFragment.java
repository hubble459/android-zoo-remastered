package nl.saxion.quentin.myzooremastered.ui.home.games.pokemonbattle;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.models.Animal;
import nl.saxion.quentin.myzooremastered.models.Move;

/**
 * Show moves and info
 */
public class FightAttackFragment extends Fragment {
    private final Animal animal;
    private View lastView;

    public FightAttackFragment() {
        this.animal = SelectedAnimal.getMyAnimal();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attack_fight, container, false);
    }

    /**
     * Initiate attack fragment
     * @param view inflated fragment view
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        ListView listView = view.findViewById(R.id.moveList);

        ArrayList<String> moves = new ArrayList<>();
        for (Move move : animal.getMoveSet()) moves.add(move.getName());

        ArrayAdapter<String> movesAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, moves);
        listView.setAdapter(movesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemView, int position, long id) {
                if (lastView != itemView) {
                    lastView.setBackgroundColor(Color.TRANSPARENT);
                    lastView = itemView;
                }
                itemView.setBackgroundColor(getResources().getColor(R.color.colorAccent, null));

                String move = ((TextView) itemView.findViewById(android.R.id.text1)).getText().toString();
                for (Move m : animal.getMoveSet()) {
                    if (m.getName().equals(move)) {
                        TextView damageText = view.findViewById(R.id.damageTextView);
                        if (m.getMoveType() == Move.ATK)
                            damageText.setText(String.format("%s DMG", m.getDamage()));
                        else
                            damageText.setText(Move.getMoveTypeName(m.getMoveType()));
                        ((TextView) view.findViewById(R.id.descriptionTextView)).setText(String.valueOf(m.getDescription()));

                        TextView ppText = view.findViewById(R.id.usesTextView);
                        if (m.getMaxPP() == -1) {
                            ppText.setText("PP ∞");
                        } else {
                            ppText.setText(String.format("PP %s/%s", m.getLeftPP(), m.getMaxPP()));
                        }
                        view.findViewById(R.id.attackButton).setTag(m);
                    }
                }
            }
        });


        View firstItem = listView.getAdapter().getView(0, null, null);
        lastView = firstItem;

        listView.performItemClick(firstItem, 0, listView.getAdapter().getItemId(0));
    }
}
