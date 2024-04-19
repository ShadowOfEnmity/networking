package org.example;

import java.io.IOException;

public class ServerRunner
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        HttpServerSocket server = new HttpServerSocket(8082, 10);
        server.run();
    }
}
