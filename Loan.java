import java.time.LocalDate;

public class Loan {

    private final Book book;
    private final Member member;
    private final LocalDate issueDate;
    private LocalDate returnDate;

    public Loan(Book book, Member member) {

        this.book = book;
        this.member = member;
        this.issueDate = LocalDate.now();
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public void markReturned() {
        returnDate = LocalDate.now();
    }
}