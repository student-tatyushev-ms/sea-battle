package com.sea_battle;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.sea_battle.fragment.ConnectionFragment;
import com.sea_battle.fragment.GameFragment;
import com.sea_battle.fragment.GameOverFragment;
import com.sea_battle.fragment.ShipPlacementFragment;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class MainActivity extends AppCompatActivity implements
        ConnectionFragment.OnFragmentInteractionListener,
        ShipPlacementFragment.OnFragmentInteractionListener,
        GameFragment.OnFragmentInteractionListener,
        GameOverFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private boolean isEnemyReady = false;
    private boolean amIReady = false;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothService mBluetoothService = null;

    private FragmentManager fragmentManager;

    private boolean isFirstPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            switchToConnectionFragment();
        }

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupConnection() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else if (mBluetoothService == null) {
            setupConnection();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mBluetoothService.start();
            }
        }
    }

    /**
     * Set up the UI and background operations.
     */
    private void setupConnection() {
        Log.d(TAG, "setupConnection()");

        // Initialize the BluetoothService to perform bluetooth connections
        mBluetoothService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mBluetoothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("MyLogs", "handleMessage: msg.what " + msg.what);
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switchToShipPlacementFragment();
                                }
                            });
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(MainActivity.this, "MESSAGE_READ: " + readMessage, Toast.LENGTH_SHORT).show();
                    if (readMessage.substring(0, 1).equals("G")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isEnemyReady = true;
                                checkReady();
                            }
                        });
                    } else if (readMessage.substring(0, 1).equals("H")) {
                        int i = Integer.parseInt(readMessage.substring(1, 2));
                        int j = Integer.parseInt(readMessage.substring(2, 3));
                        onResponse(i, j, true);
                    } else if (readMessage.substring(0, 1).equals("M")) {
                        int i = Integer.parseInt(readMessage.substring(1, 2));
                        int j = Integer.parseInt(readMessage.substring(2, 3));
                        onResponse(i, j, false);
                    } else {
                        int i = Integer.parseInt(readMessage.substring(0, 1));
                        int j = Integer.parseInt(readMessage.substring(1, 2));
                        onGetCoordinates(i, j);
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(MainActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "MESSAGE_TOAST: " + msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void onResponse(int i, int j, boolean isHit) {
        GameFragment gameFragment = (GameFragment) fragmentManager.findFragmentByTag("GameFragment");
        gameFragment.onResponse(i, j, isHit);
        checkGameOver();
    }

    private void onGetCoordinates(int i, int j) {
        GameFragment gameFragment = (GameFragment) fragmentManager.findFragmentByTag("GameFragment");
        boolean isHit = gameFragment.checkCell(i, j);
        MainActivity.this.sendMessage((isHit ? "H" : "M") + i + j);
        checkGameOver();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    setupConnection();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBluetoothService.connect(device, secure);
    }

    @Override
    public void onInsecureConnectClick() {
        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }

    @Override
    public void onDiscoverableClick() {
        // Ensure this device is discoverable by others
        ensureDiscoverable();
    }

    void switchToConnectionFragment() {
        fragmentManager
                .beginTransaction()
                .add(R.id.container, new ConnectionFragment(), "ConnectionFragment")
                .commit();
    }

    void switchToShipPlacementFragment() {
        ConnectionFragment connectionFragment = (ConnectionFragment) fragmentManager.findFragmentByTag("ConnectionFragment");
        isFirstPlayer = connectionFragment.isButtonClicked();

        fragmentManager
                .beginTransaction()
                .replace(R.id.container, new ShipPlacementFragment(), "ShipPlacementFragment")
                .commit();
    }

    void switchToGameFragment() {
        ShipPlacementFragment shipPlacementFragment = (ShipPlacementFragment) fragmentManager.findFragmentByTag("ShipPlacementFragment");
        int[][] data = shipPlacementFragment.getData();

        GameFragment gameFragment = GameFragment.newInstance(data, isFirstPlayer);

        fragmentManager
                .beginTransaction()
                .replace(R.id.container, gameFragment, "GameFragment")
                .commit();
    }

    @Override
    public void onStartClick() {
        sendMessage("G");
        amIReady = true;
        checkReady();
    }

    public void checkReady() {
        if (isEnemyReady && amIReady) {
            switchToGameFragment();
        }
    }

    private void checkGameOver() {
        GameFragment gameFragment = (GameFragment) fragmentManager.findFragmentByTag("GameFragment");
        if (gameFragment.amIWin()) {
            GameOverFragment.newInstance(true).show(fragmentManager, "GameOverFragment");
            return;
        }
        if (gameFragment.isEnemyWin()) {
            GameOverFragment.newInstance(false).show(fragmentManager, "GameOverFragment");
        }
    }

    @Override
    public void onCellClick(int i, int j) {
        sendMessage("" + i + j);
    }

    @Override
    public void onOkClick() {
        finish();
    }

}
