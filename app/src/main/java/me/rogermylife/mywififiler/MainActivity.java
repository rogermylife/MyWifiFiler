package me.rogermylife.mywififiler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private TextView textDevice;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WiFiDirectBroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    ArrayList<WifiP2pDevice> deviceList = new ArrayList<WifiP2pDevice>();

    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textDevice = (TextView) findViewById(R.id.textDevice);
        registerForContextMenu(textDevice);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void onCreateContextMenu(final ContextMenu menu,View v,ContextMenu.ContextMenuInfo m)
    {
        MenuInflater inflater = getMenuInflater();
        menu.add("sdjfie"+i);
        i++;
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("discover success");
                if (mReceiver.myPeerListListener.peers==null)
                    System.out.println("peer list null");
                else {
                    //mReceiver.myPeerListListener.peers
                    //deviceList = (ArrayList) mReceiver.myPeerListListener.peers.getDeviceList();
                    deviceList = new ArrayList<WifiP2pDevice>(mReceiver.myPeerListListener.peers.getDeviceList());
                    if (deviceList.isEmpty())
                        System.out.println("list is empty");
                    else {
                        for (WifiP2pDevice current : deviceList) {
                            System.out.println("find device address " + current.deviceName + " " + current.deviceAddress);
                            menu.add(current.deviceName);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int reasonCode) {
                System.out.println("discover failure");
            }
        });
        inflater.inflate(R.menu.context_menu,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        textDevice.setText("您選擇的是"+item.getTitle());

        return super.onContextItemSelected(item);
    }

}
