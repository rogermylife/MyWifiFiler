package me.rogermylife.mywififiler;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by RogeRxLaKer on 2016/8/9.
 */
public class FileClientAsyncTask extends AsyncTask <Void,Void,String>  {
    private Context context;
    String host;
    Uri selectedFileUri;
    //private TextView statusText;

    //public FileServerAsyncTask(Context context, View statusText) {
    public FileClientAsyncTask(Context context,String host,Uri selectedFileUri) {
        System.out.println("finished create FileClient");
        this.context = context;
        this.host = host;
        this.selectedFileUri = selectedFileUri;

    }
    @Override
    protected void onPreExecute()
    {
        System.out.println("file client prepare sendfile");
    }
    @Override
    protected String doInBackground(Void... params) {
        System.out.println("YOOOOOOHOOOOOOOOO");
        Log.d("Tag onPostExecute", "MOTHER FUCKER"+String.valueOf(Thread.currentThread().getId()));
        return "send success";

    }

    @Override
    protected void onPostExecute(String result)
    {
        System.out.println(result);
    }

//    void onPostExecute (String abc) {
//        //執行後 完成背景任務
//        //super.onPostExecute(bitmap);
//        Log.v("Tag onPostExecute", String.valueOf(Thread.currentThread().getId()));
//        System.out.println("mother");
//    }
}
