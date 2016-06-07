package com.example.laurentiu.predictietext;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText textField;
    TextView firstPrediction, secondPrediction, thirdPrediction, categoryTitle;
    ArrayList unigrams, bigrams;
    TextHandler textHandler;
    CorpusParser.Categories category;
    CorpusParser corpusParser;
    String inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        textField = (EditText) findViewById(R.id.editText);
        firstPrediction = (TextView) findViewById(R.id.textView);
        secondPrediction = (TextView) findViewById(R.id.textView2);
        thirdPrediction = (TextView) findViewById(R.id.textView3);
        categoryTitle = (TextView) findViewById(R.id.textView4);

        textFieldMonitor(textField);

        // Initially we start with this category - TO-DO: start with the last category (sharedPreferences)
        categoryTitle.setText("Medic");

        corpusParser = new CorpusParser();

        category = CorpusParser.Categories.Spital;
        try {
            inputText = corpusParser.readFromAssets(getApplicationContext(), category);
        } catch (IOException e) {
            e.printStackTrace();
        }
        unigrams = corpusParser.computeUnigrams(inputText);
        bigrams = corpusParser.computeBigrams(inputText);
        textHandler = new TextHandler();
    }

    /**
     * Sets the category chosen by user(eg. Hotel, Spital..)
     * @param category
     */
    private void setCategory(CorpusParser.Categories category) {
        try {
            inputText = corpusParser.readFromAssets(getApplicationContext(), category);
        } catch (IOException e) {
            e.printStackTrace();
        }
        unigrams = corpusParser.computeUnigrams(inputText);
        bigrams = corpusParser.computeBigrams(inputText);
    }

    /**
     * First prediction TextView listener
     */
    public void tapTV1(View v) {
        String text = textField.getText().toString();
        int j = text.length()-1;
        if(text.endsWith(" ")) {
            textField.append((String) firstPrediction.getText() + " ");
        } else {
            for(int i=text.length()-1; i>=0; i--)
                if(text.charAt(i) == ' ') {
                    text = text.substring(0, i+1);
                    text = text + firstPrediction.getText() + " ";
                    textField.setText(text);
                    textField.setSelection(textField.getText().length());
                    break;
                } else if(i == 0){
                    text = firstPrediction.getText() + " ";
                    textField.setText(text);
                    textField.setSelection(textField.getText().length());
                }
        }
    }

    /**
     * Second prediction TextView listener
     */
    public void tapTV2(View v) {
        String text = textField.getText().toString();
        int j = text.length()-1;
        if(text.endsWith(" ")) {
            textField.append(secondPrediction.getText() + " ");
        } else {
            for(int i=text.length()-1; i>=0; i--)
                if(text.charAt(i) == ' ') {
                    text = text.substring(0, i+1);
                    text = text + secondPrediction.getText() + " ";
                    textField.setText(text);
                    textField.setSelection(textField.getText().length());
                    break;
                } else if(i == 0){
                    text = secondPrediction.getText() + " ";
                    textField.setText(text);
                    textField.setSelection(textField.getText().length());
                }
        }
    }

    /**
     * Third prediction TextView listener
     */
    public void tapTV3(View v) {
        String text = textField.getText().toString();
        int j = text.length()-1;
        if(text.endsWith(" ")) {
            textField.append( thirdPrediction.getText() + " ");
        } else {
            for(int i=text.length()-1; i>=0; i--)
                if(text.charAt(i) == ' ') {
                    text = text.substring(0, i+1);
                    text = text + ((String) thirdPrediction.getText()).concat(" ");
                    textField.setText(text);
                    textField.setSelection(textField.getText().length());
                    break;
                } else if(i == 0){
                    text = thirdPrediction.getText() + " ";
                    textField.setText(text);
                    textField.setSelection(textField.getText().length());
                }
        }
    }

    public void textFieldMonitor(final EditText textField) {
        textField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()) {
                    firstPrediction.setText("");
                    secondPrediction.setText("");
                    thirdPrediction.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                 if(!s.toString().contains(" ")) {
                    ArrayList<HashMap> predictions = textHandler.fetchGrams(unigrams, s.toString(), null);
                    if (predictions.size() >= 3) {
                        firstPrediction.setText((String) ((HashMap) predictions.get(0)).get("gram"));
                        secondPrediction.setText((String) ((HashMap) predictions.get(1)).get("gram"));
                        thirdPrediction.setText((String) ((HashMap) predictions.get(2)).get("gram"));
                    } else if (predictions.size() == 2) {
                        firstPrediction.setText((String) ((HashMap) predictions.get(0)).get("gram"));
                        secondPrediction.setText((String) ((HashMap) predictions.get(1)).get("gram"));
                        thirdPrediction.setText(" ");
                    } else if (predictions.size() == 1) {
                        firstPrediction.setText((String) ((HashMap) predictions.get(0)).get("gram"));
                        secondPrediction.setText(" ");
                        thirdPrediction.setText(" ");
                    } else if (predictions.size() == 0) {
                        clearTextVies();
                    }
                } else {
                     String inputText = textField.getText().toString();
                     inputText = inputText.replace("  ", " ");
                     String[] tokens = inputText.split(" ");
                     String lastWord = textField.getText().toString();
                     String secondWord = null;
                     if(tokens.length == 1) {
                         lastWord = tokens[0];
                     } else if(tokens.length>1) {
                         secondWord = tokens[tokens.length-1];
                         lastWord = tokens[tokens.length-2];
                     }

                     ArrayList bigramprediction = null;
                     if(!s.toString().endsWith(" "))
                        bigramprediction = textHandler.fetchBigramWords(bigrams, lastWord, secondWord);
                     else if(tokens.length > 1)
                         bigramprediction = textHandler.fetchBigramWords(bigrams, secondWord, null);
                     else
                        bigramprediction = textHandler.fetchBigramWords(bigrams, lastWord, null);

                     if (bigramprediction.size() == 0) {
                         Log.d("Predictions", "Results found in unigrams: 3");
                         bigramprediction = textHandler.fetchGrams(unigrams, secondWord, null);
                         clearTextVies();
                         if(bigramprediction.size() >= 3) {
                             firstPrediction.setText((String) ((HashMap) bigramprediction.get(0)).get("gram"));
                             secondPrediction.setText((String) ((HashMap) bigramprediction.get(1)).get("gram"));
                             thirdPrediction.setText((String) ((HashMap) bigramprediction.get(2)).get("gram"));
                         }
                     } else if(bigramprediction.size() >= 3) {
                         Log.d("Predictions", "Results found in bigrams: 3");
                         firstPrediction.setText((String) bigramprediction.get(0));
                         secondPrediction.setText((String)bigramprediction.get(1));
                         thirdPrediction.setText((String)bigramprediction.get(2));
                     } else if(bigramprediction.size() == 2) {
                         Log.d("Predictions", "Results found in bigrams: 2");
                         firstPrediction.setText((String) bigramprediction.get(0));
                         secondPrediction.setText((String)bigramprediction.get(1));
                         ArrayList preds = textHandler.fetchGrams(unigrams, secondWord, null);
                         if(preds.size() > 0)
                            thirdPrediction.setText((String) ((HashMap)preds.get(0)).get("gram"));
                         else thirdPrediction.setText("");
                     } else if(bigramprediction.size() == 1) {
                         Log.d("Predictions", "Results found in bigrams: 1");
                         firstPrediction.setText((String) bigramprediction.get(0));
                         ArrayList preds = textHandler.fetchGrams(unigrams, secondWord, null);
                         if(preds.size() > 1) {
                             secondPrediction.setText((String) ((HashMap) preds.get(0)).get("gram"));
                             thirdPrediction.setText((String) ((HashMap) preds.get(1)).get("gram"));
                         } else {
                             thirdPrediction.setText("");
                             secondPrediction.setText("");
                         }
                     }
                 }
            }
        });
    }

    private void clearTextVies() {
        firstPrediction.setText(" ");
        secondPrediction.setText(" ");
        thirdPrediction.setText(" ");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hotel) {
            categoryTitle.setText("Hotel");
            category = CorpusParser.Categories.Hotel;
            setCategory(CorpusParser.Categories.Hotel);
            item.setChecked(true);
        } else if (id == R.id.nav_spital) {
            categoryTitle.setText("Medic");
            setCategory(CorpusParser.Categories.Spital);
            category = CorpusParser.Categories.Spital;
            item.setChecked(true);
        } else if (id == R.id.nav_cumparaturi) {
            categoryTitle.setText("Cumpărături");
            category = CorpusParser.Categories.Cumparaturi;
            setCategory(CorpusParser.Categories.Cumparaturi);
            item.setChecked(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
