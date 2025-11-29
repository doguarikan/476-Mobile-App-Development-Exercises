package com.example.currencyconverter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText valueInput;
    TextView valueOutput;
    NumberPicker pickerLeft;
    NumberPicker pickerRight;
    Button convert;

    HashMap<String, Double> rateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        valueInput = findViewById(R.id.valueInput);
        valueOutput = findViewById(R.id.valueOutput);
        pickerLeft = findViewById(R.id.pickerLeft);
        pickerRight = findViewById(R.id.pickerRight);
        convert = findViewById(R.id.convert);

        String[] currencies = {
                "\uD83C\uDDFA\uD83C\uDDF8 USD",
                "\uD83C\uDDEA\uD83C\uDDFA EUR",
                "\uD83C\uDDEC\uD83C\uDDE7 GBP",
                "\uD83C\uDDF9\uD83C\uDDF7 TRY",
                "\uD83C\uDDE8\uD83C\uDDE6 CAD",
                "\uD83C\uDDFA\uD83C\uDDE6 UAH"
        };

        pickerLeft.setMinValue(0);
        pickerLeft.setMaxValue(currencies.length - 1);
        pickerLeft.setDisplayedValues(currencies);

        pickerRight.setMinValue(0);
        pickerRight.setMaxValue(currencies.length - 1);
        pickerRight.setDisplayedValues(currencies);

        pickerRight.setWrapSelectorWheel(true);

        new HttpAsyncTask().execute("https://api.currencyfreaks.com/v2.0/rates/latest?apikey=51ff5d96159148d3923591324bdddb66&format=xml");

        convert.setOnClickListener(v -> {
            if(rateMap.isEmpty())
                return ;

            String left = getCurrencyCode(pickerLeft.getDisplayedValues()[pickerLeft.getValue()]);
            String right = getCurrencyCode(pickerRight.getDisplayedValues()[pickerRight.getValue()]);

            String inputStr = valueInput.getText().toString();
            if(inputStr.isEmpty()) inputStr = "0";

            double inputValue = Double.parseDouble(inputStr);

            double leftRate = rateMap.get(left);
            double rightRate = rateMap.get(right);

            double usdValue = inputValue / leftRate;
            double result = usdValue * rightRate;

            valueOutput.setText(String.format("%.4f", result));
        });
    }

    private String getCurrencyCode(String text){
        return text.substring(text.length()-3);
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute(){}
        protected String doInBackground(String... urls) {
            try {
                return HttpGet(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                XMLParser(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String HttpGet(String myUrl) throws IOException {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            InputStream inputStream = conn.getInputStream();
            return converInputStreamToString(inputStream);
        }
        private String converInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            inputStream.close();
            return result;
        }
    }

    public void XMLParser(String result) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(result));

        int eventType = xpp.getEventType();
        String currencyCode = "";
        String rateValue = "";

        while(eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && xpp.getDepth() == 3) {
                currencyCode = xpp.getName();
                if (currencyCode.startsWith("_")) {
                    currencyCode = currencyCode.substring(1);
                }
            } else if (eventType == XmlPullParser.TEXT) {
                rateValue = xpp.getText();
            } else if (eventType == XmlPullParser.END_TAG && xpp.getDepth() == 3) {
                try {
                    if (!rateValue.isEmpty())
                        rateMap.put(currencyCode, Double.parseDouble(rateValue));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            eventType = xpp.next();
        }
    }
}
