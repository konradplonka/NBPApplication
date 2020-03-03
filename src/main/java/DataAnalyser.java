import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DataAnalyser implements DataAnalyserInterface{

    private static final String CURRENCY_PRICE_URL = "http://api.nbp.pl/api/exchangerates/rates/a";
    private static final String CURRENCY_PRICE_WITH_DATE_URL = "http://api.nbp.pl/api/exchangerates/rates/c";
    private static final String CURRENCY_PRICE_FROM_SELECTED_RANGE_URL = "http://api.nbp.pl/api/exchangerates/rates/a";
    private static final String GOLD_PRICE_URL= "http://api.nbp.pl/api/cenyzlota";
    private static String JSON_FORMAT = "?format=json";


    @Override
    public void getCurrentCurrencyPrice(String currencyCode) {
        /*
         * This method is used to get current currency price
         * @param currencyCode This is the currency code for which we want to get data.
         */

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
        /*
         * This method gets average currency price for selected date
         * @param currencyCode This is the currency code for which we want to get data
         * @param date This is the date for which we want to get currency price.
         */
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
        /*
         * This method gets the average currency price for selected date range
         * @param currencyCode This is the currency code for which we want to get data
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range.
         */
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
        /*
         * This method plots currency price for selected date range
         * @param currencyCode This is the currency code for which we want to get data
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range
         * @param averageValueFlag This is the flag which describes if we want to add to the plot average currency value for selected data range
         */

        Map <String, Double> data = getAverageCurrencyPriceMap(currencyCode, startDate + "/" + endDate);
        SwingUtilities.invokeLater(() -> {
            PlotDrawer plot = new PlotDrawer("Currency NBP", "Average prices of " + currencyCode.toUpperCase(), "Date", "Price [PLN]", data, averageValueFlag);
            plot.setSize(1200, 800);
            plot.setLocationRelativeTo(null);
            plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            plot.setVisible(true);
        });
    }

    @Override
    public void getMinCurrencyPrice(String currencyCode, String startDate, String endDate) {
        /*
         * This method displays min currency price between startDate and endDate
         * @param currencyCode This is the currency code for which we want to get data
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range
         */

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date start;
        Date end;
        Double minValue = 0.0;
        String minDate = "";


        try {
            start = simpleDateFormat.parse(startDate);
            end = simpleDateFormat.parse(endDate);

            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(start);

            HashMap <String, Double> hashMap = new HashMap<>();

            while (!gregorianCalendar.getTime().after(end)){
                Date date = gregorianCalendar.getTime();
                Double priceValue = getCurrencyPriceforMinMax(currencyCode,simpleDateFormat.format(date).toString());

                if(priceValue != 0.0){
                    hashMap.put(simpleDateFormat.format(date).toString(), priceValue);
                    // System.out.println(priceValue);
                }
                gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            minValue = Collections.min(hashMap.values());

            for (Map.Entry<String, Double> entry : hashMap.entrySet()){
                if(entry.getValue() == minValue){
                    minDate = entry.getKey();
                }
            }

            System.out.println(String.format("Min %s: %.2f PLN", minDate, minValue));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private double getCurrencyPriceforMinMax(String currencyCode, String date){
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
        double avgPrice = 0.0;
        double buyPrice, sellPrice;
        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonObject = jsonElement.getAsJsonObject();
            jsonArray = jsonObject.getAsJsonArray("rates");
            jsonObject = jsonArray.get(0).getAsJsonObject();

            sellPrice = jsonObject.get("bid").getAsDouble();
            buyPrice = jsonObject.get("ask").getAsDouble();
            avgPrice = (sellPrice + buyPrice) / 2;

        } catch (IOException e) {
            return 0.0;
        }

        return avgPrice;
    }

    @Override
    public void getMaxCurrencyPrice(String currencyCode, String startDate, String endDate) {
        /*
         * This method displays max currency price between startDate and endDate
         * @param currencyCode This is the currency code for which we want to get data
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range
         */

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date start;
        Date end;
        Double maxValue = 0.0;
        String minDate = "";
        try {
            start = simpleDateFormat.parse(startDate);
            end = simpleDateFormat.parse(endDate);

            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(start);

            HashMap <String, Double> hashMap = new HashMap<>();

            while (!gregorianCalendar.getTime().after(end)){
                Date date = gregorianCalendar.getTime();
                Double priceValue = getCurrencyPriceforMinMax(currencyCode,simpleDateFormat.format(date).toString());

                if(priceValue != 0.0){
                    hashMap.put(simpleDateFormat.format(date).toString(), priceValue);
                    // System.out.println(priceValue);
                }
                gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            maxValue = Collections.max(hashMap.values());

            for (Map.Entry<String, Double> entry : hashMap.entrySet()){
                if(entry.getValue() == maxValue){
                    minDate = entry.getKey();
                }
            }

            System.out.println(String.format("Max %s: %.2f PLN", minDate, maxValue));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getSortedCurrencies(String currencyCodes, String date) {
        currencyCodes = currencyCodes.toLowerCase();
        String [] cCodes = currencyCodes.split(",");
        HashMap <String, Double> hashMap = new HashMap<>();

        double diff = 0;
        for(String code: cCodes){
            diff = getDifferenceBetweenSellAndBuyOnSelectedDate(code, date);
            if(diff!=0){
                DecimalFormat decimalFormat = new DecimalFormat("##.####");
                hashMap.put(code, Double.valueOf(decimalFormat.format(diff)));
            }

        }

        HashMap <String, Double> sortedHashMap = hashMap.entrySet()
                .stream()
                .sorted(HashMap.Entry.comparingByValue())
                .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for(HashMap.Entry<String, Double> entry: sortedHashMap.entrySet()){
            System.out.println(entry.getKey().toUpperCase() + ": " + entry.getValue() + " PLN");
        }
    }

    private double getDifferenceBetweenSellAndBuyOnSelectedDate(String currencyCode, String date){

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
        double buyPrice, sellPrice, difference = 0;
        try{
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonObject = jsonElement.getAsJsonObject();
            jsonArray = jsonObject.getAsJsonArray("rates");
            jsonObject = jsonArray.get(0).getAsJsonObject();

            sellPrice = jsonObject.get("bid").getAsDouble();
            buyPrice = jsonObject.get("ask").getAsDouble();
            difference = buyPrice - sellPrice;


        }
        catch (FileNotFoundException e){
            System.out.println("Type correct data!");
        } catch (IOException e) {
            return difference;
        }
        return difference;
    }

    @Override
    public void getCurrentGoldPrice() {
        /*
        This method displays current gold price
         */
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
        /*
         * This method displays gold price for selected date
         * @param date This is date for which we want to get gold price.
         */
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
        /*
         * This method plots currency price for selected date range
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range
         * @param averageValueFlag This is the flag which describes if we want to add to the plot average gold price value for selected date range
         */

        Map<String, Double> data = getGoldPriceMap(startDate + "/" + endDate);

        SwingUtilities.invokeLater(() -> {
            PlotDrawer plot = new PlotDrawer("Currency NBP", "Gold prices", "Date", "Price [PLN]", data, averageValueFlag);
            plot.setSize(1200, 800);
            plot.setLocationRelativeTo(null);
            plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            plot.setVisible(true);
        });

    }

    @Override
    public void getMinGoldPrice(String startDate, String endDate) {
        /*
         * This method displays min gold price between startDate and endDate
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range
         */

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date start;
        Date end;
        Double minValue = 0.0;
        String minDate = "";
        try {
            start = simpleDateFormat.parse(startDate);
            end = simpleDateFormat.parse(endDate);

            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(start);

            HashMap <String, Double> hashMap = new HashMap<>();

            while (!gregorianCalendar.getTime().after(end)){
                Date date = gregorianCalendar.getTime();
                Double priceValue = getGoldPriceforMinMax(simpleDateFormat.format(date).toString());

                if(priceValue != 0.0){
                    hashMap.put(simpleDateFormat.format(date).toString(), priceValue);
                    // System.out.println(priceValue);
                }
                gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            minValue = Collections.min(hashMap.values());

            for (Map.Entry<String, Double> entry : hashMap.entrySet()){
                if(entry.getValue() == minValue){
                    minDate = entry.getKey();
                }
            }

            System.out.println(String.format("Max %s: %.2f PLN", minDate, minValue));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMaxGoldPrice(String startDate, String endDate) {
        /*
         * This method displays max gold price between startDate and endDate
         * @param startDate This is date which is the start of the range
         * @param endDate This is date which is the end of the range
         */


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date start;
        Date end;
        Double maxValue = 0.0;
        String maxDate = "";
        try {
            start = simpleDateFormat.parse(startDate);
            end = simpleDateFormat.parse(endDate);

            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(start);

            HashMap <String, Double> hashMap = new HashMap<>();

            while (!gregorianCalendar.getTime().after(end)){
                Date date = gregorianCalendar.getTime();
                Double priceValue = getGoldPriceforMinMax(simpleDateFormat.format(date).toString());

                if(priceValue != 0.0){
                    hashMap.put(simpleDateFormat.format(date).toString(), priceValue);
                    // System.out.println(priceValue);
                }
                gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            maxValue = Collections.max(hashMap.values());

            for (Map.Entry<String, Double> entry : hashMap.entrySet()){
                if(entry.getValue() == maxValue){
                    maxDate = entry.getKey();
                }
            }

            System.out.println(String.format("Max %s: %.2f PLN", maxDate, maxValue));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private double getGoldPriceforMinMax(String date) {
        StringBuilder url = new StringBuilder();
        url.append(GOLD_PRICE_URL);
        url.append("/");
        url.append(date);
        url.append("/");
        url.append(JSON_FORMAT);

        JsonElement jsonElement = null;
        JsonObject jsonObject = null;
        JsonArray jsonArray = null;
        double price = 0;

        try {
            jsonElement = new JsonParser().parse(Connection.makeQuery(url.toString()));
            jsonArray = jsonElement.getAsJsonArray();
            jsonObject = jsonArray.get(0).getAsJsonObject();

            price = jsonObject.get("cena").getAsDouble();

            return price;

        } catch (IOException e) {
            return  0.0;
        }

    }

    private static Map<String, Double> getAverageCurrencyPriceMap(String currencyCode, String dataRange){
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