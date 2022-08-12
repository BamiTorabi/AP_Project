package client;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DataLoader {

    private final static String path = "src/main/java/client/properties/";

    public static int getConstraint(String fileName, String field) {
        Properties property = new Properties();
        try {
            property.load(new FileReader(path + fileName + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Integer.parseInt(property.getProperty(field));
    }

}
