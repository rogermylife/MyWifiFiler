package me.rogermylife.mywififiler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by RogeRxLaKer on 2016/8/9.
 */
public class FileServerAsyncTask extends AsyncTask {
//    private Context context;
//    private TextView statusText;

    //public FileServerAsyncTask(Context context, View statusText) {
    public FileServerAsyncTask() {
        System.out.println("finished create FileServer");
//        this.context = context;
//        this.statusText = (TextView) statusText;
    }

    @Override
    protected String doInBackground(Object... params) {
        System.out.println("accepting client???");
        try {

            /**
                         * Create a server socket and wait for client connections. This
                         * call blocks until a connection is accepted from a client
                         */

            ServerSocket serverSocket = new ServerSocket(8787);
            System.out.println("accepting client");
            Log.d("Tag onPostExecute", "QQQQQQQ"+String.valueOf(Thread.currentThread().getId()));
            Socket client = serverSocket.accept();
            System.out.println("GOT client");

            /**
                         * If this code is reached, a client has connected and transferred data
                         * Save the input stream from the client as a JPEG file
                         */
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + "MyWifiFiler" + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();
            FileOutputStream outputstream = new FileOutputStream(f);
            //copyFile(inputstream, new FileOutputStream(f));
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputstream.read(buf)) > 0) {
                outputstream.write(buf, 0, bytesRead);
            }
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            //Log.e(WiFiDirectActivity.TAG, e.getMessage());
            System.out.println("accept file failed");
            return null;
        }
    }

    /**
     * Start activity that can handle the JPEG image
     */
//    @Override
//    protected void onPostExecute(String result) {
//        if (result != null) {
//            statusText.setText("File copied - " + result);
//            Intent intent = new Intent();
//            intent.setAction(android.content.Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
//            context.startActivity(intent);
//        }
//    }
}
