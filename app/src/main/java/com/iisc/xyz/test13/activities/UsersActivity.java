package com.iisc.xyz.test13.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.iisc.xyz.test13.BluetoothConnectionService;
import com.iisc.xyz.test13.DeviceListAdapter;
import com.iisc.xyz.test13.R;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class UsersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "UsersActivity";
    private TextView textViewName;
    private Button graphBtn;
    private Button viewDataBtn;
    private ImageButton btPairBtn;
    private ImageButton receiveDataBtn;
    private ImageButton mapsBtn;
    private ImageButton btDiscoverable;

    public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;
    ListView listViewDevices;
    ListView listViewPaired;
    BluetoothAdapter bluetoothAdapter;

    BluetoothConnectionService bluetoothConnection;

    Button startConnectionBtn;
    Button stopConnectionBtn;
    Button sendBtn;

    public String nameFromIntent;



    //TextView incomingMessages;
    StringBuilder messages;

    EditText etsend;

    Button saveBtn;
    boolean flagg=false;




    // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothDevice device;


    ArrayAdapter<String> arrayAdapter1;
    ArrayAdapter<String> arrayAdapter2;
    ArrayList arrayList;


    private final BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);

                switch(state)
                {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive(): State OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "broadcastReceiver1: State Turning OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "broadcastReceiver1: State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "broadcastReceiver1: State Turning ON");
                        break;
                }

            }
        }
    };
    private final BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "broadcastReceiver2: Discoverability enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "broadcastReceiver2: Discoverability disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "broadcastReceiver2: Discoverability disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "broadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "broadcastReceiver2: Connected.");
                        break;
                }

            }

        }
    };
    private BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(device);
                Log.d(TAG, "onReceive():" + device.getName()+":"+device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context,R.layout.device_adapter_view, btDevices);

                listViewDevices.setAdapter(deviceListAdapter);

            }
        }
    };

    private BroadcastReceiver broadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice devicex = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(devicex.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    Toast.makeText(getApplicationContext(),"Device Paired",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "broadcastReceiver4:BOND_BONDED");

                    device=devicex;
                }
                if(devicex.getBondState() == BluetoothDevice.BOND_BONDING)
                {
                    Toast.makeText(getApplicationContext(),"Pairing...",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "broadcastReceiver4:BOND_BONDING");
                }
                if(devicex.getBondState() == BluetoothDevice.BOND_NONE)
                {
                    Toast.makeText(getApplicationContext(),"Pairing unsuccessful",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "broadcastReceiver4:BOND_NONE");
                }
            }
            findPairedDevices();
        }
    };



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy(): called.");
        super.onDestroy();
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
        unregisterReceiver(broadcastReceiver3);
        unregisterReceiver(broadcastReceiver4);
        unregisterReceiver(receiver);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        textViewName = findViewById(R.id.text1);
        nameFromIntent = getIntent().getStringExtra("EMAIL");
        textViewName.setText("WELCOME " + nameFromIntent);

        graphBtn= findViewById(R.id.graphBtn);
       // viewDataBtn=findViewById(R.id.viewDataBtn);

        btPairBtn= findViewById(R.id.onOffBTBtn);
        btDiscoverable= findViewById(R.id.btDiscoverableBtn);
        listViewDevices = findViewById(R.id.lvDiscover);
        listViewDevices.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        listViewPaired= findViewById(R.id.lvPaired);
        listViewPaired.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        startConnectionBtn = findViewById(R.id.startConnectionBtn);
        stopConnectionBtn = findViewById(R.id.stopConnectionBtn);

        sendBtn=findViewById(R.id.sendBtn);
       // etsend = findViewById(R.id.etSend);




       // incomingMessages=findViewById(R.id.incomingMessages);
        //incomingMessages.setMovementMethod(new ScrollingMovementMethod());
        messages= new StringBuilder();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("incomingMessage"));

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver4, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        findPairedDevices();
        listViewDevices.setOnItemClickListener(UsersActivity.this);



        btDevices=new ArrayList<>();


        btPairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick(): enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        startConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
            }
        });

        stopConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopConnection();
            }
        });

        /*sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = etsend.getText().toString().getBytes(Charset.defaultCharset());
                bluetoothConnection.write(bytes);

                etsend.setText("");
            }
        });*/

       // saveBtn = findViewById(R.id.saveBtn);

        

    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(flagg==false) {
                String text = intent.getStringExtra("theMessage");

                messages.append(text + "");


               // incomingMessages.append(text);

            }
        }
    };
    //create method for starting connection
//***remember the connection will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(device,MY_UUID_INSECURE);
        Toast.makeText(this, "Now Receiving Data", Toast.LENGTH_SHORT).show();
    }
    public void stopConnection(){

        enableDisableBT();
        saveDataFunction();

    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice devicex, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        bluetoothConnection.startClient(devicex,uuid);
    }



    public void enableDisableBT()
    {
        if(bluetoothAdapter==null)
        {
            Log.d(TAG, "enableDisableBT(): Does not have BT capabilities");
            Toast.makeText(getApplicationContext(), "Your device does not support bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT(): enabling bluetooth.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1, BTIntent);

        }
        if(bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "enableDisableBT(): disabling bluetooth.");
            bluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(),"Your bluetooth is disabled",Toast.LENGTH_SHORT).show();

            IntentFilter BTIntent = new IntentFilter((BluetoothAdapter.ACTION_STATE_CHANGED));
            registerReceiver(broadcastReceiver1, BTIntent);
        }

    }

    public void btMakeDiscoverable(View view) {
        Log.d(TAG, "btMakeDiscoverable(): Making Device discoverable");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

        startActivityForResult(discoverableIntent, 2);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(broadcastReceiver2, intentFilter);
    }

    public void btnDiscover(View view) {
        Log.d(TAG, "btDiscover(): Looking for unpaired devices.");
        Toast.makeText(getApplicationContext(),"Looking for Unpaired Devices",Toast.LENGTH_SHORT).show();

        if(bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btDiscover(): Canceling discovery.");

            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver3, discoverDevicesIntent);

        }
        if(!bluetoothAdapter.isDiscovering())
        {
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver3, discoverDevicesIntent);

        }
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(),"Your bluetooth is enabled",Toast.LENGTH_SHORT).show();
                findPairedDevices();
            }
        }
        if(requestCode == 2)
        {
            if(resultCode != RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(),"Your device is now discoverable",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findPairedDevices()
    {
        int index = 0;
        Set<BluetoothDevice> bluetoothSet = bluetoothAdapter.getBondedDevices();
        String[] str = new String[bluetoothSet.size()];

        if(bluetoothSet.size()>0)
        {
            for(BluetoothDevice device: bluetoothSet)
            {
                str[index]= device.getName();
                index+=1;
            }

            arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,str) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                    text.setTextColor(Color.WHITE);
                    return view;
                }
            };
            listViewPaired.setAdapter(arrayAdapter1);
        }

    }






    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick(): You clicked on a device.");

        String dName = btDevices.get(i).getName();
        String dAddress = btDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick(): device name = "+dName);
        Log.d(TAG, "onItemClick(): device address = "+dAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Toast.makeText(getApplicationContext(),"Trying to pair with "+dName+".",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Trying to pair with " + dName);
            btDevices.get(i).createBond();

            device = btDevices.get(i);
            bluetoothConnection = new BluetoothConnectionService(UsersActivity.this);
        }


    }


    /*public void saveDataActivity(View view) {
        saveDataFunction();

    }*/

    public void saveDataFunction()
    {
        flagg=true;

        Intent saveIntent = new Intent(getApplicationContext(), SaveDataActivity.class);
        saveIntent.putExtra("msg",messages.toString());
        saveIntent.putExtra("name",nameFromIntent);
        startActivity(saveIntent);
       // Toast.makeText(this, "Users: File saved.", Toast.LENGTH_SHORT).show();
        //incomingMessages.setText("");
        messages = new StringBuilder();
        flagg=false;

    }

    public void makeGraph(View view) {

       makeGraphFunction();

    }

    public void makeGraphFunction()
    {
        Intent graphIntent = new Intent(getApplicationContext(), GraphActivity.class);
        graphIntent.putExtra("name", nameFromIntent);
        startActivity(graphIntent);
    }

    /*public void viewDataActivity(View view) {

        viewDataFunction();
    }*/

    public void viewDataFunction()
    {
        Intent viewDataIntent = new Intent(getApplicationContext(), ReadFileActivity.class);
        viewDataIntent.putExtra("name", nameFromIntent);
        startActivity(viewDataIntent);
    }

    @Override
    public void onClick(View view) {

    }


}

