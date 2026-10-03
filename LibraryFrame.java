import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibraryFrame extends JFrame {

    private final LibraryService service = new LibraryService();

    // Colors
    private final Color PRIMARY = new Color(52, 152, 219);
    private final Color DARK = new Color(44, 62, 80);
    private final Color GREEN = new Color(39, 174, 96);
    private final Color ORANGE = new Color(230, 126, 34);
    private final Color LIGHT = new Color(236, 240, 241);

    private final DefaultTableModel booksModel =
            createModel(new String[]{"ID", "Title", "Author", "Status"});

    private final DefaultTableModel membersModel =
            createModel(new String[]{"ID", "Name", "Email"});

    private final DefaultTableModel loansModel =
            createModel(new String[]{"Book", "Member", "Issue Date"});

    private final JTable booksTable = new JTable(booksModel);
    private final JTable membersTable = new JTable(membersModel);
    private final JTable loansTable = new JTable(loansModel);

    private final JComboBox<Book> bookCombo = new JComboBox<>();
    private final JComboBox<Member> memberCombo = new JComboBox<>();

    public LibraryFrame() {

        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.setBackground(LIGHT);

        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createTabs(), BorderLayout.CENTER);
        root.add(createIssuePanel(), BorderLayout.SOUTH);

        refreshAll();
    }

    // ================= HEADER =================

    private JPanel createHeader() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Library Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Java OOP Desktop Application");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 220, 220));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(DARK);

        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);

        panel.add(text, BorderLayout.WEST);

        return panel;
    }

    // ================= TABS =================

    private JTabbedPane createTabs() {

        JTabbedPane tabs = new JTabbedPane();

        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Active Loans", createLoansPanel());

        return tabs;
    }

    // ================= BOOKS =================

    private JPanel createBooksPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(Color.WHITE);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(
                BorderFactory.createTitledBorder("Search books")
        );

        JButton addButton = createButton(
                "Add Book",
                PRIMARY
        );

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        refreshBooks(searchField.getText());
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        refreshBooks(searchField.getText());
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        refreshBooks(searchField.getText());
                    }
                }
        );

        addButton.addActionListener(e -> addBookDialog());

        top.add(searchField, BorderLayout.CENTER);
        top.add(addButton, BorderLayout.EAST);

        configureTable(booksTable);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(booksTable), BorderLayout.CENTER);

        return panel;
    }

    // ================= MEMBERS =================

    private JPanel createMembersPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);

        JButton addButton = createButton(
                "Add Member",
                PRIMARY
        );

        addButton.addActionListener(e -> addMemberDialog());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        top.add(addButton, BorderLayout.EAST);

        configureTable(membersTable);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(membersTable), BorderLayout.CENTER);

        return panel;
    }

    // ================= LOANS =================

    private JPanel createLoansPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);

        JButton returnButton = createButton(
                "Return Selected Book",
                ORANGE
        );

        returnButton.addActionListener(
                e -> returnSelectedLoan()
        );

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        top.add(returnButton, BorderLayout.EAST);

        configureTable(loansTable);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(loansTable), BorderLayout.CENTER);

        return panel;
    }

    // ================= ISSUE BOOK =================

    private JPanel createIssuePanel() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        panel.setBackground(new Color(225, 245, 234));

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(GREEN),
                        "Issue a Book"
                )
        );

        bookCombo.setPreferredSize(new Dimension(280, 30));
        memberCombo.setPreferredSize(new Dimension(220, 30));

        JButton issueButton = createButton(
                "Issue Book",
                GREEN
        );

        issueButton.addActionListener(
                e -> issueSelectedBook()
        );

        panel.add(new JLabel("Book:"));
        panel.add(bookCombo);

        panel.add(new JLabel("Member:"));
        panel.add(memberCombo);

        panel.add(issueButton);

        return panel;
    }

    // ================= ADD BOOK =================

    private void addBookDialog() {

        JTextField title = new JTextField();
        JTextField author = new JTextField();

        JPanel panel = new JPanel(
                new GridLayout(0, 1, 5, 5)
        );

        panel.add(new JLabel("Book Title:"));
        panel.add(title);

        panel.add(new JLabel("Author:"));
        panel.add(author);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            try {

                service.addBook(
                        title.getText(),
                        author.getText()
                );

                refreshAll();

                showInfo("Book added successfully.");

            } catch (IllegalArgumentException ex) {

                showError(ex.getMessage());
            }
        }
    }

    // ================= ADD MEMBER =================

    private void addMemberDialog() {

        JTextField name = new JTextField();
        JTextField email = new JTextField();

        JPanel panel = new JPanel(
                new GridLayout(0, 1, 5, 5)
        );

        panel.add(new JLabel("Member Name:"));
        panel.add(name);

        panel.add(new JLabel("Email:"));
        panel.add(email);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Member",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            try {

                service.addMember(
                        name.getText(),
                        email.getText()
                );

                refreshAll();

                showInfo("Member added successfully.");

            } catch (IllegalArgumentException ex) {

                showError(ex.getMessage());
            }
        }
    }

    // ================= ISSUE BOOK =================

    private void issueSelectedBook() {

        try {

            Book book =
                    (Book) bookCombo.getSelectedItem();

            Member member =
                    (Member) memberCombo.getSelectedItem();

            service.issueBook(book, member);

            refreshAll();

            showInfo("Book issued successfully.");

        } catch (RuntimeException ex) {

            showError(ex.getMessage());
        }
    }

    // ================= RETURN BOOK =================

    private void returnSelectedLoan() {

        int row = loansTable.getSelectedRow();

        if (row < 0) {

            showError(
                    "Please select an active loan first."
            );

            return;
        }

        List<Loan> loans =
                service.getActiveLoans();

        if (row >= loans.size()) {
            return;
        }

        try {

            service.returnBook(loans.get(row));

            refreshAll();

            showInfo(
                    "Book returned successfully."
            );

        } catch (RuntimeException ex) {

            showError(ex.getMessage());
        }
    }

    // ================= REFRESH =================

    private void refreshAll() {

        refreshBooks("");
        refreshMembers();
        refreshLoans();
        refreshCombos();
    }

    private void refreshBooks(String query) {

        booksModel.setRowCount(0);

        for (Book book : service.searchBooks(query)) {

            booksModel.addRow(
                    new Object[]{
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.isAvailable()
                                    ? "Available"
                                    : "Issued"
                    }
            );
        }
    }

    private void refreshMembers() {

        membersModel.setRowCount(0);

        for (Member member : service.getMembers()) {

            membersModel.addRow(
                    new Object[]{
                            member.getId(),
                            member.getName(),
                            member.getEmail()
                    }
            );
        }
    }

    private void refreshLoans() {

        loansModel.setRowCount(0);

        for (Loan loan : service.getActiveLoans()) {

            loansModel.addRow(
                    new Object[]{
                            loan.getBook().getTitle(),
                            loan.getMember().getName(),
                            loan.getIssueDate()
                    }
            );
        }
    }

    private void refreshCombos() {

        bookCombo.removeAllItems();

        for (Book book : service.getBooks()) {

            if (book.isAvailable()) {
                bookCombo.addItem(book);
            }
        }

        memberCombo.removeAllItems();

        for (Member member : service.getMembers()) {
            memberCombo.addItem(member);
        }
    }

    // ================= TABLE DESIGN =================

    private void configureTable(JTable table) {

        table.setRowHeight(30);

        table.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(Color.WHITE);

        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        table.setSelectionBackground(
                new Color(174, 214, 241)
        );

        table.setSelectionForeground(Color.BLACK);
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(
            String text,
            Color color
    ) {

        JButton button = new JButton(text);

        button.setBackground(color);
        button.setForeground(Color.WHITE);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setPreferredSize(
                new Dimension(160, 35)
        );

        return button;
    }

    // ================= TABLE MODEL =================

    private static DefaultTableModel createModel(
            String[] columns
    ) {

        return new DefaultTableModel(
                columns,
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {

                return false;
            }
        };
    }

    // ================= MESSAGES =================

    private void showInfo(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Library System",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}