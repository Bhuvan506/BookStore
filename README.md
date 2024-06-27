# Online Book Store Service

This project is an online book store service built using Java, gRPC, Protobuf, and Gradle. It supports basic CRUD operations on books.

## Instructions to Build and Run the Service

### Prerequisites
- Java 11 or higher
- Gradle 6.8 or higher

### Build the Project
1. Clone the repository:
   ```sh
   git clone <repository_url>
   cd <repository_directory>
2. Build the project:
   ```sh
   ./gradlew clean build
3. Run the Server
   ```sh
   ./gradlew run
4. Run the Client
   ```sh
   ./gradlew runClient
5. Run the Tests
   ```sh
   ./gradlew test
## Description of the Implemented RPCs
### AddBook
   - Request: AddBookRequest containing a Book
   - Response: AddBookResponse with a success message
   - Behavior: Adds a book to the in-memory store
### UpdateBook
   - Request: UpdateBookRequest containing a Book
   - Response: UpdateBookResponse with a success message
   - Behavior: Updates the details of a book in the in-memory store
### DeleteBook
   - Request: DeleteBookRequest with the isbn of the book to delete
   - Response: DeleteBookResponse with a success message
   - Behavior: Deletes a book from the in-memory store
### GetBooks
   - Request: GetBooksRequest
   - Response: GetBooksResponse with a list of all books
   - Behavior: Retrieves all books from the in-memory store
