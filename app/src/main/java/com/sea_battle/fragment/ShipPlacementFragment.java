package com.sea_battle.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sea_battle.R;
import com.sea_battle.ShipPlacement;
import com.sea_battle.view.ShipPlacementView;

public class ShipPlacementFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    private ShipPlacement mShipPlacement;
    private ShipPlacementView mField;

    private int[] shipCountByType;
    private View addShip1Button;
    private View addShip2Button;
    private View addShip3Button;
    private View addShip4Button;
    private View mStartButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            Log.d("MyLogs", "ShipPlacementFragment: invalid Context");
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
        View view = inflater.inflate(R.layout.fragment_ship_placement, container, false);
        shipCountByType = new int[]{4, 3, 2, 1};
        mStartButton = view.findViewById(R.id.start);
        mStartButton.setEnabled(false);
        mShipPlacement = new ShipPlacement();
        mField = (ShipPlacementView) view.findViewById(R.id.field);
        mField.setShipPlacement(mShipPlacement);
        view.findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShipPlacement.up();
                mField.invalidate();
            }
        });
        view.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShipPlacement.right();
                mField.invalidate();
            }
        });
        view.findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShipPlacement.down();
                mField.invalidate();
            }
        });
        view.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShipPlacement.left();
                mField.invalidate();
            }
        });
        view.findViewById(R.id.turn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShipPlacement.turn();
                mField.invalidate();
            }
        });
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartClick();
            }
        });
        addShip1Button = view.findViewById(R.id.ship1);
        addShip2Button = view.findViewById(R.id.ship2);
        addShip3Button = view.findViewById(R.id.ship3);
        addShip4Button = view.findViewById(R.id.ship4);
        addShip1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShip(1);
            }
        });
        addShip2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShip(2);
            }
        });
        addShip3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShip(3);
            }
        });
        addShip4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShip(4);
            }
        });
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShipPlacement.done()) {
                    mField.invalidate();
                    addShip1Button.setEnabled(shipCountByType[0] > 0);
                    addShip2Button.setEnabled(shipCountByType[1] > 0);
                    addShip3Button.setEnabled(shipCountByType[2] > 0);
                    addShip4Button.setEnabled(shipCountByType[3] > 0);

                    if (shipCountByType[0] == 0 &&
                            shipCountByType[1] == 0 &&
                            shipCountByType[2] == 0 &&
                            shipCountByType[3] == 0
                            ) {
                        mStartButton.setEnabled(true);
                    }
                }
            }
        });
        view.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShipPlacement.reset();
                mField.invalidate();
                shipCountByType = new int[]{4, 3, 2, 1};
                addShip1Button.setEnabled(true);
                addShip2Button.setEnabled(true);
                addShip3Button.setEnabled(true);
                addShip4Button.setEnabled(true);
                mStartButton.setEnabled(false);
            }
        });
        return view;
    }

    private void addShip(@IntRange(from = 1, to = 4) int count) {
        mShipPlacement.addShip(count, true);
        shipCountByType[count - 1]--;
        addShip1Button.setEnabled(false);
        addShip2Button.setEnabled(false);
        addShip3Button.setEnabled(false);
        addShip4Button.setEnabled(false);
        mField.invalidate();
    }

    public int[][] getData() {
        return mShipPlacement.getData();
    }

    public interface OnFragmentInteractionListener {

        void onStartClick();

    }

}
