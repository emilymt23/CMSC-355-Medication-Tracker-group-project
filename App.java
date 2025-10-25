import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Enum storing unit
enum Unit{ 
    UG,
    MG,
    G,
    ML;
    
    // Here we convert strings to unit types
    static Unit toUnit(String input) {
        if (input.equalsIgnoreCase("µg")) {
            return UG;
        }
        if (input.equalsIgnoreCase("mg")) {
            return MG;
        }
        if (input.equalsIgnoreCase("g")) {
            return G;
        }
        if (input.equalsIgnoreCase("ml")) {
            return ML;
        }
        System.out.println("Unit enum to unit problem!"); // For testing purposes
        return null;
    }
    // Here we convert units to string
    static String toText(Unit input) {
        if (input == UG) {
            return "µg";
        }
        if (input == MG) {
            return "mg";
        }
        if (input == G) {
            return "g";
        }
        if (input == ML) {
            return "mL";
        }
        System.out.println("Unit enum to string problem!"); // For testing purposes
        return null;
    }
}

/*
 * 
 * Class modeling a medication object
 * 
 */
class Medication {
    private String name; // Med name
    private int dosage; // Med dosage

    private Unit unit; // Medication unit
    private List<LocalTime> times; // List of times to take medicine

    // Constructor (boilerplate 💯)
    Medication(String name, int dosage, Unit unit, List<LocalTime> times) {
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public List<LocalTime> getTimes() {
        return times;
    }

    public void setTimes(List<LocalTime> times) {
        this.times = times;
    }
}

/*
 * 
 * Main program class
 * 
 */

public class App extends Application {
    int currentlyEditingMedication; // Store which medicine we're editing

    public static void main(String[] args) {
        launch(args); // Launch program
    }

    // Read input file and create medication objects
    private List<Medication> createMeds(){
        List<Medication> medications = new ArrayList<>(); // List to store medication objects

        // Dynamically create medications from database file
        Path filePath = Paths.get("medicationList"); // File path string
        int lineNumber = 0; // Int for counting line number

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) { // Create file input reader
            String line; // Hold current lineFiles.newBufferedReader(path, StandardCharsets.UTF_8
            Medication medication; // Variables used to dynamically create medications
            String name = "";
            int dosage = 0;
            Unit unit = Unit.MG;
            Scanner scanner; // Scanner to read 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm"); // Formatter used to convert time text to localtime objects
            while ((line = reader.readLine()) != null) { // Loop through database file
                lineNumber++; // Increment line number
                if (lineNumber % 3 == 1) { // The first line
                    name = line; // Use as med name
                }
                if (lineNumber % 3 == 2) { // The second line
                    scanner = new Scanner(line); // Initalize scanner to break line into tokens
                    dosage = Integer.parseInt(scanner.next()); // First item is med dosage
                    unit = Unit.toUnit(scanner.next()); // Second item is med unit
                }
                if (lineNumber % 3 == 0) { // The third line
                    List<LocalTime> tempTimes = new ArrayList<>(); // Arraylist to hold list of times to take med
                    scanner = new Scanner(line); // Initialize scanner
                    while (scanner.hasNext()) { // For all of the times in line
                        LocalTime tempTime = LocalTime.parse(scanner.next(), formatter); // Parse time like 8:00 into a localtime object using the formatter
                        tempTimes.add(tempTime); // Use as med list of times to take
                    }
                    medication = new Medication(name, dosage, unit, tempTimes); // Create medication object with variables 
                    medications.add(medication); // Add to med list
                }
            }
        } catch (IOException e) { // If file input error
            System.out.println("Can't read file. Will create one to save.");
        } 
        return medications; // Return the med list
    }

    /*
     *
     *  Wipes old medication list and recreates it from memory
     *
     */

    private void saveMeds(List<Medication> medications) { // Take external medication  list
        try {
            PrintWriter out = new PrintWriter("medicationList", "UTF-8"); // Create file writer
            for (int i = 0; i < medications.size(); i++) { // For every medication
                out.println(medications.get(i).getName()); // Print name
                out.println(medications.get(i).getDosage() + " " + Unit.toText(medications.get(i).getUnit())); // Print dosage and unit, spaced
                for (int j = 0; j < medications.get(i).getTimes().size(); j++) { // For every time to take
                    out.print(medications.get(i).getTimes().get(j)); // Print take times (all on one line)
                }
                out.println(""); // Newline
            }
            out.close(); // Close output because VSCode told me to
        } catch (FileNotFoundException e) { // If I don't find the file
            System.out.println("File not found!"); // Cry and quit
        } catch (UnsupportedEncodingException e) { // If there's an encoding problem
            System.out.println("Encoding error!");
        } 
    }

    // Models a basic alert that notifies user
    private void alert(String text) {
        // Alert for incomplete fields
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /* 
     *
     *  Main JavaFX method
     * 
    */

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException { // Main method
        List<Medication> medications = createMeds(); // List to hold medications

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm"); // Formatter for converting time text to localtime objects

        // Alert for confirming deletion
        Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeletion.setTitle("Confirm deletion"); // Window title
        confirmDeletion.setHeaderText(null); // Disable header
        confirmDeletion.setContentText("Are you sure you want to delete this medication?"); // Window content text
        ButtonType deleteButtonYes = new ButtonType("Yes"); // Yes button
        ButtonType deleteButtonNo = new ButtonType("No"); // No button
        confirmDeletion.getButtonTypes().setAll(deleteButtonYes, deleteButtonNo); // Add buttons to window
        
        /*
         * 
         * Main menu scene
         * 
         */


        // Title text of main menu
        Label mainTitle = new Label("Medication Tracker");
        mainTitle.getStyleClass().add("big-title"); // Apply styling to main title

        // Add medication button
        Button mainMenuMedAdd = new Button("Add Medication"); // Create button with text
        mainMenuMedAdd.getStyleClass().addAll("big-menu-button", "button"); // Apply styling to button

        // Edit medication button
        Button mainMenuMedEdit = new Button("Edit Medication");
        mainMenuMedEdit.getStyleClass().addAll("big-menu-button", "button");

        // Delete medication button
        Button mainMenuMedDelete = new Button("Delete Medication");
        mainMenuMedDelete.getStyleClass().addAll("big-menu-button", "button");

        // Close program button
        Button mainMenuExit = new Button("Exit");
        mainMenuExit.setOnAction(e -> {
            Platform.exit(); // End graphical program
            //out.close(); // Close output printwriter
        }); // Exits entire program
        mainMenuExit.getStyleClass().addAll("big-menu-button", "button");

        // Create vertical container to hold buttons and text
        VBox mainMenuBox = new VBox(30); // Space items
        mainMenuBox.setAlignment(Pos.CENTER); // Align items to center of box
        mainMenuBox.getChildren().addAll(mainTitle, mainMenuMedAdd, mainMenuMedEdit, mainMenuMedDelete, mainMenuExit); // Add the title and buttons
        mainMenuBox.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Scene is the entire program window
        Scene mainMenuScene = new Scene(mainMenuBox, 600, 900); // Add our container and set dimensions
        mainMenuScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Include CSS file

        /* 
        *
        *  Scene for adding medication
        *
        */ 

        // Page title
        Label addMedTitle = new Label("Add Medication");
        addMedTitle.getStyleClass().add("big-title"); // Apply styling to title

        // Prompt text
        Label addMedPromptText = new Label("Enter medication name:");
        addMedPromptText.getStyleClass().add("med-title"); // Apply styling to prompt

        // Text field for entering name
        TextField addMedTextField = new TextField();
        addMedTextField.setPromptText("Enter medication name here");
        addMedTextField.getStyleClass().add("text-field"); // Apply styling to text field

        // Go back button
        Button addMedReturn = new Button("Back");
        addMedReturn.getStyleClass().addAll("small-menu-button", "button");

        // Next button
        Button addMedNext = new Button("Next");
        addMedNext.getStyleClass().addAll("small-menu-button", "button");

        // Create horizontal box to hold bottom buttons
        HBox addMedHBox = new HBox(30); // Space items
        addMedHBox.setAlignment(Pos.CENTER); // Align items to the center of the box
        addMedHBox.getChildren().addAll(addMedReturn, addMedNext); // Add the title and buttons
        
        // Create vertical container to hold buttons and text
        VBox addMedBox = new VBox(30); // Space items
        addMedBox.setAlignment(Pos.CENTER); // Align items to center of box
        addMedBox.getChildren().addAll(addMedTitle, addMedPromptText, addMedTextField, addMedHBox); // Add the title and buttons
        addMedBox.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create the scene
        Scene addMedScene = new Scene(addMedBox, 600, 900); // Create scene
        addMedScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Include CSS file (MUST BE DONE AGAIN)

        /*
         * 
         * Add medication second page scene
         * 
         */

        // Page title
        Label addMedTitle2 = new Label("Add Medication");
        addMedTitle2.getStyleClass().add("big-title"); // Apply styling to title
        
        // Dosage prompt text
        Label addMedPromptTextDosage = new Label("Enter medication dosage:");
        addMedPromptTextDosage.getStyleClass().add("med-title"); // Apply styling to prompt

        // Time prompt text
        Label addMedPromptTextTime = new Label("Enter time:");
        addMedPromptTextTime.getStyleClass().add("med-title"); // Apply styling to prompt

        // Frequency prompt text
        Label addMedPromptTextFreq = new Label("Enter frequency:");
        addMedPromptTextFreq.getStyleClass().add("med-title"); // Apply styling to prompt

        // Text field for entering dosage
        TextField addMedTextField2 = new TextField();
        addMedTextField2.setPromptText("Enter number here");
        addMedTextField2.getStyleClass().add("text-field"); // Apply styling to text field
        addMedTextField2.setStyle("-fx-max-width: 200px");

        // Dropdown for dosage unit
        ComboBox<String> addMedDropdownUnit = new ComboBox<>(); // Dropdown combobox
        addMedDropdownUnit.getItems().addAll("µg", "mg", "g", "mL"); // List of available units
        addMedDropdownUnit.setValue("mg"); // Optional default selection
        addMedDropdownUnit.getStyleClass().add("dropdown"); // Apply styling to dropdown

        // Dropdown for time
        ComboBox<String> addMedDropdownTime = new ComboBox<>(); // Dropdown combobox
        addMedDropdownTime.getItems().addAll("00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30");
        addMedDropdownTime.setValue("8:00"); // Optional default selection
        addMedDropdownTime.getStyleClass().add("dropdown"); // Apply styling to dropdown

        // Go back button
        Button addMedReturn2 = new Button("Back");
        addMedReturn2.getStyleClass().addAll("small-menu-button", "button");

        // Save button
        Button addMedSave = new Button("Save");
        addMedSave.getStyleClass().addAll("small-menu-button", "button");

        // Create horizontal box to hold dosage text field and dropdown
        HBox addMedHBoxDosage = new HBox(30); // Space itms
        addMedHBoxDosage.setAlignment(Pos.CENTER); // Align items to the center of the box
        addMedHBoxDosage.getChildren().addAll(addMedTextField2, addMedDropdownUnit); // Add the title and buttons

        // Create horizontal box to hold bottom buttons
        HBox addMedHBox2 = new HBox(30); // Space itms
        addMedHBox2.setAlignment(Pos.CENTER); // Align items to the center of the box
        addMedHBox2.getChildren().addAll(addMedReturn2, addMedSave); // Add the title and buttons
        
        // Create vertical container to hold buttons and text
        VBox addMedBox2 = new VBox(30); // Space items
        addMedBox2.setAlignment(Pos.CENTER); // Align items to center of box
        addMedBox2.getChildren().addAll(addMedTitle2, addMedPromptTextDosage, addMedHBoxDosage, addMedPromptTextTime, addMedDropdownTime, addMedPromptTextFreq, addMedHBox2); // Add the title and buttons
        addMedBox2.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create the scene
        Scene addMedScene2 = new Scene(addMedBox2, 600, 900); // Create scene
        addMedScene2.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Include CSS file

        /*

         * Edit medication scene
         * 
         */
        // Page title
        Label editMedTitle = new Label("Edit Medication");
        editMedTitle.getStyleClass().add("big-title"); // Apply styling to title

        // Go back button
        Button editMedReturn = new Button("Back");
        editMedReturn.getStyleClass().addAll("big-menu-button", "button");
        editMedReturn.setOnAction(e -> primaryStage.setScene(mainMenuScene)); // Switch to main menu screen

        // Create vertical container to hold scrollable med list
        VBox editMedScrollVBox = new VBox(20);
        editMedScrollVBox.setAlignment(Pos.CENTER); // Align items to center
        editMedScrollVBox.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create scrollpane to allow med list to scroll
        ScrollPane editMedScrollPane = new ScrollPane(editMedScrollVBox); // Create scrollpane with med list vbox
        editMedScrollPane.setFitToWidth(true); // Make VBox match ScrollPane width
        editMedScrollPane.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create vertical container to hold buttons and text
        VBox editMedBox = new VBox(30); // Space items
        editMedBox.setAlignment(Pos.CENTER); // Align items to center of box
        editMedBox.getChildren().addAll(editMedTitle, editMedScrollPane, editMedReturn); // Add the title and buttons
        editMedBox.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create the scene
        Scene editMedScene = new Scene(editMedBox, 600, 900); // Create scene
        editMedScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Include CSS file

        /*
         * 
         * Edit medication second page scene
         * 
         */

        // Page title
        Label editMedTitle2 = new Label("Edit Medication");
        editMedTitle2.getStyleClass().add("big-title"); // Apply styling to title
        
        // Dosage prompt text
        Label editMedPromptTextDosage = new Label("Enter medication dosage:");
        editMedPromptTextDosage.getStyleClass().add("med-title"); // Apply styling to prompt

        // Time prompt text
        Label editMedPromptTextTime = new Label("Enter time:");
        editMedPromptTextTime.getStyleClass().add("med-title"); // Apply styling to prompt

        // Frequency prompt text
        Label editMedPromptTextFreq = new Label("Enter frequency:");
        editMedPromptTextFreq.getStyleClass().add("med-title"); // Apply styling to prompt

        // Text field for entering dosage
        TextField editMedTextField2 = new TextField(); // Get dose of selected med
        editMedTextField2.setPromptText("Enter number here");
        editMedTextField2.getStyleClass().add("text-field"); // Apply styling to text field
        editMedTextField2.setStyle("-fx-max-width: 200px");

        // Dropdown for dosage unit
        ComboBox<String> editMedDropdownUnit = new ComboBox<>(); // Dropdown combobox
        editMedDropdownUnit.getItems().addAll("µg", "mg", "g", "mL"); // List of available units
        editMedDropdownUnit.getStyleClass().add("dropdown"); // Apply styling to dropdown

        // Dropdown for time
        ComboBox<String> editMedDropdownTime = new ComboBox<>(); // Dropdown combobox
        editMedDropdownTime.getItems().addAll("00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30");
        editMedDropdownTime.getStyleClass().add("dropdown"); // Apply styling to dropdown

        // Go back button
        Button editMedReturn2 = new Button("Back");
        editMedReturn2.getStyleClass().addAll("small-menu-button", "button");

        // Save button
        Button editMedSave = new Button("Save");
        editMedSave.getStyleClass().addAll("small-menu-button", "button");

        // Create horizontal box to hold dosage text field and dropdown
        HBox editMedHBoxDosage = new HBox(30); // Space itms
        editMedHBoxDosage.setAlignment(Pos.CENTER); // Align items to the center of the box
        editMedHBoxDosage.getChildren().addAll(editMedTextField2, editMedDropdownUnit); // Add the title and buttons

        // Create horizontal box to hold bottom buttons
        HBox editMedHBox2 = new HBox(30); // Space itms
        editMedHBox2.setAlignment(Pos.CENTER); // Align items to the center of the box
        editMedHBox2.getChildren().addAll(editMedReturn2, editMedSave); // Add the title and buttons
        
        // Create vertical container to hold buttons and text
        VBox editMedBox2 = new VBox(30); // Space items
        editMedBox2.setAlignment(Pos.CENTER); // Align items to center of box
        editMedBox2.getChildren().addAll(editMedTitle2, editMedPromptTextDosage, editMedHBoxDosage, editMedPromptTextTime, editMedDropdownTime, editMedPromptTextFreq, editMedHBox2); // Add the title and buttons
        editMedBox2.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create the scene
        Scene editMedScene2 = new Scene(editMedBox2, 600, 900); // Create scene
        editMedScene2.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Include CSS file

        /*
         * 
         * Delete med scene
         * 
         */

        // Page title
        Label deleteMedTitle = new Label("Delete Medication");
        deleteMedTitle.getStyleClass().add("big-title"); // Apply styling to title

        // Go back button
        Button deleteMedReturn = new Button("Back");
        deleteMedReturn.getStyleClass().addAll("big-menu-button", "button");
        deleteMedReturn.setOnAction(e -> primaryStage.setScene(mainMenuScene)); // Switch to main menu screen

        // Create vertical container to hold scrollable med list
        VBox deleteMedScrollVBox = new VBox(20);
        deleteMedScrollVBox.setAlignment(Pos.CENTER); // Align items to center
        deleteMedScrollVBox.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create scrollpane to allow med list to scroll
        ScrollPane deleteMedScrollPane = new ScrollPane(deleteMedScrollVBox); // Create scrollpane with med list vbox
        deleteMedScrollPane.setFitToWidth(true); // Make VBox match ScrollPane width
        deleteMedScrollPane.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create vertical container to hold buttons and text
        VBox deleteMedBox = new VBox(30); // Space items
        deleteMedBox.setAlignment(Pos.CENTER); // Align items to center of box
        deleteMedBox.getChildren().addAll(deleteMedTitle, deleteMedScrollPane, deleteMedReturn); // Add the title and buttons
        deleteMedBox.setStyle("-fx-background-color: lightblue"); // Set background color to blue

        // Create the scene
        Scene deleteMedScene = new Scene(deleteMedBox, 600, 900); // Create scene
        deleteMedScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Include CSS file

        /*
         * 
         * Other code
         * 
         */

        // We have to delcare button actions at the very end because they need to reference everything else

        // Main menu 'add medication' button
        mainMenuMedAdd.setOnAction(e -> primaryStage.setScene(addMedScene)); // Switch to add medicine screen

        // Add medication 'return' button
        addMedReturn.setOnAction(e -> {
            primaryStage.setScene(mainMenuScene); // Return to the main menu
            addMedTextField.clear(); // Clear all the fields if user goes back to main menu without finishing
            addMedTextField2.clear();
        });

        // Add medication 'next' button
        addMedNext.setOnAction(e -> {
            if(addMedTextField.getText().trim().isEmpty()) { // If field is empty
                alert("Enter information to continue, or exit.");  // Blocks until closed
            }
            else {
                primaryStage.setScene(addMedScene2); // Go to second part of add medication scene if numeric
            }
        });

        // Add medication 'return' button
        addMedReturn2.setOnAction(e -> primaryStage.setScene(addMedScene)); // Switch to add medicine screen

        // Add medication 'save' button
        addMedSave.setOnAction( e -> {
            if(addMedTextField2.getText().trim().isEmpty()) { // If field is empty
                alert("Enter information to continue, or exit.");  // Blocks until closed
            }
            else {
                try {
                    Double.parseDouble(addMedTextField2.getText().trim()); // See if dosage input is numeric
                    String tempName = addMedTextField.getText(); // Med name
                    int tempDosage = Integer.parseInt(addMedTextField2.getText()); // Dosage amount
                    String tempUnit = addMedDropdownUnit.getValue(); // Dosage unit
                    LocalTime tempTime = LocalTime.parse(addMedDropdownTime.getValue(), formatter); // Parse times using formatter
                    List<LocalTime> tempTimes = new ArrayList<>(); // Create list to hold times
                    tempTimes.add(tempTime); // Add time (NOTE WE ONLY SUPPORT ONE TIME RIGHT NOW)
                    
                    Medication medication = new Medication(tempName, tempDosage, Unit.toUnit(tempUnit), tempTimes); // Create new medication object (have to cast unit to unit object)
                    medications.add(medication); // Add medication to list
                    saveMeds(medications); // Resave med list to file

                    primaryStage.setScene(mainMenuScene); // Return to main menu
                    addMedTextField.clear(); // Clear input fields upon saving
                    addMedTextField2.clear();
                }
                catch (NumberFormatException e2) { // Catch non-numeric error
                        alert("Dosage must be a number."); // Throw error message to user
                }
            }
        });

        // Main menu 'edit medication' button
        mainMenuMedEdit.setOnAction(e -> {
            if (medications.size() > 0) { // If there are medicines to edit
                editMedScrollVBox.getChildren().clear(); // Clear the vbox to start fresh
                for (int i = 0; i < medications.size(); i++) { // For all of the medications
                    Button button = new Button(medications.get(i).getName()); // Create new button
                    button.setUserData(i); // Store ID as data in the button
                    button.setOnAction(f -> { // When a button is clicked
                        currentlyEditingMedication = (int) button.getUserData(); // Store what medication we're working on
                        editMedTextField2.setText(Integer.toString(medications.get(currentlyEditingMedication).getDosage())); // Get dosage from selected med
                        editMedDropdownUnit.setValue(Unit.toText(medications.get(currentlyEditingMedication).getUnit())); // Get unit from selected med 
                        editMedDropdownTime.setValue(medications.get(currentlyEditingMedication).getTimes().get(0).format(formatter)); // Pull time from selected med     
                        primaryStage.setScene(editMedScene2); // Continue to editing details scene
                    }); // Go to add med scene
                    button.getStyleClass().addAll("big-menu-button", "button"); // Apply styling to button
                    button.setStyle("-fx-background-color: #9d9"); // Override and make these buttons green
                    editMedScrollVBox.getChildren().add(button); // Add button to vbox
                }
                primaryStage.setScene(editMedScene); // Advance to the 'edit medication' scene
            }
            else {
                alert("There are no medications to edit. Please add a medication first.");
            }
        }); // Switch to edit medicine screen

        // Edit medication 'return' button
        editMedReturn2.setOnAction(e -> primaryStage.setScene(editMedScene)); // Switch to add medicine screen

        // Edit medication 'save' button
        editMedSave.setOnAction( e -> {
            if(editMedTextField2.getText().trim().isEmpty()) { // If field is empty
                alert("Enter information to continue, or exit.");  // Blocks until closed
            }
            else {
                try {
                    Double.parseDouble(editMedTextField2.getText().trim()); // See if dosage input is numeric
                    medications.get(currentlyEditingMedication).setDosage(Integer.parseInt(editMedTextField2.getText())); // Re-set dosage
                    medications.get(currentlyEditingMedication).setUnit(Unit.toUnit(editMedDropdownUnit.getValue())); // Re-set unit
                    medications.get(currentlyEditingMedication).getTimes().set(0, LocalTime.parse(editMedDropdownTime.getValue(), formatter));
                    saveMeds(medications); // Resave med list to file

                    primaryStage.setScene(editMedScene); // Return to main menu
                    editMedTextField2.clear(); // Clear input fields upon saving
                }
                catch (NumberFormatException e2) { // Catch non-numeric error
                        alert("Dosage must be a number."); // Throw error message to user
                }
            }
        });

        // Main menu 'delete' button
        mainMenuMedDelete.setOnAction(e -> {
            if (medications.size() > 0) { // If there are medicines to delete
                deleteMedScrollVBox.getChildren().clear(); // Clear the vbox to start fresh
                for (int i = 0; i < medications.size(); i++) { // For all of the medications
                    Button button = new Button(medications.get(i).getName()); // Create new button
                    button.setUserData(i); // Store ID as data in the button
                    button.setOnAction(f -> { // When a button is clicked
                        Optional<ButtonType> confirmDeletionResult = confirmDeletion.showAndWait(); // Confirmation popup
                        if (confirmDeletionResult.isPresent()) { // If confirmation exists (it won't if you click x on the window)
                            if (confirmDeletionResult.get() == deleteButtonYes) { // If user clicked yes
                                currentlyEditingMedication = (int) button.getUserData(); // Store what medication we're working on
                                medications.remove(currentlyEditingMedication); // Remove selected med from list
                                saveMeds(medications); // Resave med list to file
                                primaryStage.setScene(mainMenuScene); // Return to main menu (forces users to reload list of meds)
                            }
                        }
                }); // Go to add med scene
                button.getStyleClass().addAll("big-menu-button", "button"); // Apply styling to button
                button.setStyle("-fx-background-color: #d99"); // Override and make these buttons red
                deleteMedScrollVBox.getChildren().add(button); // Add button to vbox
                }
                primaryStage.setScene(deleteMedScene); // Advance to the 'edit medication' scene
            }
            else {
                alert("There are no medications to delete. Please add a medication first.");
            }
        });

        // Initiation of program
        primaryStage.setTitle("Medication tracker"); // Set program title
        primaryStage.setScene(mainMenuScene); // Set stage to our scene
        primaryStage.show(); // Show scene
    }
}