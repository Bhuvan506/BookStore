package bookstore;

import io.grpc.stub.StreamObserver;

import bookstore.BookServiceOuterClass.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {
    private static Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
//    private final Map<String, Book> bookStore = new HashMap<>();
    private final MongoBookStore bookStore = new MongoBookStore();

    @Override
    public void addBook(AddBookRequest request, StreamObserver<AddBookResponse> responseObserver) {
        Book book = request.getBook();
        String isbn = book.getIsbn();
        try {
            if(bookStore.findBook(isbn)) {
                AddBookResponse response = AddBookResponse.newBuilder().setMessage("Book already exists").build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        bookStore.put(isbn, book);
        try {
                bookStore.addBook(book);
            } catch (IOException e) {
                throw new RuntimeException(e);
        }
        AddBookResponse response = AddBookResponse.newBuilder().setMessage("Book added successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        Book book = request.getBook();
        String isbn = book.getIsbn();
        try {
            if(!bookStore.findBook(isbn)) {
                UpdateBookResponse response = UpdateBookResponse.newBuilder().setMessage("Book does not exists").build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bookStore.updateBook(book);
        } catch (IOException e) {
            logger.warn("Error", e);
            throw new RuntimeException(e);
        }
        UpdateBookResponse response = UpdateBookResponse.newBuilder().setMessage("Book updated successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        String isbn = request.getIsbn();
        try {
            if(!bookStore.findBook(isbn)) {
                DeleteBookResponse response = DeleteBookResponse.newBuilder().setMessage("Book not found").build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bookStore.deleteBook(isbn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DeleteBookResponse response = DeleteBookResponse.newBuilder().setMessage("Book deleted successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBooks(GetBooksRequest request, StreamObserver<GetBooksResponse> responseObserver) {
        GetBooksResponse.Builder responseBuilder = GetBooksResponse.newBuilder();
        Map<String, Book> store;
        try {
            store = bookStore.getBooks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        store.values().forEach(responseBuilder::addBooks);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void streamBooks(StreamBooksRequest request, StreamObserver<Book> responseObserver) {
        Map<String, Book> store;
        try {
            store = bookStore.getBooks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(Book book: store.values()) {
            responseObserver.onNext(book);
        }
        responseObserver.onCompleted();
    }
}
