package ie.aranearum.tela.veyron;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import java.util.Stack;

import ie.aranearum.tela.veyron.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private SQLiteDatabase db = null;

    public static Stack<UIHistory> stack = new Stack<UIHistory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> {
            Integer versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            Snackbar.make(view, "Mailing Overview to nino.matassa@gmail.com. Version: " + versionName + " " + versionCode.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
        // Download request
        CSV csv = new CSV(MainActivity.this, false);
        csv.start();
        try {
            csv.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            db = Database.getInstance(MainActivity.this, false, false);
        }
        //UIMessage.informationBox(MainActivity.this, "Overview");
        UITerra uiTerra = new UITerra(MainActivity.this);
        //UIRegion uiRegion = new UIRegion(MainActivity.this);

        //Toast.makeText(MainActivity.this, "UITerra", Toast.LENGTH_LONG).show();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (stack.size() == 1) {
            this.moveTaskToBack(true);
            UIMessage.toast(MainActivity.this, "Spirale - Moved to Background", Toast.LENGTH_LONG);
        }
        else {
            stack.pop();
            UIHistory uiHistory = stack.pop();
            switch (uiHistory.getUIX()) {
                case Constants.UITerra:
                    //bCallUITerra = true;
                    //new CSV(MainActivity.this).getDataFiles(false);
                    new UITerra(MainActivity.this);
                    break;
                case Constants.UIRegion:
                    new UIRegion(MainActivity.this);
                    break;
                /*case Constants.UICountry:
                    new UICountry(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UICountryByRegion:
                    new UICountryByRegion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraTotalCases:
                    new UITerraTotalCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraTotalDeaths:
                    new UITerraTotalDeaths(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraCase24H:
                    new UITerraCase24H(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraCase7D:
                    new UITerraCase7D(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraDeath24H:
                    new UITerraDeath24H(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraDeath7D:
                    new UITerraDeath7D(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraTotalInfected:
                    new UITerraTotalInfected(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraActiveCases:
                    new UITerraActiveCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UIActiveCases:
                    new UIActiveCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraActiveCasesPerX:
                    new UITerraActiveCasesPerX(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraRNought:
                    new UITerraRNought(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UITerraFullyVaccinated:
                    new UITerraFullyVaccinated(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;
                case Constants.UIActiveCaseAverage:
                    new UIActiveCaseAverage(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
                    break;*/
                default:
            }
        }
    }
}