public interface DataAnalyserInterface {

    // Methods for analysing currency price

    void getCurrentCurrencyPrice(String currencyCode);

    void getAverageCurrencyPriceFromSelectedDate(String currencyCode, String date);

    void getAverageCurrencyPriceFromSelectedDateRange(String currencyCode, String startDate, String endDate);

    void plotCurrencyPriceFromSelectedDateRange(String currencyCode, String startDate, String endDate, boolean averageValueFlag);

    // Methods for analysing gold price

    void getCurrentGoldPrice();

    void getGoldPriceFromSelectedDate(String date);

    void plotGoldPriceFromSelectedDateRange(String startDate, String endDate, boolean averageValueFlag);

}
