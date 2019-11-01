package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.*;
import sample.elements.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Main extends Application implements gridElements {
    public File file;
    private LinkedList<String> dates = new LinkedList<>();
    private LinkedList<String> quantity = new LinkedList<>();
    private LinkedList<String> currencies = new LinkedList<>();

    private Desktop desktop = Desktop.getDesktop();
    private FileChooser fileChooser = new FileChooser();
    private HTMLWebPage webPage = new HTMLWebPage("https://www.money.pl/pieniadze/nbp/srednie/");

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        init(primaryStage);
        setGridConstraints();
        this.webPage.printWeb();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void init(Stage primaryStage) {
        primaryStage.setTitle("Currency Calculator");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        layout.setPadding(new Insets(10));
        layout.setVgap(15);
        layout.setHgap(15);
        labelSave.setVisible(false);

        comboBox_Currency.getItems().add("Choose file first");
        comboBox_Currency.setValue(comboBox_Currency.getItems().get(0));
        for (int i = 0; i < webPage.getCurrenciesSymbols().size(); i++) {
            comboBox_desiredCurreny.getItems().add(webPage.getCurrenciesSymbols().get(i) + " - " + webPage.getCurrenciesNames().get(i));
        }
        comboBox_desiredCurreny.setMaxSize(150,50);
        comboBox_Currency.setMaxSize(150,50);
        buttonOpenFile.setMaxSize(150, 50);
        buttonCalculate.setMaxSize(150, 50);
        comboBox_desiredCurreny.setValue(comboBox_desiredCurreny.getItems().get(0));
        labelDesireCurrencyValue.setText(webPage.getCurrenciesValues().get(comboBox_desiredCurreny.getSelectionModel().getSelectedIndex())  + " zł");

        layout.getChildren().addAll(labelPath, labelCurrency, labelDesireCurreny, temp, result, buttonOpenFile, comboBox_Currency, comboBox_desiredCurreny,
                buttonCalculate, labelCalculate, buttonEditFile, labelQuantity, labelDesireCurrencyValue, buttonSaveFile, labelSave);

        buttonOpenFile.setOnAction( new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        labelPath.setText(file.getName());
                        runScanner(file);
                    }
                }
            } );
        buttonEditFile.setOnAction( new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (file != null && Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } );
        buttonSaveFile.setOnAction( new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    labelSave.setVisible(true);
                }
            } );
        buttonCalculate.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DecimalFormat decimalFormat = new DecimalFormat("##,###,###.##");
                if (labelPath.getText() != "'path:'") {
                    int selectedCurrency_index = 0;
                    for (String tempCurrency: webPage.getCurrenciesSymbols()) {
                        if (tempCurrency.equals(comboBox_Currency.getSelectionModel().getSelectedItem())) {
                            break;
                        } else {
                            selectedCurrency_index++;
                        }
                    }
                    String value = String.valueOf(calculate(
                            String.valueOf(quantity.get(comboBox_Currency.getSelectionModel().getSelectedIndex())),
                            String.valueOf(webPage.getCurrenciesValues().get(comboBox_desiredCurreny.getSelectionModel().getSelectedIndex())),
                            String.valueOf(webPage.getCurrenciesValues().get(selectedCurrency_index))));
                    if (comboBox_Currency.getItems().get(0) != "Select file first") {
                        labelCalculate.setText(decimalFormat.format(Double.valueOf(value)) + " " + webPage.getCurrenciesSymbols().get(comboBox_desiredCurreny                                            .getSelectionModel().getSelectedIndex()));
                    }
                } else {
                    labelCalculate.setText("You need to choose file first!");
                }
            }
        });

        comboBox_Currency.valueProperty().addListener( new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (comboBox_Currency.getItems().size() > 0) {
                    labelQuantity.setText(quantity.get(comboBox_Currency.getSelectionModel().getSelectedIndex()) + " " +
                            currencies.get(comboBox_Currency.getSelectionModel().getSelectedIndex()));
                }
            }
        });
        comboBox_desiredCurreny.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                labelDesireCurrencyValue.setText(webPage.getCurrenciesValues().get(comboBox_desiredCurreny.getSelectionModel().getSelectedIndex()) + " zł");
            }
        });
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

    private void runScanner(File file) {
        this.dates = new LinkedList<>();
        this.quantity = new LinkedList<>();
        this.currencies = new LinkedList<>();
        comboBox_Currency.getItems().clear();
        try {
            Scanner scanner = new Scanner(new File(file.getPath()));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(";");
                while (lineScanner.hasNext()) {
                    String temp = lineScanner.next();
                    if(checkIfItsDate(temp)) {
                        dates.add(temp);
                    } else if (checkIfItsValue(temp)) {
                        quantity.add(temp);
                    } else if (checkIfItsShortcut(temp)) {
                        currencies.add(temp);
                        comboBox_Currency.getItems().add(temp);
                    }
                }
            }
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        } finally {
            comboBox_Currency.setValue(comboBox_Currency.getItems().get(0));
            labelQuantity.setText(quantity.get(comboBox_Currency.getSelectionModel().getSelectedIndex()));
            labelQuantity.setText(quantity.get(comboBox_Currency.getSelectionModel().getSelectedIndex()) + " " +
                    currencies.get(comboBox_Currency.getSelectionModel().getSelectedIndex()));
        }
    }

    private String calculate(String oldQuantity, String oldDesireCurrencyValue, String oldCurrencyValue) {
        double newQuantity = removeCommaSigns(oldQuantity);
        double newDesireCurrencyValue = removeCommaSigns(oldDesireCurrencyValue);
        double newCurrencyValue = removeCommaSigns(oldCurrencyValue);
        System.out.println("New quantity: " + newQuantity);
        System.out.println("New desireChoiceBoxValue: " + newDesireCurrencyValue);
        System.out.println("New currencyValue: " + newCurrencyValue);
        double result = (newCurrencyValue/newDesireCurrencyValue)*newQuantity;
        return String.valueOf(result);
    }

    private double removeCommaSigns(String value) {
        String parts = "";
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '.') {
                if (value.charAt(i) == ',') {
                    parts += '.';
                } else {
                    parts += value.charAt(i);
                }
            }
        }
        double newValue = Double.valueOf(parts);
        return newValue;
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

    private boolean checkIfItsValue(String currency) {
        String acceptable = "0123456789.,";
        Boolean isAcceptable = true;
        int commaCounter = 0;
        for (int i = 0; i < currency.length(); i++) {
            isAcceptable = false;
            for (int j = 0; j < acceptable.length(); j++) {
                if (currency.charAt(i) == acceptable.charAt(j)) {
                    isAcceptable = true;
                    if (currency.charAt(i) == ',') {
                        commaCounter++;
                    }
                }
            }
            if (!isAcceptable || commaCounter > 1) {
                return false;
            }
        }
        return isAcceptable;

    }

    private boolean checkIfItsShortcut(String shortcut) {
        for (String temp: webPage.getCurrenciesSymbols()) {
            if (temp.equals(shortcut)) {
                return true;
            }
        }
        return false;
    }
}


