import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Connection {
    public static String makeQuery(String queryUrl) throws IOException {
        String result = "";
        URL url = new URL(queryUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        String input;
        while((input = reader.readLine()) != null){
            result += input;
        }
        return result;
    }
}
