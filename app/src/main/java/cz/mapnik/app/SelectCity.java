package cz.mapnik.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import cz.mapnik.app.utils.Cities;

/**
 * Created by chaemil on 3.1.15.
 */
public class SelectCity extends Activity {

    private ListView lv;
    ArrayAdapter<String> adapter;
    EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        lv = (ListView) findViewById(R.id.city_list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.city_name, Cities.europe);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SelectCity.this, GuessActivity.class);
                i.putExtra(App.CUSTOM_LOCATION, String.valueOf(parent.getItemAtPosition(position)));
                startActivity(i);
                finish();
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SelectCity.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
