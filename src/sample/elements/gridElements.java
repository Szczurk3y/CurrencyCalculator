package sample.elements;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public interface gridElements {
    GridPane layout = new GridPane();
    Label labelCurrency = new Label("Currency:");
    Label labelPath = new Label("'path:'");
    Label labelDesireCurreny = new Label("Desire currency:");
    Label labelCalculate = new Label("0.00");
    Label labelSave = new Label("saved!");
    Label labelCurrencyValue = new Label("Quantity");
    Label labelDesireCurrencyValue = new Label("");
    Label temp = new Label("");
    Label result = new Label("Result:");
    Button buttonOpenFile = new Button("Choose file");
    Button buttonSaveFile = new Button("Save to file");
    Button buttonEditFile = new Button("Edit file");
    Button buttonCalculate = new Button("Calculate!");
}
