package sample;

import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.elements.*;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.omg.CORBA.Any;

import java.awt.*;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Scanner;

public class Main extends Application implements gridElements {
    ChoiceBox<String> choiceBox_Currency = new ChoiceBox<>();
    ChoiceBox<String> choiceBox_desiredCurreny = new ChoiceBox<>();
    LinkedList<String> dates = new LinkedList<>();
    LinkedList<String> values = new LinkedList<>();
    Desktop desktop = Desktop.getDesktop();
    FileChooser fileChooser = new FileChooser();
    public File file;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        initialize(primaryStage);
        setGridConstraints();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void runScanner(File file) {
        choiceBox_Currency.getItems().clear();
        choiceBox_desiredCurreny.getItems().clear();
        this.dates = new LinkedList<>();
        this.values = new LinkedList<>();
        LinkedList<String> tempCurrencies = new LinkedList<>();
        try {
            Scanner scanner = new Scanner(new File(file.getPath()));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(";");
                while (lineScanner.hasNext()) {
                    String temp = lineScanner.next();
                    if(checkIfItsDate(temp))
                        dates.add(temp);
                    else if(checkIfItsValues(temp))
                        values.add(temp);
                    else if (checkIfItsShortcuts(temp))
                        tempCurrencies.add(temp);
                }
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Printing dates:");
        for (int i = 0; i < dates.size(); i++) {
            System.out.println(dates.get(i));
        }
        System.out.println("Printing values:");
        for (int i = 0; i < values.size(); i++) {
            System.out.println(values.get(i));
        }
        System.out.println("Printing shortcuts:");
        for (int i = 0; i < tempCurrencies.size(); i++) {
            choiceBox_Currency.getItems().add(tempCurrencies.get(i));
            choiceBox_desiredCurreny.getItems().add(tempCurrencies.get(i));
            System.out.println(tempCurrencies.get(i));
        }
        choiceBox_Currency.setValue(choiceBox_Currency.getItems().get(0));
        choiceBox_desiredCurreny.setValue(choiceBox_Currency.getItems().get(1));
    }

    private boolean checkIfItsDate(String date) {
        Scanner lineScanner = new Scanner(date);
        if (date.length() == 10) {
            for (int i = 0; i < date.length(); i++) {
                if (date.charAt(i) == '.' || date.charAt(i) == '-') {
                    lineScanner.useDelimiter(Character.toString(date.charAt(i)));
                    String temp = new String();
                    while (lineScanner.hasNext()) {
                        temp = lineScanner.next();
                        if (temp.length() != 2) {
                            if (lineScanner.hasNext()) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;

    }

    private boolean checkIfItsValues(String currency) {
        String acceptable = "0123456789.,";
        Boolean isAcceptable = true;
        for (int i = 0; i < currency.length(); i++) {
            isAcceptable = false;
            for (int j = 0; j < acceptable.length(); j++) {
                if (currency.charAt(i) == acceptable.charAt(j)) {
                    isAcceptable = true;
                }
            }
            if (!isAcceptable) {
                return false;
            }
        }
        return isAcceptable;

    }

    private boolean checkIfItsShortcuts(String shortcut) {
        String acceptable = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Boolean isAcceptable = true;
        if (shortcut.length() > 4) {
            isAcceptable = false;
        } else {
            for (int i = 0; i < shortcut.length(); i++) {
                isAcceptable = false;
                for (int j = 0; j < acceptable.length(); j++) {
                    if (shortcut.charAt(i) == acceptable.charAt(j)) {
                        isAcceptable = true;
                    }
                }
                if (!isAcceptable) {
                    return false;
                }
            }
        }
        return isAcceptable;
    }

    private void setGridConstraints() {
        int i = 1;
        int j = 1;
        for (Node element : layout.getChildren()) {
            if (j == 6) {
                i++;
                j = 1;
            }
            GridPane.setConstraints(element, i, j);
            j++;
        }

    }

    private void initialize(Stage primaryStage) {
        Scene scene = new Scene(layout, 330, 220 );
        primaryStage.setTitle("Currency Calculator");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        layout.setPadding(new Insets(10));
        layout.setVgap(15);
        layout.setHgap(15);
        labelSave.setVisible(false);
        labelCurrencyLoaded.setVisible(false);
        labelDesireCurrencyLoaded.setVisible(false);

        choiceBox_Currency.getItems().add("Select file first");
        choiceBox_Currency.setValue(choiceBox_Currency.getItems().get(0));
        choiceBox_desiredCurreny.getItems().add("Selct file first");
        choiceBox_desiredCurreny.setValue(choiceBox_desiredCurreny.getItems().get(0));
        layout.getChildren().addAll(labelPath, labelCurrency, labelDesireCurreny, temp, result, buttonOpenFile, choiceBox_Currency, choiceBox_desiredCurreny,
                buttonCalculate, labelCalculate, buttonEditFile, labelCurrencyLoaded, labelDesireCurrencyLoaded, buttonSaveFile, labelSave);

        buttonOpenFile.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        labelPath.setText(file.getName());
                        runScanner(file);
                    }
                }
            }
        );

        buttonEditFile.setOnAction((final ActionEvent e) -> {
            if (file != null) {
                try {
                    desktop.open(file);
                    runScanner(file);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });


        buttonSaveFile.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    labelSave.setVisible(true);
                }
            }
        );
    }

    private void editFile(File file) {
        EventQueue.invokeLater(() -> {
            try {
                desktop.open(file);
                EventQueue.invokeLater(() -> {
                    runScanner(file);
                });
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }
}


