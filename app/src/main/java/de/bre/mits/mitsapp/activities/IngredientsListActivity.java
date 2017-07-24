package de.bre.mits.mitsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.bre.mits.mitsapp.R;
import de.bre.mits.mitsapp.util.Helpermethods;
import de.bre.mits.mitsapp.util.RestConnector;
import de.bre.mits.mitsapp.util.Settings;
import de.bre.mits.mitsapp.ingredientsList.CardModel;
import de.bre.mits.mitsapp.ingredientsList.CardsAdapter;

public class IngredientsListActivity extends AppCompatActivity {

    private static final String TAG = "IngredientsListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);

        ListView lvCards = (ListView) findViewById(R.id.list_cards);
        CardsAdapter adapter = new CardsAdapter(this);

        lvCards.setAdapter(adapter);
        try {
            populateCardModel(adapter);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        //fa button for adding new ingredient only for head chef with username "chef"
        if (Helpermethods.getpref().getString("username", "").equals("chef")) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newIngFloatingActionButton);
            fab.setVisibility(fab.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    switchToNewIngredientActivity();
                }
            });
        }
    }


    /**
     * refresh activity because provider
     * could have new data to display
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /**
     * finish activity on back pressed twice
     */
    @Override
    public void onBackPressed() {
        if (Helpermethods.getBackPressedTimestamp()+ Settings.DELAY_FOR_EXIT > System.currentTimeMillis()) finish();
        Toast.makeText(getBaseContext(), R.string.onBackPressedLogOffToast, Toast.LENGTH_SHORT).show();
        Helpermethods.setBackPressedTimestamp(System.currentTimeMillis());
    }

    /**
     * reads form data provider and creates cards for ui
     * @param adapter from this activity
     * @throws JSONException if JSON from provider
     */
    private void populateCardModel(CardsAdapter adapter) throws JSONException {
        JSONArray jArr;
        if(Helpermethods.getpref().getString("username", "").equals("manager")) {
            jArr = RestConnector.getRessource(Settings.RESOURCE_CRIT_STOCK_INFO);
        } else {
            jArr = RestConnector.getRessource(Settings.RESOURCE_STOCK_MODIFICATION_AND_INFO);
        }

        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jobj = (JSONObject) jArr.get(i);
            String id = jobj.getString("id");
            String bezeichnung = jobj.getString("bezeichnung");

            JSONObject bestand = (JSONObject) jobj.get("bestand");
            String menge = bestand.getString("menge");
            String mindestbestand = bestand.getString("mindestbestand");
            String mengeneinheit = bestand.getString("mengeneinheit");
            //add new card with gathered info
            adapter.add(new CardModel(id, bezeichnung, mindestbestand, menge, mengeneinheit));
        }
    }

    /**
     * Called when the user taps the Add floating action button (fab)
     */
    private void switchToNewIngredientActivity() {
        Intent intent = new Intent(this, NewIngredientActivity.class);
        startActivity(intent);
    }
}
