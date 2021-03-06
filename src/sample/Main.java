package sample;

import com.sun.xml.internal.ws.util.ASCIIUtility;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
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

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

        comboBox_Currency.getItems().add("Choose a file first");
        comboBox_Currency.setValue(comboBox_Currency.getItems().get(0));
        for (int i = 0; i < webPage.getCurrenciesSymbols().size(); i++) {
            comboBox_desiredCurreny.getItems().add(webPage.getCurrenciesSymbols().get(i) + " - " + webPage.getCurrenciesNames().get(i));
        }
        comboBox_desiredCurreny.setMaxSize(150,50);
        comboBox_Currency.setMaxSize(150,50);
        buttonOpenFile.setMaxSize(150, 50);
        buttonCalculate.setMaxSize(150, 50);
        labelPath.setMaxSize(100,50);

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
                    if (file != null) {
                        try {
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                            Date date = new Date(System.currentTimeMillis());
                            FileWriter fileWriter = new FileWriter(file.getPath(), true);
                            fileWriter.write("\n// Result from: " + dateFormatter.format(date) + " --> " + labelCalculate.getText());
                            fileWriter.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                labelSave.setVisible(true);
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                } finally {
                                    labelSave.setVisible(false);
                                }
                            }
                        };
                        thread.start();
                    }
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
                    if (comboBox_Currency.getItems().get(0) != "Choose a file first") {
                        labelCalculate.setText(decimalFormat.format(Double.valueOf(value)) + " " + webPage.getCurrenciesSymbols().get(comboBox_desiredCurreny                                            .getSelectionModel().getSelectedIndex()));
                    }
                } else {
                    labelCalculate.setText("You need to choose a file first!");
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
        LinkedList<String> errors = new LinkedList<>();
        this.dates = new LinkedList<>();
        this.quantity = new LinkedList<>();
        this.currencies = new LinkedList<>();
        comboBox_Currency.getItems().clear();
        boolean isErrorOccure = false;
        try {
            Scanner scanner = new Scanner(new File(file.getPath()));
            if (scanner.hasNextLine()) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    Scanner lineScanner = new Scanner(line);
                    lineScanner.useDelimiter(";");
                    while (lineScanner.hasNext()) {
                        String temp = lineScanner.next();
                        if (checkIfItsDate(temp)) {
                            dates.add(temp);
                        } else if (checkIfItsValue(temp)) {
                            quantity.add(temp);
                        } else if (checkIfItsShortcut(temp)) {
                            currencies.add(temp);
                            comboBox_Currency.getItems().add(temp);
                        } else {
                            if (temp.charAt(0) != '/' && temp.charAt(1) != '/') {
                                System.out.println(temp);
                                errors.add(temp);
                                isErrorOccure = true;
                            } else {
                                continue;
                            }
                        }
                    }
                }
                comboBox_Currency.setValue(comboBox_Currency.getItems().get(0));
                labelQuantity.setText(quantity.get(comboBox_Currency.getSelectionModel().getSelectedIndex()));
                labelQuantity.setText(quantity.get(comboBox_Currency.getSelectionModel().getSelectedIndex()) + " " +
                        currencies.get(comboBox_Currency.getSelectionModel().getSelectedIndex()));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Załadowano!");
                alert.setHeaderText("Pomyślnie wcyztano dane z pliku");
                alert.setContentText("Plik zostanie zapamiętany przy nastepnym uruchomieniu programu\n\nkliknij ok żeby przejść dalej.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd wczytywania");
                alert.setHeaderText("Sprawdź składnie danych w wybranym pliku");
                alert.setContentText("Data: DD-MM-RRRR\tnp: 10.12.2019\nWaluta: XYZ\tnp: PLN\nIlość: xxx.yyy,zz\tnp: 111.222,33");
                alert.showAndWait();
            }

        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        } finally {
            if (isErrorOccure) {
                String temp = "";
                for (int i=0; i < errors.size(); i++) {
                    temp += errors.get(i) + " ";
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Niektóre dane nie zostały wczytane.");
                alert.setHeaderText("Sprawdź składnie tych danych:");
                alert.setContentText(temp);
                alert.showAndWait();
            }
        }
    }

    private String calculate(String oldQuantity, String oldDesireCurrencyValue, String oldCurrencyValue) {
        double newQuantity = removeCommaSigns(oldQuantity);
        double newDesireCurrencyValue = removeCommaSigns(oldDesireCurrencyValue);
        double newCurrencyValue = removeCommaSigns(oldCurrencyValue);
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


