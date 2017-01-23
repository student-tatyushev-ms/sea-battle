package com.sea_battle.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sea_battle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    private boolean isButtonClicked = false;

    public ConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            Log.d("MyLogs", "ConnectionFragment: invalid Context");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        view.findViewById(R.id.insecure_connect_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInsecureConnectPress();
            }
        });
        view.findViewById(R.id.discoverable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDiscoverablePress();
            }
        });
        return view;
    }

    private void onInsecureConnectPress() {
        isButtonClicked = true;
        if (listener != null) {
            listener.onInsecureConnectClick();
        }
    }

    private void onDiscoverablePress() {
        if (listener != null) {
            listener.onDiscoverableClick();
        }
    }

    public boolean isButtonClicked() {
        return isButtonClicked;
    }

    public interface OnFragmentInteractionListener {

        void onInsecureConnectClick();

        void onDiscoverableClick();

    }


}
