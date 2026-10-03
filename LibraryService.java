import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryService {

    private final List<Book> books = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    private int nextBookId = 1;
    private int nextMemberId = 1;

    public LibraryService() {

        addBook("Clean Code", "Robert C. Martin");
        addBook("The Pragmatic Programmer", "Andrew Hunt");
        addBook("Effective Java", "Joshua Bloch");

        addMember("Ayesha Khan", "ayesha@example.com");
        addMember("Ali Ahmed", "ali@example.com");
    }

    public Book addBook(String title, String author) {

        validate(title, "Book title");
        validate(author, "Author");

        Book book = new Book(
                nextBookId++,
                title.trim(),
                author.trim()
        );

        books.add(book);

        return book;
    }

    public Member addMember(String name, String email) {

        validate(name, "Member name");
        validate(email, "Email");

        Member member = new Member(
                nextMemberId++,
                name.trim(),
                email.trim()
        );

        members.add(member);

        return member;
    }

    public Loan issueBook(Book book, Member member) {

        if (book == null || member == null) {

            throw new IllegalArgumentException(
                    "Please select a book and member."
            );
        }

        if (!book.isAvailable()) {

            throw new IllegalStateException(
                    "This book is already issued."
            );
        }

        book.setAvailable(false);

        Loan loan = new Loan(book, member);

        loans.add(loan);

        return loan;
    }

    public void returnBook(Loan loan) {

        if (loan == null || !loan.isActive()) {

            throw new IllegalStateException(
                    "This loan has already been returned."
            );
        }

        loan.getBook().setAvailable(true);

        loan.markReturned();
    }

    public List<Book> searchBooks(String query) {

        if (query == null || query.isBlank()) {

            return getBooks();
        }

        String search = query.toLowerCase();

        return books.stream()
                .filter(book ->
                        book.getTitle()
                                .toLowerCase()
                                .contains(search)
                        ||
                        book.getAuthor()
                                .toLowerCase()
                                .contains(search)
                )
                .collect(Collectors.toList());
    }

    public List<Book> getBooks() {

        return Collections.unmodifiableList(books);
    }

    public List<Member> getMembers() {

        return Collections.unmodifiableList(members);
    }

    public List<Loan> getActiveLoans() {

        return loans.stream()
                .filter(Loan::isActive)
                .collect(Collectors.toList());
    }

    private void validate(String value, String field) {

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    field + " cannot be empty."
            );
        }
    }
}