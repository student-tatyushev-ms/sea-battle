package com.sea_battle.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sea_battle.R;
import com.sea_battle.SeaBattleField;
import com.sea_battle.view.SeaBattleFieldView;

public class GameFragment extends Fragment {

    private static final String KEY_DATA = "DATA";
    private static final String KEY_IS_FIRST = "IS_FIRST";

    private OnFragmentInteractionListener listener;

    private SeaBattleFieldView myFieldView;
    private SeaBattleFieldView enemyFieldView;

    private SeaBattleField myField;
    private SeaBattleField enemyField;

    public static GameFragment newInstance(int[][] data, boolean isFirst) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, data);
        args.putBoolean(KEY_IS_FIRST, isFirst);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            Log.d("MyLogs", "GameFragment: invalid Context");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        myFieldView = (SeaBattleFieldView) view.findViewById(R.id.my_field);
        enemyFieldView = (SeaBattleFieldView) view.findViewById(R.id.enemy_field);
        myField = new SeaBattleField();

        int[][] data = (int[][]) getArguments().getSerializable(KEY_DATA);
        boolean isFirst = getArguments().getBoolean(KEY_IS_FIRST);

        myField.setData(data);
        myFieldView.invalidate();
        enemyField = new SeaBattleField();
        myFieldView.setSeaBattleField(myField);
        enemyFieldView.setSeaBattleField(enemyField);
        myFieldView.setClickable(false);
        enemyFieldView.setClickable(isFirst);

        enemyFieldView.setOnCellTouchListener(new SeaBattleFieldView.OnCellTouchListener() {
            @Override
            public void onCellTouch(int i, int j) {
                listener.onCellClick(i, j);
                enemyFieldView.setClickable(false);
            }
        });
        return view;
    }

    public void onResponse(int i, int j, boolean isHit) {
        if (isHit) {
            enemyField.hit(i, j);
        } else {
            enemyField.miss(i, j);
        }
        enemyFieldView.invalidate();
    }

    public boolean checkCell(int i, int j) {
        boolean isHit = myField.checkCell(i, j);
        myFieldView.invalidate();
        enemyFieldView.setClickable(true);
        return isHit;  // check and attack
    }

    public boolean amIWin() {
        return enemyField.hitCount() >= 20;
    }

    public boolean isEnemyWin() {
        return myField.hitCount() >= 20;
    }

    public interface OnFragmentInteractionListener {

        void onCellClick(int i, int j);

    }

}
