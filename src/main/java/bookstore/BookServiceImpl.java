package bookstore;

import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

import bookstore.BookServiceOuterClass.*;

public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final Map<String, Book> bookStore = new HashMap<>();

    @Override
    public void addBook(AddBookRequest request, StreamObserver<AddBookResponse> responseObserver) {
        Book book = request.getBook();
        String isbn = book.getIsbn();
        if(bookStore.containsKey(isbn)) {
            AddBookResponse response = AddBookResponse.newBuilder().setMessage("Book already exists").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        bookStore.put(isbn, book);
        AddBookResponse response = AddBookResponse.newBuilder().setMessage("Book added successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        Book book = request.getBook();
        String isbn = book.getIsbn();
        if(!bookStore.containsKey(isbn)) {
            UpdateBookResponse response = UpdateBookResponse.newBuilder().setMessage("Book does not exists").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        bookStore.put(isbn, book);
        UpdateBookResponse response = UpdateBookResponse.newBuilder().setMessage("Book updated successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        String isbn = request.getIsbn();
        if(!bookStore.containsKey(isbn)) {
            DeleteBookResponse response = DeleteBookResponse.newBuilder().setMessage("Book not found").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        bookStore.remove(isbn);
        DeleteBookResponse response = DeleteBookResponse.newBuilder().setMessage("Book deleted successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBooks(GetBooksRequest request, StreamObserver<GetBooksResponse> responseObserver) {
        GetBooksResponse.Builder responseBuilder = GetBooksResponse.newBuilder();
        bookStore.values().forEach(responseBuilder::addBooks);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void streamBooks(StreamBooksRequest request, StreamObserver<Book> responseObserver) {
        for(Book book: bookStore.values()) {
            responseObserver.onNext(book);
        }
        responseObserver.onCompleted();
    }
}
