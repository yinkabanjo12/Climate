package climate.yboogie.com.climate;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String CLIMATE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    final String API_KEY = "4d483d1fd6788f53f4a9f33645c1dfc4";

    EditText editCityName;

    TextView mDescription;
    TextView mTemperature;
    Button sendCityButton;

    private Context context;

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editCityName = (EditText) findViewById(R.id.queryET);

        mDescription = (TextView) findViewById(R.id.weatherTV);
        mTemperature = (TextView) findViewById(R.id.temperatureTV);

        sendCityButton = (Button) findViewById(R.id.sendCityButton);

        context = this;

        mChart = (LineChart)findViewById(R.id.linechart);


    }

    //Method which fires the SEND button and takes the data entered into the ET

    public void sendMessage(View v) {

        String sCity = editCityName.getText().toString();
        getCityWeather(sCity);
    }

    //Method which catches this information and pieces it together with params; COMPLETE
    private void getCityWeather(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("mode", "json");
        params.put("appid", API_KEY);

        openWeatherRequest(params);
    }

    //Method which makes the 3rd Party API Call and handles the JSON response
    private void openWeatherRequest (RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(CLIMATE_URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("Weather", "Success! JSON: " + response.toString());
                //ClimateDataModel cDataModel = ClimateDataModel.rJSON(response);
                //Log.d("Success", "JSON Update UI " + cDataModel.getDescription());

                ArrayList<ClimateDataModel> cDataModels = new ArrayList();
                cDataModels = ClimateDataModel.processResults(response);

                Log.d("Success", "JSON Update UI " + cDataModels.size());

                //Y Values
                ArrayList<Entry> yValues = new ArrayList<>();

                for(int i = 0; i < cDataModels.size(); i++) {

                    Log.d("Different", "TriHourly Date and Time " + cDataModels.get(i).getDateAndTime());

                    Log.d("Different", "TriHourly Description of Weather " + cDataModels.get(i).getDescription());

                    Log.d("Different", "TriHourly Temperature " + cDataModels.get(i).getTemperature());

                    Log.d("Different", "TriHourly JustTime " + cDataModels.get(i).getJustTime());

                    //Add values to my Entries
                    yValues.add(new Entry(i, (float) cDataModels.get(i).getTemperature()));

                }

                updateUI(cDataModels);

                LineDataSet set1 = new LineDataSet(yValues, "Temperature in Celcius");

                //Manipulate the dataSet itself(everything with set1)
                set1.setFillAlpha(110);

                set1.setLineWidth(2f);

                //Data should appear curved now; SUCCESS
                set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                set1.setHighlightEnabled(true);

                set1.setDrawHighlightIndicators(true);
                set1.setHighLightColor(Color.BLACK);

                set1.setCircleRadius(6);
                set1.setCircleHoleRadius(3);

                set1.setValueTextSize(12);

                set1.setColor(Color.RED);
                set1.setCircleColor(Color.YELLOW);
                set1.setValueTextColor(Color.GREEN);


                //Set Up My x-axis
                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.RED);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawLabels(true);

                //Set Up My y-axis
                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setDrawAxisLine(true);
                leftAxis.setDrawGridLines(true);
                leftAxis.setTextSize(12f);
                leftAxis.setAxisMinimum(0f);

                mChart.getAxisRight().setEnabled(false);
                mChart.getDescription().setEnabled(false);

                //Create the mChart
                LineData data = new LineData(set1);

                mChart.setData(data);
                mChart.invalidate(); // refresh
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Weather", "Fail " + e.toString());
                Toast.makeText(MainActivity.this, "Please check your spelling and re-enter your chosen City", Toast.LENGTH_SHORT).show();

                Log.d("Weather", "Status code " + statusCode);
            }

        });

    }

    //Method which updates the UI

    public void updateUI(ArrayList<ClimateDataModel> cDataModels) {
        mDescription.setText(cDataModels.get(0).getDescription());
        mTemperature.setText(cDataModels.get(0).getsTemperature());
    }
}