package com.example.laurentiu.predictietext;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    public Drawable x;

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

        textHandler = new TextHandler();
        textField = (EditText) findViewById(R.id.editText);
        firstPrediction = (TextView) findViewById(R.id.textView);
        secondPrediction = (TextView) findViewById(R.id.textView2);
        thirdPrediction = (TextView) findViewById(R.id.textView3);
        categoryTitle = (TextView) findViewById(R.id.textView4);

        // Initially we start with this category - TO-DO: start with the last category (sharedPreferences)
        categoryTitle.setText("Medic");

        corpusParser = new CorpusParser();

        category = CorpusParser.Categories.Spital;
        setCategory(category);

        x = getResources().getDrawable(R.drawable.x);
        x.setBounds(0, 0, x.getIntrinsicWidth() / 3, x.getIntrinsicHeight() / 3);
        textField.setCompoundDrawables(null, null, x, null);
        textFieldMonitor(textField);
        textField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
            textField.append(thirdPrediction.getText() + " ");
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

    /**
     * Method to copy the current text to clipboard
     * @param v
     */
    public void copyTextToClipboard(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(textField.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this,
                    "Nu aţi scris nimic!", Toast.LENGTH_LONG).show();
        } else {
            ClipData clip = ClipData.newPlainText("copyToClipboard", (CharSequence) textField.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this,
                    "Copiat! Acum poţi insera oriunde.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to highlight the message from the text field
     * @param v
     */
    public void showMessage(View v) {
        if(textField.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this,
                    "Nu aţi scris nimic!", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Mesaj");
            CharSequence text = textField.getText().toString().isEmpty() ? "Nu aţi scris nimic in câmp!" : formatMessage();
            alertDialogBuilder.setMessage(text);
            alertDialogBuilder.show();
        }
    }

    /**
     * Method to export the message to SMS app
     * @param v
     */
    public void sendSMS(View v) {
        if(textField.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this,
                    "Nu aţi scris nimic!", Toast.LENGTH_LONG).show();
        } else {
            try {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); //Need to change the build to API 19

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                String message = formatMessage();
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);

                if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                {
                    sendIntent.setPackage(defaultSmsPackageName);
                }

                this.startActivity(sendIntent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to send e-mail
     * @param v
     */
    public void sendEmail(View v) {
        if(textField.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this,
                    "Nu aţi scris nimic!", Toast.LENGTH_LONG).show();
        } else {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_TEXT, formatMessage());
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to make first letter capital
     */
    public String formatMessage() {
        String message = textField.getText().toString();
        String messageSub = message.substring(1).toLowerCase();
        String substring = message.substring(0,1).toUpperCase();
        String newMessage = substring + messageSub;
        return newMessage;
    }

    public void textFieldMonitor(final EditText textField) {

        textField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == event.ACTION_DOWN)
                    if(event.getRawX() >= 950) {
                        textField.setText("");
                        return true;
                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.showSoftInput(textField, InputMethodManager.SHOW_IMPLICIT);
                    }
                return true;
            }
        });
        textField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
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
                if (!s.toString().contains(" ")) {
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
                        clearTextViews();
                    }
                } else {
                    String inputText = textField.getText().toString();
                    inputText = inputText.replace("  ", " ");
                    String[] tokens = inputText.split(" ");
                    String lastWord = textField.getText().toString();
                    String secondWord = null;
                    if (tokens.length == 1) {
                        lastWord = tokens[0];
                    } else if (tokens.length > 1) {
                        secondWord = tokens[tokens.length - 1];
                        lastWord = tokens[tokens.length - 2];
                    }

                    ArrayList bigramprediction = null;
                    if (!s.toString().endsWith(" "))
                        bigramprediction = textHandler.fetchBigramWords(bigrams, lastWord, secondWord);
                    else if (tokens.length > 1)
                        bigramprediction = textHandler.fetchBigramWords(bigrams, secondWord, null);
                    else
                        bigramprediction = textHandler.fetchBigramWords(bigrams, lastWord, null);

                    if (bigramprediction.size() == 0) {
                        Log.d("Predictions", "Results found in bigrams: 0");

                        // First case we have more than 1 word in textfield
                        bigramprediction = secondWord != null ? textHandler.fetchGrams(unigrams, secondWord, null) : textHandler.fetchGrams(unigrams, lastWord, null);
                        clearTextViews();

                        if (bigramprediction.size() >= 3) {
                            firstPrediction.setText((String) ((HashMap) bigramprediction.get(0)).get("gram"));
                            secondPrediction.setText((String) ((HashMap) bigramprediction.get(1)).get("gram"));
                            thirdPrediction.setText((String) ((HashMap) bigramprediction.get(2)).get("gram"));
                        } else if (bigramprediction.size() == 2) {
                            firstPrediction.setText((String) ((HashMap) bigramprediction.get(0)).get("gram"));
                            secondPrediction.setText((String) ((HashMap) bigramprediction.get(1)).get("gram"));
                            thirdPrediction.setText("");
                        } else if (bigramprediction.size() == 1) {
                            firstPrediction.setText((String) ((HashMap) bigramprediction.get(0)).get("gram"));
                            secondPrediction.setText("");
                            thirdPrediction.setText("");
                        }
                    } else if (bigramprediction.size() >= 3) {
                        Log.d("Predictions", "Results found in bigrams: 3");
                        firstPrediction.setText((String) bigramprediction.get(0));
                        secondPrediction.setText((String) bigramprediction.get(1));
                        thirdPrediction.setText((String) bigramprediction.get(2));
                    } else if (bigramprediction.size() == 2) {
                        Log.d("Predictions", "Results found in bigrams: 2");
                        firstPrediction.setText((String) bigramprediction.get(0));
                        secondPrediction.setText((String) bigramprediction.get(1));
                        ArrayList preds = textHandler.fetchGrams(unigrams, secondWord, null);
                        if (preds.size() > 0) {
                            String first = ((String) firstPrediction.getText()).replace(" ", "");
                            String second = ((String) secondPrediction.getText()).replace(" ", "");
                            if (((HashMap) preds.get(0)).get("gram").equals(first) || ((HashMap) preds.get(0)).get("gram").equals(second))
                                thirdPrediction.setText((String) ((HashMap) preds.get(1)).get("gram"));
                            else
                                thirdPrediction.setText((String) ((HashMap) preds.get(0)).get("gram"));
                        } else thirdPrediction.setText("");
                    } else if (bigramprediction.size() == 1) {
                        Log.d("Predictions", "Results found in bigrams: 1");
                        firstPrediction.setText((String) bigramprediction.get(0));
                        if (secondWord != null) {
                            ArrayList preds = textHandler.fetchGrams(unigrams, secondWord, null);
                            if (preds.size() > 1) {
                                String first = ((String) firstPrediction.getText()).replace(" ", "");
                                if (((HashMap) preds.get(0)).get("gram").equals(first)) {
                                    secondPrediction.setText((String) ((HashMap) preds.get(1)).get("gram"));
                                    thirdPrediction.setText((String) ((HashMap) preds.get(2)).get("gram"));
                                } else if (((HashMap) preds.get(1)).get("gram").equals(first)) {
                                    secondPrediction.setText((String) ((HashMap) preds.get(0)).get("gram"));
                                    thirdPrediction.setText((String) ((HashMap) preds.get(2)).get("gram"));
                                } else {
                                    secondPrediction.setText((String) ((HashMap) preds.get(0)).get("gram"));
                                    thirdPrediction.setText((String) ((HashMap) preds.get(1)).get("gram"));
                                }
                            } else {
                                thirdPrediction.setText("");
                                secondPrediction.setText("");
                            }
                        }
                    }
                }
            }
        });
    }

    private void clearTextViews() {
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
