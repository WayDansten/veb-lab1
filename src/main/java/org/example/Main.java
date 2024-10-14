package org.example;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        boolean result;
        String body;
        String response;
        Logger logger = Logger.getLogger(Main.class.getName());
        logger.setLevel(Level.ALL);
        logger.info("Logging has started...");
        var server = new FCGIInterface();
        String header = "Content-Type: application/json\nContent-Length: %d\n\n%s";
        while (server.FCGIaccept() >= 0) {
            long startTime = System.nanoTime() / 1000;
            String request = FCGIInterface.request.params.getProperty("QUERY_STRING");
            logger.info(request);
            if (Pattern.matches("^type=[01](&.*)?$", request) && !request.equals("type=1")) {
                request = request.replace('=', ' ');
                request = request.replace('X', ' ');
                request = request.replace('Y', ' ');
                request = request.replace('R', ' ');
                request = request.replace("type", " ");
                request = request.strip();
                String[] data = request.split("&");
                if (data[0].equals("0")) {
                    body = "{\"x_values\": [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2], \"r_values\": [1, 2, 3, 4, 5]}";
                } else if (data[0].equals("1")) {
                    double x;
                    double y;
                    double r;
                    long finishTime;
                    Date currentDate;
                    try {
                        x = Double.parseDouble(data[1]);
                        y = Double.parseDouble(data[2]);
                        r = Double.parseDouble(data[3]);
                        result = (x >= -r && y < r / 2 && x <= 0 && y > 0) ||
                                (((Math.pow(x, 2) + Math.pow(y, 2)) <= Math.pow(r, 2)) && x >= 0 && y >= 0) ||
                                (y >= -(x + r) / 2 && y <= 0 && x <= 0 && x >= -r);
                    } catch (NumberFormatException e) {
                        result = false;
                    } finally {
                        finishTime = System.nanoTime() / 1000 - startTime;
                        currentDate = new Date(System.currentTimeMillis());
                    }
                    body = "{\"result\": %b, \"time\": %d, \"curr_time\": \"%s\"}".formatted(result, finishTime, new SimpleDateFormat("HH:mm:ss").format(currentDate));
                } else {
                    body = "{\"response\": Неизвестный запрос!}";
                }
            } else {
                body = "{\"response\": Неизвестный запрос!}";
            }
            response = String.format(header, body.getBytes(StandardCharsets.UTF_8).length, body);
            System.out.println(response);
        }
    }
}