package intro_maven_demo;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Twilliosmssender extends JFrame {

    // private static final String TWILIO_ACCOUNT_SID = "";
    // private static final String TWILIO_AUTH_TOKEN = "";

    private JTextArea logTextArea;
    private File selectedFile;
    private final JTextField rowTextField;
    private final JTextField columnTextField;
    private final JTextField dateTextField;
    private final JTextField timeTextField;
    private final JTextArea smsTextArea;

    private int phoneNumberRowIndex = 0; // Default row index for phone numbers (0-based)
    private int phoneNumberColumnIndex = 0; // Default column index for phone numbers (0-based)

    public Twilliosmssender() {
        super("Twilio SMS Sender");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel rowLabel = new JLabel("Row Index:");
        rowTextField = new JTextField("0", 5);

        JLabel columnLabel = new JLabel("Column Index:");
        columnTextField = new JTextField("0", 5);

        JLabel dateLabel = new JLabel("Date:");
        dateTextField = new JTextField("06/07/2023", 15);

        JLabel timeLabel = new JLabel("Time:");
        timeTextField = new JTextField("2 pm", 15);

        JLabel smsLabel = new JLabel("SMS Message:");
        smsTextArea = new JTextArea(5, 20);
        JScrollPane smsScrollPane = new JScrollPane(smsTextArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(rowTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(columnLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(columnTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(dateTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(timeTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        inputPanel.add(smsLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        inputPanel.add(smsScrollPane, gbc);

        logTextArea = new JTextArea(10, 40);
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);

        JButton openButton = new JButton("Open Excel File");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            }
        });

        JButton sendButton = new JButton("Send SMS");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    phoneNumberRowIndex = Integer.parseInt(rowTextField.getText());
                    phoneNumberColumnIndex = Integer.parseInt(columnTextField.getText());

                    String date = dateTextField.getText();
                    String time = timeTextField.getText();
                    String smsMessage = smsTextArea.getText();

                    Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
                    logTextArea.setText("");

                    List<String> phoneNumbers = readPhoneNumbersFromExcel(selectedFile, phoneNumberRowIndex, phoneNumberColumnIndex);
                    for (String phoneNumber : phoneNumbers) {
                        String message = "Date: " + date + "\nTime: " + time + "\n" + smsMessage;
                        sendSms(phoneNumber, message);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an Excel file first.");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(openButton);
        buttonPanel.add(sendButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(logScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
    }
    private void sendSms(String phoneNumber, String message) {
        Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber("+15419452506"), message).create();
        logTextArea.append("SMS sent to: " + phoneNumber + "\n");
    }

    private List<String> readPhoneNumbersFromExcel(File file, int rowIndex, int columnIndex) {
        List<String> phoneNumbers = new ArrayList<>();
        try {
    FileInputStream fileInputStream = new FileInputStream(file);
    Workbook workbook = WorkbookFactory.create(fileInputStream);
    Sheet sheet = workbook.getSheetAt(0);

    for (Row row : sheet) {
        Cell cell = row.getCell(columnIndex);
        if (row.getRowNum() >= rowIndex && cell != null && cell.getCellType() == CellType.STRING) {
            String phoneNumber = cell.getStringCellValue().trim();
            phoneNumbers.add(phoneNumber);
        }
    }

    workbook.close();
    fileInputStream.close();
} catch (IOException e) {
    e.printStackTrace();
}

return phoneNumbers;

    }   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Twilliosmssender app = new Twilliosmssender();
                app.setVisible(true);
            }
        });
    }
}
