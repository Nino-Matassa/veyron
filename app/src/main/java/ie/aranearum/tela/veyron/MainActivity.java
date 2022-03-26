package ie.aranearum.tela.veyron;

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

import ie.aranearum.tela.veyron.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    public static Stack<UIHistory> stack = new Stack<UIHistory>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    e.printStackTrace();
                } finally {
                    if(Database.exists(MainActivity.this)) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db = Database.getInstance(MainActivity.this, false, false);
                            }});
                        thread.start();
                        try {
                            thread.join();
                        } catch (Exception e) {
                            Log.d("MainActivity.onCreate", e.toString());
                        }
                    } else {
                        Database.setBuildFromScratch(true);
                        db = Database.getInstance(MainActivity.this, false, true);
                    }
                    UITerra uiTerra = new UITerra(MainActivity.this);
                }
            }
        }, Constants.delayMilliSeconds);
    }

    @Override
    public void onBackPressed() {
        if (stack.size() == 1) {
            this.moveTaskToBack(true);
            Toast.makeText(MainActivity.this, "Exiting Veyron", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "About!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}