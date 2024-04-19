package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpClientRunner {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        String currentDir = System.getProperty("user.dir");
        String targetClassDir = currentDir + "/target/classes";

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        Path jsonPath = Paths.get("/home/stas/IdeaProjects/javaguruhomework/networking/networking/src/main/resources/salaries.json");
        byte[] jsonSalaries = Files.readAllBytes(jsonPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8082/salaries"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(jsonSalaries))
                .build();


        client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(Paths.get(targetClassDir + "/salaries.html")))
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println).exceptionally(e -> {
                    System.err.println("Request execution error: " + e.getMessage());
                    return null;
                }).join();

        Thread.sleep(6000);

    }

}
