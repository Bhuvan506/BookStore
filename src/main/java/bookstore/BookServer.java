package bookstore;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BookServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService(new BookServiceImpl())
                .build();
        server.start();
        System.out.println("Server started at port 9090");
        server.awaitTermination();
    }
}
