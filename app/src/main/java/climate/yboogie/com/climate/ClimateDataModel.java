package climate.yboogie.com.climate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClimateDataModel {

    private String mDescription;
    private double mTemperature;
    private String msTemperature;
    private String mDateAndTime;
    private String mJustTime;

    public static ArrayList<ClimateDataModel> processResults(JSONObject response) {

        ArrayList<ClimateDataModel> cDataModels = new ArrayList();

        try {

            //cDataModel.mDescription = "There will be " + response.getJSONArray("list").getJSONObject(0).
            //getJSONArray("weather").getJSONObject(0).getString("description");

            //return cDataModel;

            JSONArray forecastListJSON = response.getJSONArray("list");

            for(int i = 0; i < 8; i++) {

                ClimateDataModel cDataModel = new ClimateDataModel();

                JSONObject triHourlyForecast = forecastListJSON.getJSONObject(i);

                cDataModel.mDescription = triHourlyForecast.getJSONArray("weather").getJSONObject(0).getString("description");

                double temperatureResult = triHourlyForecast.getJSONObject("main").getDouble("temp") - 273.15;
                int tempRoundedValue = (int) Math.rint(temperatureResult);

                cDataModel.msTemperature = Integer.toString(tempRoundedValue);

                cDataModel.mTemperature = temperatureResult;

                cDataModel.mDateAndTime = triHourlyForecast.getString("dt_txt");

                String[] dateAndTimeSplit = cDataModel.getDateAndTime().split("\\s");

                cDataModel.mJustTime = dateAndTimeSplit[1];

                cDataModels.add(cDataModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return cDataModels;
    }

    public String getDescription() {
        return mDescription;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public String getsTemperature() {
        return msTemperature + "°C";
    }

    public String getDateAndTime() {
        return mDateAndTime;
    }

    public String getJustTime() {
        return mJustTime;
    }


}
