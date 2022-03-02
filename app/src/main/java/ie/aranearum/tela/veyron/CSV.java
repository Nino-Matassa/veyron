package ie.aranearum.tela.veyron;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.TimeZone;

public class CSV extends Thread {
    private boolean invalidate = false;
    private  String csvFileName = null;
    private  File csvFile = null;

    public CSV(@NonNull Context context, boolean invalidate) {
        this.invalidate = invalidate;
        this.csvFileName = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
        this.csvFile = new File(csvFileName);
    }

    public void run() {
        super.run();
        if(!csvFile.exists()){
            boolean b = download();
        } else {
            boolean b = downloadRequest();
        }
    }

    private boolean outdated() {
        try {
            Timestamp csvTimeStamp = new Timestamp(csvFile.lastModified());

            LocalDate csvLastModified = csvTimeStamp.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
            LocalDate today = LocalDate.now();

            if(today.compareTo(csvLastModified) > 0) {
                return true;
            }
        } catch (Exception e) {
            Log.d("MainActivity", e.toString());
            return false;
        }
        return false;
    }

    private boolean downloadRequest() {
        if (invalidate || outdated()) {
            boolean b = archive();
            b = delete();
            b = download();
        }
        return true;
    }

    private boolean archive() {
        return true;
    } // Cannot read/write to external storage so archiving unresolved for now

    private boolean delete() {
        if (csvFile.exists()) {
            try {
                boolean b = csvFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean download() {
        try {
            ReadableByteChannel readChannel = Channels.newChannel(new URL(Constants.OWIDCSV).openStream());
            FileOutputStream fileOS = new FileOutputStream(csvFileName);
            FileChannel writeChannel = fileOS.getChannel();
            writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
            writeChannel.close();
            readChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}



