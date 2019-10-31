package sample.elements;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ChoiceBox;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;


public interface gridElements {
    GridPane layout = new GridPane();
    Label labelCurrency = new Label("Currency:");
    Label labelPath = new Label("'path:'");
    Label labelDesireCurreny = new Label("Desire currency:");
    Label labelCalculate = new Label("0.00");
    Label labelSave = new Label("saved!");
    Label labelQuantity = new Label("Quantity");
    Label labelDesireCurrencyValue = new Label("");
    Label temp = new Label("");
    Label result = new Label("Result:");
    Button buttonOpenFile = new Button("Choose file");
    Button buttonSaveFile = new Button("Save to file");
    Button buttonEditFile = new Button("Edit file");
    Button buttonCalculate = new Button("Calculate!");
    Scene scene = new Scene(layout, 400, 220 );
    ComboBox<String> comboBox_Currency = new ComboBox<>();
    ComboBox<String> comboBox_desiredCurreny = new ComboBox<String>();

}
