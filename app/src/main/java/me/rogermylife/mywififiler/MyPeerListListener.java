package me.rogermylife.mywififiler;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by RogeRxLaKer on 2016/8/7.
 */
public class MyPeerListListener implements  WifiP2pManager.PeerListListener{
    WifiP2pDeviceList peers;


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        this.peers=peers;
        System.out.println("this.peers = peers");
    }
}
