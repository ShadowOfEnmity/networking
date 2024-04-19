package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.example.entity.Employee;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerSocket {
    private final int port;
    private final ExecutorService executorService;


    public HttpServerSocket(int port, int poolSize) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public void run() throws IOException, InterruptedException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/salaries", new ProcessHandler());
        server.setExecutor(executorService);
        server.start();

        System.out.println("HTTP server is started on 8082 port");
        System.out.println("Please press Enter to stop the server...");
        System.in.read();

        server.stop(0);
        System.out.println("HTTP server is stopped");
        Thread.sleep(1000);

    }

}

class ProcessHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        Headers requestHeaders = exchange.getRequestHeaders();
        ClassLoader classLoader = ProcessHandler.class.getClassLoader();
        URL resource = classLoader.getResource("templates/template.html");
        if (requestHeaders.containsKey("Content-Type")
                && requestHeaders.containsKey("Content-Length")) {
            String contentLength = requestHeaders.get("Content-Length").stream().findFirst().orElseGet(
                    () -> "0"
            );
            int integer = Integer.parseInt(contentLength);
            try (InputStream input = exchange.getRequestBody();
                 OutputStream output = exchange.getResponseBody();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8), integer);
                 StringWriter writer = new StringWriter()) {

                Thread.sleep(1000);

                VelocityEngine velocityEngine = new VelocityEngine();
                velocityEngine.setProperty("resource.loaders", "file");
                velocityEngine.setProperty("resource.loader.file.path", Objects.requireNonNull(getClass().getClassLoader().getResource("templates/")).getPath());
                velocityEngine.init();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                Employee[] employees = gson.fromJson(jsonObject.get("employees"), Employee[].class);

                Map<String, String> salariesInfo = calculateSalary(employees);

                Template template = velocityEngine.getTemplate("template.vm");
                VelocityContext context = new VelocityContext();

                salariesInfo.entrySet().forEach(entry -> context.put(entry.getKey(), entry.getValue()));

                template.merge(context, writer);

                byte[] bytes = writer.toString().getBytes(StandardCharsets.UTF_8);

                Headers responseHeaders = exchange.getResponseHeaders();

                exchange.sendResponseHeaders(200, 0);
                responseHeaders.set("Content-Type", "text/html");
                responseHeaders.set("Content-Disposition", "attachment; filename=totalsalary.html");
                responseHeaders.set("Content-Encoding", "binary");
                responseHeaders.set("Charset", "utf-8");
                responseHeaders.set("Content-Length", String.valueOf(bytes.length));
                output.write(System.lineSeparator().getBytes());
                output.write(bytes);

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, String> calculateSalary(Employee[] employees) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        Map<String, String> matcher = new HashMap<>();
        BigDecimal total_income = Arrays.stream(employees).map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add);
        BigDecimal total_tax = Arrays.stream(employees).map(Employee::getTax).reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add);
        BigDecimal total_profit = total_income.subtract(total_tax);
        matcher.put("total_income", df.format(total_income));
        matcher.put("total_tax", df.format(total_tax));
        matcher.put("total_profit", df.format(total_profit));

        return matcher;
    }
}