package ie.aranearum.tela.veyron;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    public static Stack<UIHistory> stack = new Stack<UIHistory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CSV csv = new CSV(MainActivity.this, false);
        csv.start();
        try {
            csv.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(Database.exists(MainActivity.this)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Database.setBuildFromScratch(false);
                        db = Database.getInstance(MainActivity.this, false, false);
                    }}).start();
            } else {
                Database.setBuildFromScratch(true);
                db = Database.getInstance(MainActivity.this, false, false);
                Database.setBuildFromScratch(false);
            }
            UITerra uiTerra = new UITerra(MainActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        if (stack.size() == 1) {
            this.moveTaskToBack(true);
            //UIMessage.toast(MainActivity.this, "Spirale - Moved to Background", Toast.LENGTH_LONG);
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
                default:
                    break;
            }
        }
    }
}