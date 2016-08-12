package me.rogermylife.mywififiler;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private TextView textDevice;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WiFiDirectBroadcastReceiver mReceiver;
    MyConnectionListener myConnectionlistener;
    IntentFilter mIntentFilter;
    ArrayList<WifiP2pDevice> deviceList = new ArrayList<WifiP2pDevice>();
    WifiP2pDevice targetDevice;
    WifiP2pInfo targetDeviceInfo;
    Uri selectedFileUri;
    TextView textView2;
    Context context;

    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView2 = (TextView)findViewById(R.id.textView2);
        context = this.getApplicationContext();
        setSupportActionBar(toolbar);


        textDevice = (TextView) findViewById(R.id.textDevice);
        registerForContextMenu(textDevice);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        myConnectionlistener = new MyConnectionListener();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

//        FileSenderAsyncTask fileSender = new FileSenderAsyncTask();
//        fileSender.execute();
//        FileServerAsyncTask fileServer = new FileServerAsyncTask();
//        fileServer.execute();


        Button chooseFileButton = (Button)this.findViewById(R.id.chooseFileButton);

        assert chooseFileButton != null;
        chooseFileButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // 建立 "選擇檔案 Action" 的 Intent
                Intent intent = new Intent( Intent.ACTION_PICK );

                // 過濾檔案格式
                //intent.setType( "image/*" );
                intent.setType( "*/*" );

                // 建立 "檔案選擇器" 的 Intent  (第二個參數: 選擇器的標題)
                Intent destIntent = Intent.createChooser( intent, "選擇檔案" );

                // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
                startActivityForResult( destIntent, 0 );
            }
        });


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
                        int id=87;
                        for (WifiP2pDevice current : deviceList) {
                            System.out.println("find device address " + current.deviceName + " " + current.deviceAddress);
                            menu.add(0,id++,0,current.deviceName);
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
        //item.getOrder();
        System.out.println(item.getItemId());
        if(deviceList.isEmpty()) {
            System.out.println("deviceList is empty");
            return super.onContextItemSelected(item);
        }
        //textDevice.setText("您選擇的是"+item.getTitle()+item.getOrder());
        targetDevice = deviceList.get(item.getItemId()-87);
        textDevice.setText("您選擇的是"+targetDevice.deviceName + targetDevice.deviceAddress);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = targetDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                System.out.println("connect success");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("connect failed");
            }
        });
        return super.onContextItemSelected(item);
    }


    public void sendFile(View view)
    {
        System.out.println("sendFile");
        //this.getApplicationContext();
        //FileClientAsyncTask fileClient = new FileClientAsyncTask(this.getApplicationContext(),targetDevice.deviceAddress,selectedFileUri);
        //fileClient.execute();
        //FileSenderAsyncTask fileSender = new FileSenderAsyncTask();
        //fileSender.execute();
        Intent serviceIntent = new Intent(this, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, selectedFileUri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                targetDeviceInfo.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8787);
        startService(serviceIntent);

        System.out.println("sendFile tread gogo  "+selectedFileUri.toString()+"     "+targetDeviceInfo.groupOwnerAddress.getHostAddress());
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("[MainActivity] leave me alone");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("[MainActivity] LET ME GOOOOOOOOOOOO");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // 有選擇檔案
        if ( resultCode == RESULT_OK )
        {
            // 取得檔案的 Uri
            Uri uri = data.getData();
            if( uri != null )
            {
                // 利用 Uri 顯示 ImageView 圖片
                ImageView iv = (ImageView)this.findViewById(R.id.imageView);
                iv.setImageURI( uri );
                uri.getEncodedPath();

                System.out.println( "selectFile:   "+uri.getEncodedPath() );
                selectedFileUri = uri;
            }
            else
            {
                System.out.println("selectFile:   "+"無效的檔案路徑 !!");
                selectedFileUri =null;
            }
        }
        else
        {
            System.out.println("selectFile:   "+"取消選擇檔案 !!");
            selectedFileUri = null;
        }
    }

    class MyConnectionListener implements WifiP2pManager.ConnectionInfoListener {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            targetDeviceInfo = info;
            // The owner IP is now known.
            System.out.println("[MainActivity] " + ((info.isGroupOwner == true) ? " isGroupOwner YES " : " isGroupOwner NO "));

            // InetAddress from WifiP2pInfo struct.
            System.out.println("[MainActivity] " + "Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

            // After the group negotiation, we assign the group owner as the file
            // server. The file server is single threaded, single connection server
            // socket.
            if (info.groupFormed && info.isGroupOwner) {
                new FileServerAsyncTask().execute();
            } else if (info.groupFormed) {
                // The other device acts as the client. In this case, we enable the
                // get file button.
            }
        }
    }
}

