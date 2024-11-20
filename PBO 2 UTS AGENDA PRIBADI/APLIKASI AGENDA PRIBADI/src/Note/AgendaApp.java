package Note;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.io.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Image;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

// Kelas utama untuk aplikasi agenda pribadi
public class AgendaApp extends JFrame {
    // Deklarasi komponen UI dan variabel database
    private JTextField titleField, timeField;
    private JTextArea descriptionArea;
    private JTable agendaTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, exportButton, importButton, uploadImageButton; // Tombol-tombol untuk aksi
    private JDateChooser dateChooser;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agenda_db"; // URL koneksi ke database
    private static final String DB_USER = "root"; // Username database
    private static final String DB_PASSWORD = ""; // Password database
    private String image_path = null; // Variabel untuk menyimpan path gambar yang dipilih

    // Konstruktor utama untuk membuat jendela aplikasi
    public AgendaApp() {
        setTitle("Agenda Pribadi");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Mengatur layout utama
        setLayout(new BorderLayout(10, 10));

        // Komponen input
        titleField = new JTextField(20);
        timeField = new JTextField(10);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true); // Mengatur teks agar tetap dalam batas area
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(java.awt.Color.LIGHT_GRAY); // Memberikan warna latar belakang

        // Komponen JDateChooser untuk memilih tanggal
        dateChooser = new JDateChooser();

        // Membuat tombol-tombol aksi
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        exportButton = new JButton("Export PDF");
        importButton = new JButton("Import File");
        uploadImageButton = new JButton("Upload Image"); // Tombol untuk mengunggah gambar

        // Memberikan gaya pada tombol
        styleButton(addButton);
        styleButton(updateButton);
        styleButton(deleteButton);
        styleButton(exportButton);
        styleButton(importButton);
        styleButton(uploadImageButton);

        // Mengatur tabel untuk menampilkan data agenda
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Date", "Time", "Description", "Image"}, 0);
        agendaTable = new JTable(tableModel);
        agendaTable.setFillsViewportHeight(true); // Tabel memenuhi area tampilan
        agendaTable.setDefaultEditor(Object.class, null); // Menonaktifkan pengeditan langsung
        agendaTable.setSelectionBackground(new java.awt.Color(135, 206, 250)); // Warna seleksi
        JScrollPane scrollPane = new JScrollPane(agendaTable);

        // Panel untuk input form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateChooser);
        formPanel.add(new JLabel("Time (HH:MM):"));
        formPanel.add(timeField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(uploadImageButton);

        // Menambahkan panel dan tabel ke frame
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        // Listener untuk tombol-tombol
        addButton.addActionListener(e -> addAgenda());
        updateButton.addActionListener(e -> updateAgenda());
        deleteButton.addActionListener(e -> deleteAgenda());
        exportButton.addActionListener(e -> exportPDF());
        uploadImageButton.addActionListener(e -> chooseImage());

        // Memuat data agenda dari database
        loadAgendaData();
        setVisible(true);
    }

    // Metode untuk memberikan gaya pada tombol
    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180)); // Warna latar belakang
        button.setForeground(Color.WHITE); // Warna teks
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14)); // Gaya dan ukuran font
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Ubah kursor saat hover
    }

    // Metode untuk memilih gambar dari file
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            image_path = selectedFile.getAbsolutePath(); // Simpan path gambar
            JOptionPane.showMessageDialog(this, "Image uploaded successfully!");
        }
    }
    
    // Metode untuk menambahkan agenda baru
    private void addAgenda() {
    String title = titleField.getText();
    java.util.Date date = dateChooser.getDate();
    String time = timeField.getText();
    String description = descriptionArea.getText();

    if (title.isEmpty() || date == null || time.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all required fields: Title, Date, and Time.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = sdf.format(date);

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String query = "INSERT INTO agenda (title, date, time, description, image_path) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, title);
        stmt.setString(2, formattedDate);
        stmt.setString(3, time);
        stmt.setString(4, description);
        stmt.setString(5, image_path != null ? image_path : ""); // Simpan path gambar, pastikan ada default jika null
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Agenda added successfully!");
        loadAgendaData();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding agenda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    // Metode untuk memperbarui agenda yang dipilih
    private void updateAgenda() {
        int selectedRow = agendaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an agenda to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String title = titleField.getText();
        java.util.Date date = dateChooser.getDate();
        String time = timeField.getText();
        String description = descriptionArea.getText();

        if (date == null) {
            JOptionPane.showMessageDialog(this, "Please select a date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE agenda SET title = ?, date = ?, time = ?, description = ?, image_path = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, formattedDate);
            stmt.setString(3, time);
            stmt.setString(4, description);
            stmt.setString(5, image_path); // Mengupdate path gambar di database
            stmt.setInt(6, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Agenda updated successfully!");
            loadAgendaData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating agenda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metode untuk menghapus agenda yang dipilih
    private void deleteAgenda() {
        int selectedRow = agendaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an agenda to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM agenda WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Agenda deleted successfully!");
            loadAgendaData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting agenda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metode untuk memuat data agenda dari database
    private void loadAgendaData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM agenda";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");
                tableModel.addRow(new Object[]{id, title, date, time, description, imagePath});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Metode untuk mengekspor data ke PDF
    private void exportPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
        
        // Ensure the file ends with ".pdf"
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Menambahkan judul
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("Agenda Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Menambahkan tabel data
            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.addCell("ID");
            pdfTable.addCell("Title");
            pdfTable.addCell("Date");
            pdfTable.addCell("Time");
            pdfTable.addCell("Description");
            pdfTable.addCell("Image");

            // Atur lebar kolom untuk menyesuaikan gambar dan teks
            float[] columnWidths = { 1f, 2f, 2f, 2f, 3f, 3f };  // Kolom gambar diberi lebih lebar
            pdfTable.setWidths(columnWidths);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object cellValue = tableModel.getValueAt(i, j);
                    
                    // Cek jika kolom adalah kolom image path (asumsi di index 5)
                    if (j == 5 && cellValue != null && !cellValue.toString().isEmpty()) {
                        try {
                            // Tambahkan gambar ke PDF jika path valid
                            Image img = Image.getInstance(cellValue.toString());
                            img.scaleToFit(100, 100); // Menyesuaikan ukuran gambar agar lebih kecil
                            PdfPCell imageCell = new PdfPCell(img);
                            imageCell.setPadding(5);  // Menambah padding di sekitar gambar
                            pdfTable.addCell(imageCell);
                        } catch (Exception ex) {
                            // Jika ada masalah dengan gambar, tampilkan path-nya saja
                            pdfTable.addCell(cellValue.toString());
                        }
                    } else {
                        // Tambahkan data biasa ke tabel PDF
                        pdfTable.addCell(cellValue != null ? cellValue.toString() : "");
                    }
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "Agenda exported to PDF successfully at " + filePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    // Metode utama untuk menjalankan aplikasi
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgendaApp());
    }
}
