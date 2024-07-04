package bookstore;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import bookstore.BookServiceOuterClass.*;

import java.util.Iterator;

public class BookClient {

        public static void main(String[] args) {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                                .usePlaintext()
                                .build();

                BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

                // Add a book
                Book book = Book.newBuilder()
                                .setIsbn("1234567890")
                                .setTitle("Test Book")
                                .addAuthors("Author A")
                                .setPageCount(100)
                                .build();
                AddBookRequest addRequest = AddBookRequest.newBuilder().setBook(book).build();
                AddBookResponse addResponse = stub.addBook(addRequest);
                System.out.println(addResponse.getMessage());

                // Get all books
                GetBooksRequest getRequest = GetBooksRequest.newBuilder().build();
                GetBooksResponse getResponse = stub.getBooks(getRequest);
                getResponse.getBooksList().forEach(b -> System.out.println(b.getTitle()));

                // Update the book
                Book updatedBook = Book.newBuilder()
                                .setIsbn("1234567890")
                                .setTitle("Updated Test Book")
                                .addAuthors("Author A")
                                .setPageCount(150)
                                .build();
                UpdateBookRequest updateRequest = UpdateBookRequest.newBuilder().setBook(updatedBook).build();
                UpdateBookResponse updateResponse = stub.updateBook(updateRequest);
                System.out.println(updateResponse.getMessage());

                // Delete the book
                DeleteBookRequest deleteRequest = DeleteBookRequest.newBuilder().setIsbn("1234567890").build();
                DeleteBookResponse deleteResponse = stub.deleteBook(deleteRequest);
                System.out.println(deleteResponse.getMessage());

                // Get all books again to ensure the book was deleted
                GetBooksResponse finalGetResponse = stub.getBooks(GetBooksRequest.newBuilder().build());
                System.out.println(finalGetResponse.getBooksList()); // .forEach(b -> System.out.println(b.getTitle()));

                // Get all books through stream
                Iterator<Book> books;
                books = stub.streamBooks(StreamBooksRequest.newBuilder().build());
                for(int i=0; books.hasNext(); i++) {
                        book = books.next();
                        System.out.println(book);
                }

                // Shutdown channel
                channel.shutdown();
        }
}
