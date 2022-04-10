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
    private SweetAlertDialog dialogConfirmation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!stack.empty()) {
            stack.clear();
            UITerra uiTerra = new UITerra(MainActivity.this);
        }
    }

    @Override
    public void onStart() {
        initialise();
        super.onStart();
    }

    public void initialise() {
        UIMessage.eyeCandy(MainActivity.this, "Initialising Veyron");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Thread csvThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CSV csv = new CSV(MainActivity.this);
                        csv.initialise();
                    }
                });
                csvThread.start();
                try {
                    csvThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (Database.exists(MainActivity.this)) {
                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = Database.getInstance(MainActivity.this);
                            }
                        }).start();
                    } catch (Exception /*| InterruptedException*/ e) {
                        e.printStackTrace();
                        Log.d("MainActivityThread", e.toString());
                    }
                } else {
                    Thread threadFullBuild = new Thread(new Runnable() { // Thread it so the system doesn't ask wait/cancel
                        @Override
                        public void run() {
                            Database.setBuildFromScratch(true);
                            db = Database.getInstance(MainActivity.this);
                        }});
                    threadFullBuild.start();
                    try {
                        threadFullBuild.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (stack.empty()) {
                    UITerra uiTerra = new UITerra(MainActivity.this);
                } else {
                    UIMessage.eyeCandy(MainActivity.this, null);
                }
            }
        }, Constants.delayMilliSeconds);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (stack.size() == 1) {
            this.moveTaskToBack(true);
            Toast.makeText(MainActivity.this, "Veyron is in the background now.", Toast.LENGTH_SHORT).show();
        } else {
            stack.pop();
            UIHistory uiHistory = stack.pop();
            UIMessage.eyeCandy(MainActivity.this, "Querying database");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (uiHistory.getUIX()) {
                        case Constants.UITerra:
                            stack.clear();
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
                                    uiHistory.region, uiHistory.country, uiHistory.ILambdaXHistory, uiHistory.fieldXName);
                            break;
                        default:
                            break;
                    }
                }
            }, Constants.delayMilliSeconds);
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
                message += "\nVersion: " + pInfo.versionName + "." + pInfo.getLongVersionCode() + " " + Constants.beta;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("About", e.toString());
            }
            dialogConfirmation = new SweetAlertDialog(this)
                    .setTitleText(title)
                    .setContentText(message)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialogConfirmation.dismiss();
                            Toast.makeText(MainActivity.this, "Website Link?", Toast.LENGTH_LONG).show();
                        }
                    });
            dialogConfirmation.show();
            return true;
        }

        if (id == R.id.invalidate) {
            dialogConfirmation = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.BUTTON_CONFIRM)
                    .setTitleText("Invalidate Database")
                    .setContentText("Rebuild the database?")
                    .setCancelText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialogConfirmation.dismiss();
                            new CSV(MainActivity.this).delete();
                            Database.delete(MainActivity.this);
                            Database.setBuildFromScratch(true);
                            initialise();
                        }
                    });
            dialogConfirmation.show();
        }

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
            dialogConfirmation = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.BUTTON_CONFIRM)
                    .setTitleText("Update Database")
                    .setContentText("Get new updates?")
                    .setCancelText("Cancel")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            UI.setMenubarTitleToUpdating(MainActivity.this, true);
                            dialogConfirmation.dismiss();
                            new CSV(MainActivity.this).delete();
                            initialise();
                        }
                    });
            dialogConfirmation.show();
        }

        return super.onOptionsItemSelected(item);
    }
}