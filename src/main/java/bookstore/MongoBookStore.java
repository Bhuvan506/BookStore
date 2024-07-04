package bookstore;

import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.*;
import org.hypertrace.core.documentstore.*;
import org.hypertrace.core.documentstore.expression.impl.ConstantExpression;
import org.hypertrace.core.documentstore.expression.impl.IdentifierExpression;
import org.hypertrace.core.documentstore.expression.impl.RelationalExpression;
import org.hypertrace.core.documentstore.expression.operators.RelationalOperator;
import org.hypertrace.core.documentstore.model.config.ConnectionConfig;
import org.hypertrace.core.documentstore.model.config.DatabaseType;
import org.hypertrace.core.documentstore.model.config.DatastoreConfig;
import org.hypertrace.core.documentstore.model.config.Endpoint;
import bookstore.BookServiceOuterClass.*;
import org.hypertrace.core.documentstore.query.Filter;
import org.hypertrace.core.documentstore.query.Query;
import com.fasterxml.jackson.databind.JsonNode.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MongoBookStore {

    private static final Printer printer = JsonFormat.printer().preservingProtoFieldNames();
    private static final Parser parser = JsonFormat.parser().ignoringUnknownFields();
    private final Collection collection;

    public MongoBookStore() {
        Endpoint endpoint = Endpoint.builder()
                .host("localhost")
                .port(28018)
                .build();
        ConnectionConfig config = ConnectionConfig.builder()
                .addEndpoint(endpoint)
                .type(DatabaseType.MONGO)
                .build();
        Datastore db = DatastoreProvider.getDatastore(DatastoreConfig.builder()
                .type(DatabaseType.MONGO)
                .connectionConfig(config)
                .build());
        collection = db.getCollection("books");
    }

    public void addBook(final Book book) throws IOException {
        collection.create(Key.from(book.getIsbn()), new JSONDocument(printer.print(book)));
    }

    public void updateBook(final Book book) throws IOException {
        collection.update(Key.from(book.getIsbn()), new JSONDocument(printer.print(book)), new org.hypertrace.core.documentstore.Filter());
    }

    public void deleteBook(final String isbn) throws IOException {
        collection.delete(Key.from(isbn));
    }

    public Map<String, Book> getBooks() throws IOException {
        final Map<String, Book> books = new HashMap<>();
        Query query = Query.builder().build();
        CloseableIterator<Document> docs = collection.aggregate(query);
        while(docs.hasNext()) {
            final Document document = docs.next();
            final Book.Builder builder = Book.newBuilder();
            parser.merge(document.toJson(), builder);
            books.put(builder.build().getIsbn(), builder.build());
        }
        return books;
    }

    public boolean findBook(final String isbn) throws IOException {
        Query query = Query.builder().setFilter(Filter.builder().expression(RelationalExpression.builder()
                .lhs(IdentifierExpression.of("isbn"))
                .operator(RelationalOperator.EQ)
                .rhs(ConstantExpression.of(isbn))
                .build()).build()).build();
        CloseableIterator<Document> docs = collection.aggregate(query);
        return docs.hasNext();
    }

}
