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
        options.addOption("min", false, "displays date when currency or gold have min price");
        options.addOption("max", false, "displays date when currency or gold have max price");
        options.addOption("sortCurrencies", false, "displays n currencies sorted towards difference between sell and buy price");
        options.addOption("h", false, "help for application");


        options.addOption("c", true, "currency code");
        options.addOption("nc", true, "n currencies codes");
        options.addOption("gold", false, "gold tag");
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
                else if(cmd.hasOption("gold")){
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
                else if(cmd.hasOption("d") && cmd.hasOption("gold")){
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
                else if(cmd.hasOption("gold") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");

                    dataAnalyser.plotGoldPriceFromSelectedDateRange(startDate, endDate, true);
                }

            }
            else if(cmd.hasOption("min")){
                if(cmd.hasOption("c") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String currencyCode = cmd.getOptionValue("c");
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");
                    dataAnalyser.getMinCurrencyPrice(currencyCode, startDate, endDate);
                }
                else if(cmd.hasOption("gold") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");
                    dataAnalyser.getMinGoldPrice(startDate, endDate);
                }
            }
            else if(cmd.hasOption("max")){
                if(cmd.hasOption("c") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String currencyCode = cmd.getOptionValue("c");
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");
                    dataAnalyser.getMaxCurrencyPrice(currencyCode, startDate, endDate);
                }
                else if(cmd.hasOption("gold") && cmd.hasOption("s") && cmd.hasOption("e")){
                    String startDate = cmd.getOptionValue("s");
                    String endDate = cmd.getOptionValue("e");
                    dataAnalyser.getMaxGoldPrice(startDate, endDate);
                }
            }

            else if(cmd.hasOption("sortCurrencies") && cmd.hasOption("nc") && cmd.hasOption("d")){
                String nc = cmd.getOptionValue("nc");
                String date = cmd.getOptionValue("d");
                dataAnalyser.getSortedCurrencies(nc, date);
            }

            else if(cmd.hasOption("h")){

                System.out.println("Arguments: ");
                System.out.println("-----------------------------------------------------------------");

                System.out.println("-currentPrice       displays current price of currency or gold");
                System.out.println("-priceOnDate        displays price on selected date");
                System.out.println("-plot               draws plot");
                System.out.println("-min                displays date when currency or gold have min price");
                System.out.println("-max                displays date when currency or gold have max price");
                System.out.println("-sortCurrencies     displays n currencies sorted towards difference between sell and buy price");

                System.out.println("");

                System.out.println("Additional arguments: ");
                System.out.println("-----------------------------------------------------------------");
                System.out.println("-c                  currency code");
                System.out.println("-nc                 currencies codes");
                System.out.println("-gold               gold tag");
                System.out.println("-s                  start date");
                System.out.println("-e                  end date");

                System.out.println("");

                System.out.println("Examples: ");
                System.out.println("-----------------------------------------------------------------");
                System.out.println("-currentPrice -c EUR");
                System.out.println("-currentPrice -c EUR -d 2020-02-15");
                System.out.println("-priceOnDate -c EUR -s 2020-02-15 -e 2020-02-28");
                System.out.println("-currentPrice - gold");
                System.out.println("-currentPrice - gold -d 2020-02-15");
                System.out.println("-plot -c EUR -s 2020-02-15 -e 2020-02-28");
                System.out.println("-min -c EUR -s 2020-02-15 -e 2020-02-28");
                System.out.println("-max -c EUR -s 2020-02-15 -e 2020-02-28");
                System.out.println("-min -gold -s 2020-02-15 -e 2020-02-28");
                System.out.println("-max -gold -s 2020-02-15 -e 2020-02-28");
                System.out.println("-sortCurrencies -nc EUR,USD,CHF,GBP,HUF -d 2020-01-20");

            }
        } catch (ParseException e) {
            System.out.println("Wrong arguments!");
        }
    }

}