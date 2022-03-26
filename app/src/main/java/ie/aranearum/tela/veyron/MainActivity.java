package ie.aranearum.tela.veyron;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import java.util.Stack;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ie.aranearum.tela.veyron.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    public static Stack<UIHistory> stack = new Stack<UIHistory>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static boolean restartingFromBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(restartingFromBackground)
            return;

        restartingFromBackground = true;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UIMessage.eyeCandy(MainActivity.this, "Initialising Veyron");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CSV csv = new CSV(MainActivity.this, false);
                csv.start();
                try {
                    csv.join();
                } catch (InterruptedException e) {
                    Log.d("MainActivity.CSV", e.toString());
                }
                if (Database.exists(MainActivity.this)) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = Database.getInstance(MainActivity.this, false, false);
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (Exception e) {
                        Log.d("MainActivity.Database", e.toString());
                    }
                } else {
                    Database.setBuildFromScratch(true);
                    db = Database.getInstance(MainActivity.this, false, true);
                }
                UITerra uiTerra = new UITerra(MainActivity.this);
            }
        }, Constants.delayMilliSeconds);
    }

    @Override
    public void onBackPressed() {
        if (stack.size() == 1) {
            this.moveTaskToBack(true);
            Toast.makeText(MainActivity.this, "Veyron is in the background now.", Toast.LENGTH_SHORT).show();
        } else {
            stack.pop();
            UIHistory uiHistory = stack.pop();
            switch (uiHistory.getUIX()) {
                case Constants.UITerra:
                    new UITerra(MainActivity.this);
                    break;
                case Constants.UIRegion:
                    new UIRegion(MainActivity.this);
                    break;
                case Constants.UICountry:
                    new UICountry(MainActivity.this, uiHistory.getRegionId());
                    break;
                case Constants.UICountryX:
                    new UICountryX(MainActivity.this, uiHistory.getCountryId());
                    break;
                case Constants.UIFieldXHistory:
                    new UIFieldXHistory(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId(),
                            uiHistory.region, uiHistory.country, uiHistory.ILambdaXHistory, uiHistory.fieldXName, uiHistory.executeSQL);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            String title = "Project Veron";
            String message = "COVID-19 statistical analysis using OWID data.";
            message += "\n Written: January 1, 2022";
            message += "\nBy: Nino Matassa";
            try {
                PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
                message += "\nVersion: " + pInfo.versionName + " " + Constants.beta;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("About", e.toString());
            }
            new SweetAlertDialog(this)
                    .setTitleText(title)
                    .setContentText(message)
                    .show();
            return true;
        }

        /*if(id == R.id.invalidate) {
            UIMessage.eyeCandy(MainActivity.this, "Rebuilding Veyron");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    db = Database.getInstance(MainActivity.this, true, false);
                    UITerra uiTerra = new UITerra(MainActivity.this);
                }
            }, Constants.delayMilliSeconds);
        }*/

        if (id == R.id.home) {
            UIMessage.eyeCandy(MainActivity.this, "Home " + Constants.house);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stack.clear();
                    new UITerra(MainActivity.this);
                }
            }, Constants.delayMilliSeconds);
        }

        if (id == R.id.update) {
            UIMessage.eyeCandy(MainActivity.this, "Repopulating Veyron");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    db = Database.getInstance(MainActivity.this, false, true);
                    UITerra uiTerra = new UITerra(MainActivity.this);
                }
            }, Constants.delayMilliSeconds);
        }

        return super.onOptionsItemSelected(item);
    }
}