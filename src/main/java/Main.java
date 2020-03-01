import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(String [] args){
        Options options = new Options();

        options.addOption("currentPrice", false, "displays current price of currency or gold");
        options.addOption("priceOnDate", false, "displays price on the date");
        options.addOption("plot", false, "draws plot");


        options.addOption("c", true, "currency code");
        options.addOption("d", true, "date");
        options.addOption("s", true, "start date");
        options.addOption("e", true, "end date");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            DataAnalyser  dataAnalyser = new DataAnalyser();

            if(cmd.hasOption("currentPrice")){
                if(cmd.hasOption("c")){
                    String currencyCode = cmd.getOptionValue("c");
                    dataAnalyser.getCurrentCurrencyPrice(currencyCode);
                }
                else{
                    dataAnalyser.getCurrentGoldPrice();
                }
            }

            else if(cmd.hasOption("priceOnDate")){

                if(cmd.hasOption("c") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String currencyCode = cmd.getOptionValue("c");
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");

                    dataAnalyser.getAverageCurrencyPriceFromSelectedDateRange(currencyCode, startDate, endDate);
                }

                else if(cmd.hasOption("c") && cmd.hasOption("d")){
                    String currencyCode = cmd.getOptionValue("c");
                    String date = cmd.getOptionValue("d");

                    dataAnalyser.getAverageCurrencyPriceFromSelectedDate(currencyCode, date);
                }
                else if(cmd.hasOption("d")){
                    String date = cmd.getOptionValue("d");
                    dataAnalyser.getGoldPriceFromSelectedDate(date);
                }
            }
            else if(cmd.hasOption("plot")){
                if(cmd.hasOption("c") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");
                    String currencyCode = cmd.getOptionValue("c");

                    dataAnalyser.plotCurrencyPriceFromSelectedDateRange(currencyCode, startDate, endDate, true);
                }
                else if(cmd.hasOption("s") && cmd.hasOption("e")){
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");

                    dataAnalyser.plotGoldPriceFromSelectedDateRange(startDate, endDate, true);

                }

            }
        } catch (ParseException e) {
            System.out.println("Wrong arguments!");
        }

    }

}
