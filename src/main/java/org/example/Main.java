package org.example;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        var server = new FCGIInterface();
        String header = "Content-Type: application/json\nContent-Length: %d\n\n%s";
        boolean result;
        String body;
        String response;

        while (server.FCGIaccept() >= 0) {
            long startTime = System.nanoTime() / 1000;
            String request = FCGIInterface.request.params.getProperty("QUERY_STRING");
            String requestMethod = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
            if (requestMethod.equals("POST")) {
                request = request.replace('=', ' ');
                request = request.replace('X', ' ');
                request = request.replace('Y', ' ');
                request = request.replace('R', ' ');
                request = request.strip();
                String[] data = request.split("&");
                if (data.length == 3) {
                    try {
                        double x = Double.parseDouble(data[0]);
                        double y = Double.parseDouble(data[1]);
                        double r = Double.parseDouble(data[2]);
                        result = (x >= -r && y < r / 2 && x <= 0 && y > 0) ||
                                (((Math.pow(x, 2) + Math.pow(y, 2)) <= Math.pow(r, 2)) && x >= 0 && y >= 0) ||
                                (y >= -(x + r) / 2 && y <= 0 && x <= 0 && x >= -r);
                    } catch (NumberFormatException e) {
                        result = false;
                    }
                } else {
                    result = false;
                }
                long finishTime = System.nanoTime() / 1000 - startTime;
                Date currentDate = new Date(System.currentTimeMillis());
                body = "{\"result\": %b, \"time\": %d, \"curr_time\": %s}".formatted(result, finishTime, new SimpleDateFormat("HH.mm.ss").format(currentDate));
            } else if (requestMethod.equals("GET")) {
                body = "{\"x_values\": [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2], \"r_values\": [1, 2, 3, 4, 5]}";
            } else {
                body = "{\"response\": Неизвестный запрос!}";
            }
            response = String.format(header, body.getBytes(StandardCharsets.UTF_8).length, body);
            System.out.println(response);
        }
    }
}