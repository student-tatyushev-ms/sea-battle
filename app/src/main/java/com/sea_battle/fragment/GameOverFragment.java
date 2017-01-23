package com.sea_battle.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sea_battle.R;

public class GameOverFragment extends DialogFragment {

    private static final String ARG_WIN = "win";

    private OnFragmentInteractionListener mListener;

    public GameOverFragment() {
        // Required empty public constructor
    }

    public static GameOverFragment newInstance(boolean isWin) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_WIN, isWin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean isWin = getArguments() != null && getArguments().getBoolean(ARG_WIN);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage(isWin ? R.string.win : R.string.lose)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onButtonPressed();
                    }
                });
        return builder.create();
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onOkClick();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onOkClick();

    }

}
