package de.bre.mits.mitsapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.springframework.http.HttpStatus;

import de.bre.mits.mitsapp.R;
import de.bre.mits.mitsapp.util.RestConnector;
import de.bre.mits.mitsapp.util.Settings;

public class NewIngredientActivity extends AppCompatActivity {

    private EditText newIngName;
    private EditText newIngAmount;
    private EditText newIngCritAmount;
    private Spinner newIngQuantityUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ingredient);
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_ing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_ingredient:
                saveNewIngredient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNewIngredient() {
        //collect data to send
        String name = newIngName.getText().toString();
        String amount = newIngAmount.getText().toString();
        String critAmount = newIngCritAmount.getText().toString();
        String quantityUnit = newIngQuantityUnit.getSelectedItem().toString();

        //prepare uri to send to data provider
        String uri = Settings.RESOURCE_ADD_NEW_INGREDIENT + "/?kategorie=none&menge=" + amount +
                "&bezeichnung=" + name + "&mindestbestand=" + critAmount + "&einheit=" + quantityUnit;

        HttpStatus response = RestConnector.putRessource(uri);

        //feedback to user
        Toast.makeText(this,"Antwort des Servers: " +  response.getReasonPhrase(),
                Toast.LENGTH_LONG).show();
        if (response.equals(HttpStatus.CREATED)) finish();
    }


    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Settings.ACTION_BAR_FONT_COLOR);


        newIngName = (EditText) findViewById(R.id.newIngNameField);

        newIngAmount = (EditText) findViewById(R.id.newIngAmountField);

        newIngCritAmount = (EditText) findViewById(R.id.newIngCritAmountField);

        newIngQuantityUnit = (Spinner) findViewById(R.id.newIngQuantityUnitSpinner);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_newIngQuantityUnit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newIngQuantityUnit.setAdapter(adapter);
    }
}



