package org.example;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String header = "Content-Type: application/json\nContent-Length: %d\n\n%s";
        var server = new FCGIInterface();
        while (server.FCGIaccept()>= 0) {
            long time = System.currentTimeMillis();
            String request = FCGIInterface.request.params.getProperty("QUERY_STRING");
            request = request.replace('=', ' ');
            request = request.replace('X', ' ');
            request = request.replace('Y', ' ');
            request = request.replace('R', ' ');
            request = request.strip();
            String[] data = request.split("&");
            double x = Double.parseDouble(data[0]);
            double y = Double.parseDouble(data[1]);
            double r = Double.parseDouble(data[2]);
            boolean result = (x >= -r && y < r/2 && x <= 0 && y > 0) ||
            ((Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r, 2)) && x >= 0 && y >= 0) ||
            (y >= -(x + r)/2 && y <= 0 && x <= 0 && x >= -r);
            time -= System.currentTimeMillis();
            String body = "{\"result\": %b, \"time\": %d}".formatted(result, time);
            String response = String.format(header, body.getBytes(StandardCharsets.UTF_8).length, body);
            System.out.println(response);
        }
    }
}