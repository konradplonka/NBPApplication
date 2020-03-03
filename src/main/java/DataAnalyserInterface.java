public interface DataAnalyserInterface {

    // Methods for analysing currency price

    void getCurrentCurrencyPrice(String currencyCode);

    void getAverageCurrencyPriceFromSelectedDate(String currencyCode, String date);

    void getAverageCurrencyPriceFromSelectedDateRange(String currencyCode, String startDate, String endDate);

    void plotCurrencyPriceFromSelectedDateRange(String currencyCode, String startDate, String endDate, boolean averageValueFlag);

    void getMinCurrencyPrice(String currencyCode, String startDate, String endDate);

    void getMaxCurrencyPrice(String currencyCode, String startDate, String endDate);

    void getSortedCurrencies(String currenciesCodes, String date);

    // Methods for analysing gold price

    void getCurrentGoldPrice();

    void getGoldPriceFromSelectedDate(String date);

    void plotGoldPriceFromSelectedDateRange(String startDate, String endDate, boolean averageValueFlag);

    void getMinGoldPrice(String startDate, String endDate);

    void getMaxGoldPrice(String startDate, String endDate);



}