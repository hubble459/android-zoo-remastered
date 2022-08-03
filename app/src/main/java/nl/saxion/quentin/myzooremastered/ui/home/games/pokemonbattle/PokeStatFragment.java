package nl.saxion.quentin.myzooremastered.ui.home.games.pokemonbattle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import nl.saxion.quentin.myzooremastered.R;
import nl.saxion.quentin.myzooremastered.SelectedAnimal;
import nl.saxion.quentin.myzooremastered.models.Animal;

/**
 * Show status/info of pokemon/animal
 */
public class PokeStatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Animal animal = SelectedAnimal.getMyAnimal();

        ((TextView) view.findViewById(R.id.name)).setText(String.format(getString(R.string.name) + ": %s", animal.getName()));
        ((TextView) view.findViewById(R.id.level)).setText(String.format(getString(R.string.level), animal.getLevel()));
        ((TextView) view.findViewById(R.id.animalHP2)).setText(String.format(getString(R.string.hp2), animal.getHP(), animal.getMaxHP()));
        ((TextView) view.findViewById(R.id.def)).setText(String.format(getString(R.string.def), animal.getDefence()));
        ((TextView) view.findViewById(R.id.xp)).setText(String.format(getString(R.string.xp), animal.getXp(), animal.getXpNeeded()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fightFragmentContainer, new MainFightFragment()).commit();
            }
        });
    }
}
