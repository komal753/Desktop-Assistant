import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;

public class Assistant {

    private JTextArea assistantOutput;
    private boolean isDarkMode = false; // Track dark mode status

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Assistant::new);
    }

    public Assistant() {
        // Create the main frame
        JFrame frame = new JFrame("Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // Add a KeyListener to the frame to handle Command+W or Control+W
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.isControlDown() && e.getKeyCode() == KeyEvent.VK_W) || (e.isMetaDown() && e.getKeyCode() == KeyEvent.VK_W)) {
                    System.exit(0);
                }
            }
        });

        // Create the main layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 238, 245));

        // TextArea for assistant responses
        assistantOutput = new JTextArea();
        assistantOutput.setEditable(false);
        assistantOutput.setLineWrap(true);
        assistantOutput.setWrapStyleWord(true);
        assistantOutput.setFont(new Font("Arial", Font.PLAIN, 16));
        assistantOutput.setBackground(new Color(250, 250, 250));
        assistantOutput.setForeground(new Color(80, 80, 80));
        assistantOutput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        assistantOutput.setText("Assistant's Responses will appear here...\n");
        JScrollPane scrollPane = new JScrollPane(assistantOutput);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // TextField for user input
        JTextField userInput = new JTextField();
        userInput.setFont(new Font("Arial", Font.PLAIN, 16));
        userInput.setForeground(new Color(50, 50, 50));
        userInput.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 200), 2));

        // Add KeyListener to handle Enter key
        userInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processUserInput(userInput.getText());
                    userInput.setText("");
                }
            }
        });

        // Button to submit user input
        JButton submitButton = new JButton("Send");
        styleButton(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processUserInput(userInput.getText());
                userInput.setText("");
            }
        });

        // Buttons for quick actions
        JPanel buttonPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        buttonPanel.setBackground(new Color(240, 238, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton timeButton = new JButton("Time");
        JButton dateButton = new JButton("Date");
        JButton browserButton = new JButton("Open Browser");
        JButton fileExplorerButton = new JButton("Open Files");
        JButton notesButton = new JButton("Open Notes");
        JButton cameraButton = new JButton("Open Camera");
        JButton sleepButton = new JButton("Sleep");
        JButton shutdownButton = new JButton("Shutdown");
        JButton darkModeButton = new JButton("Dark Mode");

        styleButton(timeButton);
        styleButton(dateButton);
        styleButton(browserButton);
        styleButton(fileExplorerButton);
        styleButton(notesButton);
        styleButton(cameraButton);
        styleButton(sleepButton);
        styleButton(shutdownButton);
        styleButton(darkModeButton);

        timeButton.addActionListener(e -> processUserInput("time"));
        dateButton.addActionListener(e -> processUserInput("date"));
        browserButton.addActionListener(e -> processUserInput("open browser"));
        fileExplorerButton.addActionListener(e -> openFileExplorer());
        notesButton.addActionListener(e -> openNotes());
        cameraButton.addActionListener(e -> openCamera());
        sleepButton.addActionListener(e -> sleepLaptop());
        shutdownButton.addActionListener(e -> shutdownLaptop());
        darkModeButton.addActionListener(e -> toggleDarkMode(mainPanel, buttonPanel, userInput));

        buttonPanel.add(timeButton);
        buttonPanel.add(dateButton);
        buttonPanel.add(browserButton);
        buttonPanel.add(fileExplorerButton);
        buttonPanel.add(notesButton);
        buttonPanel.add(cameraButton);
        buttonPanel.add(sleepButton);
        buttonPanel.add(shutdownButton);
        buttonPanel.add(darkModeButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Bottom panel for user input and submit button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inputPanel.setBackground(new Color(240, 238, 245));
        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Set up the frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Toggles between light and dark modes.
     */
    private void toggleDarkMode(JPanel mainPanel, JPanel buttonPanel, JTextField userInput) {
        isDarkMode = !isDarkMode;

        Color background = isDarkMode ? new Color(30, 30, 30) : new Color(240, 238, 245);
        Color textColor = isDarkMode ? new Color(220, 220, 220) : new Color(80, 80, 80);
        Color inputBackground = isDarkMode ? new Color(50, 50, 50) : new Color(250, 250, 250);
        Color inputBorder = isDarkMode ? new Color(100, 100, 100) : new Color(180, 180, 200);

        mainPanel.setBackground(background);
        buttonPanel.setBackground(background);

        assistantOutput.setBackground(inputBackground);
        assistantOutput.setForeground(textColor);

        userInput.setBackground(inputBackground);
        userInput.setForeground(textColor);
        userInput.setBorder(BorderFactory.createLineBorder(inputBorder, 2));

        for (Component c : buttonPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(inputBackground);
                c.setForeground(textColor);
            }
        }
    }

    /**
     * Processes the user input and updates the assistant's output.
     * @param input The user's input.
     */
    private void processUserInput(String input) {
        if (input == null || input.isBlank()) {
            assistantOutput.append("\nAssistant: Please enter a valid command.\n\n");
            return;
        }

        assistantOutput.append("\nYou: " + input + "\n\n");

        // Process the command and respond
        String response = getAssistantResponse(input);
        assistantOutput.append("Assistant: " + response + "\n\n");
    }

    /**
     * Generates a response based on user input.
     * @param input The user's input.
     * @return The assistant's response.
     */
    private String getAssistantResponse(String input) {
        input = input.toLowerCase();

        switch (input) {
            case "hello":
                return "Hello! How can I assist you today?";
            case "open browser":
                openWebBrowser();
                return "Opening your default web browser...";
            case "time":
                return "The current time is " + LocalTime.now().toString();
            case "date":
                return "Today's date is " + LocalDate.now().toString();
            default:
                return getGeminiAPIResponse(input);
        }
    }

    /**
     * Styles buttons to match the aesthetic theme.
     * @param button The button to style.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(224, 255, 250));
        button.setForeground(new Color(60, 60, 60));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 200), 2));
    }

    /**
     * Opens the default web browser.
     */
    private void openWebBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.google.com"));
        } catch (Exception e) {
            assistantOutput.append("\nAssistant: Failed to open the web browser.\n\n");
        }
    }

    /**
     * Opens a simple note-taking application.
     */
    private void openNotes() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                new ProcessBuilder("open", "-a", "TextEdit").start();
            } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("notepad").start();
            } else {
                new ProcessBuilder("gedit").start();
            }
            assistantOutput.append("\nAssistant: Opening Notes...\n\n");
        } catch (Exception e) {
            assistantOutput.append("\nAssistant: Failed to open Notes.\n\n");
        }
    }

    /**
     * Opens the system's default camera application.
     */
    private void openCamera() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                new ProcessBuilder("open", "-a", "Photo Booth").start();
            } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd.exe", "/c", "start", "microsoft.windows.camera:").start();
            } else {
                new ProcessBuilder("cheese").start();
            }
            assistantOutput.append("\nAssistant: Opening Camera...\n\n");
        } catch (Exception e) {
            assistantOutput.append("\nAssistant: Failed to open the Camera.\n\n");
        }
    }

    /**
     * Puts the system into sleep mode.
     */
    private void sleepLaptop() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("rundll32.exe", "powrprof.dll,SetSuspendState", "Sleep").start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("pmset", "sleepnow").start();
            } else if (os.contains("nix") || os.contains("nux")) {
                new ProcessBuilder("systemctl", "suspend").start();
            }
            assistantOutput.append("\nAssistant: Putting the system to sleep...\n\n");
        } catch (Exception e) {
            assistantOutput.append("\nAssistant: Failed to put the system to sleep. Please check permissions or try manually.\n\n");
        }
    }

    /**
     * Shut-Down.
     */
    private void shutdownLaptop() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("shutdown", "/s", "/t", "0").start(); // Windows shutdown command
            } else if (os.contains("mac")) {
                new ProcessBuilder("shutdown", "-h", "now").start(); // macOS shutdown command
            } else if (os.contains("nix") || os.contains("nux")) {
                new ProcessBuilder("shutdown", "now").start(); // Linux shutdown command
            }
            assistantOutput.append("\nAssistant: Shutting down the system...\n\n");
        } catch (Exception e) {
            assistantOutput.append("\nAssistant: Failed to shut down the system. Please check permissions or try manually.\n\n");
        }
    }


    /**
     * Opens the system's default file manager.
     */
    private void openFileExplorer() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                new ProcessBuilder("open", ".").start();
            } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("explorer", ".").start();
            } else {
                new ProcessBuilder("xdg-open", ".").start();
            }
            assistantOutput.append("\nAssistant: Opening file manager...\n\n");
        } catch (Exception e) {
            assistantOutput.append("\nAssistant: Failed to open the file manager.\n\n");
        }
    }

    /**
     * Makes an API call to the Gemini API.
     * @param query The user's query.
     * @return The API response.
     */
    private String getGeminiAPIResponse(String query) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAdmAUDtpErmJKC_C8fczpS02f4fc2FTZQ"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(String.format(
                            "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                            query.replace("\"", "\\\""))))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Only show debug information in case of error
            if (response.body().contains("error")) {
                assistantOutput.append("\nDEBUG: Raw API Response: " + response.body() + "\n\n");
                return "It seems there was an issue with the API call. Please try again later.";
            }

            return parseGeminiAPIResponse(response.body());
        } catch (Exception e) {
            return "I'm sorry, I couldn't process your request right now.";
        }
    }

    /**
     * Parses the Gemini API response to extract the reply text.
     * @param response The raw API response.
     * @return The extracted reply.
     */
    private String parseGeminiAPIResponse(String response) {
        try {
            // Find the "candidates" key
            int candidatesIndex = response.indexOf("\"candidates\"");
            if (candidatesIndex == -1) return "No candidates found in the API response.";

            // Locate the start of the candidates array
            int arrayStartIndex = response.indexOf("[", candidatesIndex);
            int arrayEndIndex = response.indexOf("]", arrayStartIndex);
            if (arrayStartIndex == -1 || arrayEndIndex == -1) return "Invalid candidates array in the API response.";

            // Extract the content of the array
            String candidatesArray = response.substring(arrayStartIndex + 1, arrayEndIndex);

            // Find the first "text" key in the candidates array
            int textIndex = candidatesArray.indexOf("\"text\"");
            if (textIndex == -1) return "No text field found in the candidates.";

            // Locate the value of the "text" field
            int textValueStart = candidatesArray.indexOf("\"", textIndex + 7) + 1;
            int textValueEnd = candidatesArray.indexOf("\"", textValueStart);
            if (textValueStart == -1 || textValueEnd == -1) return "Failed to extract the text value.";

            // Extract and return the text value, cleaning up formatting artifacts
            return candidatesArray.substring(textValueStart, textValueEnd)
                    .trim()
                    .replace("\\n", " ") // Remove newline artifacts
                    .replace("\\", "")   // Remove unwanted backslashes
                    .replace("*", "");   // Remove asterisks
        } catch (Exception e) {
            return "An error occurred while parsing the API response: " + e.getMessage();
        }
    }
}

