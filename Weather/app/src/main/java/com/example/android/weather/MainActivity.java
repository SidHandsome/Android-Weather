package com.example.android.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static android.R.id.edit;
import static android.R.id.message;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static java.lang.Math.abs;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView weatherText;
    public class GetWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection;
            String result = "";
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                /*connection.getResponseCode();
                InputStream in=connection.getErrorStream();
                if (in == null) {
                    in = connection.getInputStream();
                }*/
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"weather couldn't be found 1",Toast.LENGTH_SHORT).show();

            }
            return "failed";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherText=(TextView)findViewById(R.id.weather_text_view);
        editText=(EditText)findViewById(R.id.edit_text);
    }

/*07-03 01:34:23.617 28944-28944/com.example.android.weather I/main:
        {"coord":{"lon":-0.13,"lat":51.51},"weather":[{"id":800,"main":"Clear",
        "description":"clear sky","icon":"01d"}],"base":"stations","main":
        {"temp":294.32,"pressure":1021,"humidity":40,"temp_min":293.15,"temp_max":295.15},
        "visibility":10000,"wind":{"speed":4.1,"deg":260},"clouds":{"all":0},"dt":1499023200,
        "sys":{"type":1,"id":5091,"message":0.0041,"country":"GB","sunrise":1498967341,"sunset":1499026807},
        "id":2643743,"name":"London","cod":200}*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String>weatherDetails=new ArrayList<String>();
        String s="";
        Double n=0.0;
        //hide keyboard
        InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        int itemThatWasClicked = item.getItemId();
        if (itemThatWasClicked == R.id.action_search) {
            GetWeather getWeather=new GetWeather();
            String result="";
            try {
                String inputAsUrl= URLEncoder.encode(editText.getText().toString(),"UTF-8");
                result=getWeather.execute(makeWeatherQuery(inputAsUrl)).get();
                JSONObject jsonObject=new JSONObject(result);

                //GETTING DATA FROM JSON ARRAY
                String weatherInfo=jsonObject.getString("weather");
                JSONArray arr=new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=arr.getJSONObject(i);
                    weatherDetails.add(jsonPart.getString("main"));
                    weatherDetails.add(jsonPart.getString("description"));
                }

                //GETTING DATA FROM JSON OBJECT
                weatherInfo=jsonObject.getString("main");
                JSONObject jsonPart=jsonObject.getJSONObject("main");

                s=jsonPart.getString("temp");
                n=abs(273-Double.parseDouble(s));
                weatherDetails.add(Integer.toString((int)Math.round(n))+"°C");

                weatherDetails.add(jsonPart.getString("pressure")+" hPa");

                weatherDetails.add(jsonPart.getString("humidity")+"%");

                s=jsonPart.getString("temp_min");
                n=abs(273-Double.parseDouble(s));
                weatherDetails.add(Integer.toString((int)Math.round(n))+"°C");

                s=jsonPart.getString("temp_max");
                n=abs(273-Double.parseDouble(s));
                weatherDetails.add(Integer.toString((int)Math.round(n))+"°C");

                //GETTING DATA FROM JSON OBJECT
                weatherInfo=jsonObject.getString("wind");
                jsonPart=jsonObject.getJSONObject("wind");
                weatherDetails.add(jsonPart.getString("speed")+"meter/sec");

                //GETTING DATA FROM JSON OBJECT
                weatherInfo=jsonObject.getString("clouds");
                jsonPart=jsonObject.getJSONObject("clouds");
                weatherDetails.add(jsonPart.getString("all")+"%");

                String message=weatherDetails.get(0)+" : "+weatherDetails.get(1)+
                "\nTemperature : "+weatherDetails.get(2)+
                "\nPressure : "+weatherDetails.get(3)+
                "\nHumidity : "+weatherDetails.get(4)+
                "\nMin Temperature : "+weatherDetails.get(5)+
                "\nMax Temperature : "+weatherDetails.get(6)+
                "\nWind Speed : "+weatherDetails.get(7)+
                "\nClouds : "+weatherDetails.get(8);
                if(message!=""){
                    weatherText.setText(message);
                }
                else{
                    Toast.makeText(getApplicationContext(),"weather couldn't be found2",Toast.LENGTH_SHORT).show();
                }
            }/* catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"weather couldn't be found3",Toast.LENGTH_SHORT).show();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"weather couldn't be found4",Toast.LENGTH_SHORT).show();
            }*/catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String makeWeatherQuery(String s){
        URL weatherURL=NetworkUtils.buildUrl(s);
        String s1=weatherURL.toString();
        return s1;
    }
}
