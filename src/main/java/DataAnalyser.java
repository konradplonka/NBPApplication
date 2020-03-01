import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class DataAnalyser implements DataAnalyserInterface{

    private static final String CURRENCY_PRICE_URL = "http://api.nbp.pl/api/exchangerates/rates/a";
    private static final String CURRENCY_PRICE_WITH_DATE_URL = "http://api.nbp.pl/api/exchangerates/rates/c";
    private static final String CURRENCY_PRICE_FROM_SELECTED_RANGE_URL = "http://api.nbp.pl/api/exchangerates/rates/a";
    private static final String GOLD_PRICE_URL= "http://api.nbp.pl/api/cenyzlota";
    private static String JSON_FORMAT = "?format=json";


    @Override
    public void getCurrentCurrencyPrice(String currencyCode) {

        currencyCode = currencyCode.toLowerCase();

        StringBuilder url = new StringBuilder();
        url.append(CURRENCY_PRICE_URL);
        url.append("/");
        url.append(currencyCode);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        double price;

        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonObject = jsonElement.getAsJsonObject();
            jsonArray = jsonObject.getAsJsonArray("rates");
            jsonObject = jsonArray.get(0).getAsJsonObject();

            price = jsonObject.get("mid").getAsDouble();

            System.out.println(String.format("%s price: %.2f PLN", currencyCode.toUpperCase(), price));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getAverageCurrencyPriceFromSelectedDate(String currencyCode, String date) {
        currencyCode = currencyCode.toLowerCase();

        StringBuilder url = new StringBuilder();
        url.append(CURRENCY_PRICE_WITH_DATE_URL);
        url.append("/");
        url.append(currencyCode);
        url.append("/");
        url.append(date);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        double buyPrice, sellPrice, avgPrice;
        try{
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonObject = jsonElement.getAsJsonObject();
            jsonArray = jsonObject.getAsJsonArray("rates");
            jsonObject = jsonArray.get(0).getAsJsonObject();

            sellPrice = jsonObject.get("bid").getAsDouble();
            buyPrice = jsonObject.get("ask").getAsDouble();
            avgPrice = (sellPrice + buyPrice) / 2;

            System.out.println(String.format("%s price: %.2f PLN",currencyCode, avgPrice));
        }
        catch (FileNotFoundException e){
            System.out.println("Type correct data!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getAverageCurrencyPriceFromSelectedDateRange(String currencyCode, String startDate, String endDate) {
        StringBuilder url = new StringBuilder();
        url.append(CURRENCY_PRICE_FROM_SELECTED_RANGE_URL);
        url.append("/");
        url.append(currencyCode);
        url.append("/");
        url.append(startDate);
        url.append("/");
        url.append(endDate);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        double sum = 0;
        double avgPrice;

        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonObject = jsonElement.getAsJsonObject();
            jsonArray = jsonObject.getAsJsonArray("rates");

            for(int i = 0; i < jsonArray.size(); i++){
                jsonObject = jsonArray.get(i).getAsJsonObject();
                sum += jsonObject.get("mid").getAsDouble();
            }

            avgPrice = sum / jsonArray.size();
            System.out.println(String.format("%s mean price: %.2f PLN",currencyCode, avgPrice));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void plotCurrencyPriceFromSelectedDateRange(String currencyCode, String startDate, String endDate, boolean averageValueFlag) {
        Map <String, Double> data = getMeanCurrencyPriceMap(currencyCode, startDate + "/" + endDate);
        SwingUtilities.invokeLater(() -> {
            PlotDrawer plot = new PlotDrawer("Currency NBP", "Average prices of " + currencyCode.toUpperCase(), "Date", "Price [PLN]", data, averageValueFlag);
            plot.setSize(1200, 800);
            plot.setLocationRelativeTo(null);
            plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            plot.setVisible(true);
        });
    }

    @Override
    public void getCurrentGoldPrice() {
        StringBuilder url = new StringBuilder();
        url.append(GOLD_PRICE_URL);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        double goldPrice;
        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonArray = jsonElement.getAsJsonArray();
            jsonObject = jsonArray.get(0).getAsJsonObject();

            goldPrice = jsonObject.get("cena").getAsDouble();
            System.out.println(String.format("Gold price: %.2f PLN", goldPrice));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void getGoldPriceFromSelectedDate(String date) {
        StringBuilder url = new StringBuilder();
        url.append(GOLD_PRICE_URL);
        url.append("/");
        url.append(date);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        double goldPrice;
        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonArray = jsonElement.getAsJsonArray();
            jsonObject = jsonArray.get(0).getAsJsonObject();

            goldPrice = jsonObject.get("cena").getAsDouble();
            System.out.println(String.format("Gold price: %.2f PLN",date, goldPrice));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void plotGoldPriceFromSelectedDateRange(String startDate, String endDate, boolean averageValueFlag) {
        Map<String, Double> data = getGoldPriceMap(startDate + "/" + endDate);

        SwingUtilities.invokeLater(() -> {
            PlotDrawer plot = new PlotDrawer("Currency NBP", "Gold prices", "Date", "Price [PLN]", data, averageValueFlag);
            plot.setSize(1200, 800);
            plot.setLocationRelativeTo(null);
            plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            plot.setVisible(true);
        });

    }
    private static Map<String, Double> getMeanCurrencyPriceMap(String currencyCode, String dataRange){
        /*
         * This method returns map with date as a key and currency price as a value
         * @param currencyName this is the currency code (ex. EUR, USD) used for retrieve data form web
         * @param date used for retrieve data from web
         */

        StringBuilder url = new StringBuilder();
        url.append(CURRENCY_PRICE_FROM_SELECTED_RANGE_URL);
        url.append("/");
        url.append(currencyCode);
        url.append("/");
        url.append(dataRange);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        Map <String, Double> data = new TreeMap<>();

        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonObject = jsonElement.getAsJsonObject();
            jsonArray = jsonObject.getAsJsonArray("rates");

            for(int i = 0; i < jsonArray.size(); i++){
                jsonObject = jsonArray.get(i).getAsJsonObject();
                data.put(jsonObject.get("effectiveDate").getAsString(), jsonObject.get("mid").getAsDouble());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static Map <String, Double> getGoldPriceMap(String dataRange){
        /*
         * This method returns map with date as a key and currency price as a value
         * @param dateRange used for retrieve data from web
         */

        StringBuilder url = new StringBuilder();
        url.append(GOLD_PRICE_URL);
        url.append("/");
        url.append(dataRange);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        Map <String, Double> data = new TreeMap<>();

        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonArray = jsonElement.getAsJsonArray();

            for(int i = 0; i < jsonArray.size(); i++){
                jsonObject = jsonArray.get(i).getAsJsonObject();
                data.put(jsonObject.get("data").getAsString(), jsonObject.get("cena").getAsDouble());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
