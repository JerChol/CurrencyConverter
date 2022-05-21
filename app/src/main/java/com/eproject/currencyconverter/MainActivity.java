package com.eproject.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView xmlViewer,currToConvTo;
    //AppCompatSpinner spinnerCountry;
    EditText currToConv,currToConvRate,currToConvToRate;
    Button convBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String message = "Please use the conversion rate values below.";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        xmlViewer = findViewById(R.id.TextView_viewXML);
        //spinnerCountry = findViewById(R.id.spinner);

        //Method 1
        parseXML();

        /* This code will not be used anymore - removed feature
        CountryData countryData = new CountryData();
        List<String> countryNames = countryData.getCountryNamesArrayList();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,countryNames);
        spinnerCountry.setAdapter(arrayAdapter);
        */

        //The Calculations from currency to convert from and for currency to convert to goes here
        currToConv = findViewById(R.id.editTextCurrencyToChange);
        currToConvRate = findViewById(R.id.editTextCurrencyToChangeRate);
        currToConvTo = findViewById(R.id.editTextCurrencyToChangeTo);
        currToConvToRate = findViewById(R.id.editTextCurrencyToChangeToRate);
        convBtn = findViewById(R.id.buttonConvert);

        convBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double amountToConv = Double.parseDouble(currToConv.getText().toString());
                Double amountToConvRate = Double.parseDouble(currToConvRate.getText().toString());
                Double amountToConvToRate = Double.parseDouble(currToConvToRate.getText().toString());

                if(amountToConv != null || amountToConvRate != null || amountToConvToRate != null) {

                    Double toUSD = amountToConv / amountToConvRate;
                    Double toChosenCurrency = toUSD * amountToConvToRate;

                    currToConvTo.setText(toChosenCurrency.toString());
                }else if(amountToConv == null || amountToConvRate == null || amountToConvToRate == null){
                    Toast.makeText(MainActivity.this, "Please fill in all the details from the values below", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void parseXML() {

        XmlPullParserFactory parserFactory;
        try{

            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            InputStream inputStream = getAssets().open("currencyConvRates.xml");

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(inputStream,null);

            //Method 2
            processParsing(parser);

        }catch(XmlPullParserException | IOException e){
            e.printStackTrace();
        }

    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {

        ArrayList<Country> countries = new ArrayList<>();
        int eventType = parser.getEventType();
        Country currentCountry = null;

        while(eventType != XmlPullParser.END_DOCUMENT) {

            String eltName;

            switch(eventType) {

                case XmlPullParser.START_TAG:
                    eltName = parser.getName();

                    if("country".equals(eltName)){
                        currentCountry = new Country();
                        countries.add(currentCountry);
                    }else if(currentCountry != null){
                        if("name".equals(eltName)){
                            currentCountry.name = parser.nextText();
                        }else if("currency".equals(eltName)){
                            currentCountry.currency = parser.nextText();
                        }else if("currencyAbb".equals(eltName)){
                            currentCountry.currencyAbb = parser.nextText();
                        }else if("currencySymbol".equals(eltName)){
                            currentCountry.currencySymbol = parser.nextText();
                        }else if("convRate".equals(eltName)){
                            currentCountry.convRate = Double.parseDouble(parser.nextText());
                        }else if("revConvRate".equals(eltName)){
                            currentCountry.revConvRate = Double.parseDouble(parser.nextText());
                        }
                    }
                    break;

            }//End of Switch
            eventType = parser.next();
        }//End of while loop
        printCountries(countries);
    }

    private void printCountries(ArrayList<Country> countries) {

        StringBuilder builder = new StringBuilder();

        for(Country country : countries){
            builder.append(country.name).append("\n").
                    append(country.currency).append("\n").
                    append(country.currencyAbb).append("\n").
                    append(country.currencySymbol).append("\n").
                    append("Conversion Rate: ").append(country.convRate).append("\n").
                    append(country.revConvRate).append("\n\n");
        }
            xmlViewer.setText(builder.toString());
    }
}