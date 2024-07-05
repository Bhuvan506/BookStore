package bookstore;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bookstore.BookServiceOuterClass.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class BookServiceImplTest {

    private ManagedChannel channel;
    private BookServiceGrpc.BookServiceBlockingStub blockingStub;

    @Before
    public void setup() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    @After
    public void teardown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testAddBook() {
        Book book = Book.newBuilder()
                .setIsbn("1234567890")
                .setTitle("Test Book")
                .addAuthors("Author A")
                .setPageCount(100)
                .build();
        AddBookRequest request = AddBookRequest.newBuilder().setBook(book).build();
        AddBookResponse response = blockingStub.addBook(request);
        assertEquals("Book added successfully", response.getMessage());
    }

    @Test
    public void testUpdateBook() {
        Book book = Book.newBuilder()
                .setIsbn("1234567890")
                .setTitle("Updated Test Book")
                .addAuthors("Author A")
                .setPageCount(150)
                .build();
        UpdateBookRequest request = UpdateBookRequest.newBuilder().setBook(book).build();
        UpdateBookResponse response = blockingStub.updateBook(request);
        assertEquals("Book updated successfully", response.getMessage());
    }

    @Test
    public void testDeleteBook() {
        DeleteBookRequest request = DeleteBookRequest.newBuilder().setIsbn("1234567890").build();
        DeleteBookResponse response = blockingStub.deleteBook(request);
        assertEquals("Book deleted successfully", response.getMessage());
    }

    @Test
    public void testGetBooks() {
        GetBooksRequest request = GetBooksRequest.newBuilder().build();
        GetBooksResponse response = blockingStub.getBooks(request);
        assertTrue(response.getBooksCount() >= 0);
    }
}
