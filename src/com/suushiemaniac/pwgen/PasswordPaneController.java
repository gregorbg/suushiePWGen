package com.suushiemaniac.pwgen;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class PasswordPaneController implements Initializable {

    private static final int MOUSE_DRAG_MAX = 75;
    private static final char[] SMALL = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private static final char[] CAPITAL = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static final char[] NUMBERS = {'0','1','2','3','4','5','6','7','8','9'};
    private static final char[] SYMBOLS = {'!','"','§','$','%','&','/','(',')','=','°','?','^','+','*','#','\'',',','.','-',';',':','_'};

    private int recentMouseDrag = 0;
    private String currentPassword = "";
    private Random passwordRandom, symbolTypeRandom;

    @FXML
    private GridPane gridPaneHead;

    @FXML
    private ChoiceBox<String> choiceBoxAlg;

    private Spinner<Integer> spinnerLength;

    @FXML
    private CheckBox checkBoxSmall;

    @FXML
    private CheckBox checkBoxCapital;

    @FXML
    private CheckBox checkBoxNumbers;

    @FXML
    private CheckBox checkBoxSymbols;

    @FXML
    private ProgressBar progressBarPassword;

    @FXML
    private StackPane paneMouseGen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.spinnerLength = new Spinner<>(1, Integer.MAX_VALUE, 10, 1);
        this.gridPaneHead.add(this.spinnerLength, 1, 0);
        this.spinnerLength.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> resetPassword());

        this.choiceBoxAlg.getSelectionModel().select(0);
        this.choiceBoxAlg.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> resetPassword());

        this.paneMouseGen.setStyle("-fx-border-color:black");

        this.passwordRandom = new Random();
        this.symbolTypeRandom = new Random();
    }

    public void minTick() {
        if (!checkBoxSmall.isSelected() && !checkBoxCapital.isSelected() && !checkBoxNumbers.isSelected() && !checkBoxSymbols.isSelected()) checkBoxSmall.setSelected(true);
        resetPassword();
    }

    private ArrayList<char[]> getSelectedCharArrays() {
        ArrayList<char[]> selectedTypesList = new ArrayList<>();
        if (checkBoxSmall.isSelected()) selectedTypesList.add(SMALL);
        if (checkBoxCapital.isSelected()) selectedTypesList.add(CAPITAL);
        if (checkBoxNumbers.isSelected()) selectedTypesList.add(NUMBERS);
        if (checkBoxSymbols.isSelected()) selectedTypesList.add(SYMBOLS);
        return selectedTypesList;
    }

    private int getSelectedCount() {
        CheckBox[] checkBoxes = {this.checkBoxSmall, this.checkBoxCapital, this.checkBoxNumbers, this.checkBoxSymbols};
        int count = 0;
        for (CheckBox box : checkBoxes) if (box.isSelected()) count++;
        return count;
    }

    public void passwordMouseDrag(MouseEvent event) {
        this.recentMouseDrag++;
        if (this.recentMouseDrag >= MOUSE_DRAG_MAX) {
            this.recentMouseDrag = 0;

            int currentCoord = (int) (event.getX() * event.getY());
            if (this.choiceBoxAlg.getSelectionModel().getSelectedIndex() == 1) currentCoord *= this.passwordRandom.nextInt(Integer.MAX_VALUE);

            ArrayList<char[]> selectedTypesList = getSelectedCharArrays();
            char[] currentType = selectedTypesList.get(symbolTypeRandom.nextInt(getSelectedCount()));
            this.currentPassword += currentType[Math.abs(currentCoord) % currentType.length];
            this.progressBarPassword.setProgress(this.currentPassword.length() / (double) this.spinnerLength.getValue());
        }
        if (this.currentPassword.length() == this.spinnerLength.getValue()) {
            passwordOutput(this.currentPassword);
            resetPassword();
        }
    }

    private void resetPassword() {
        this.currentPassword = "";
        this.progressBarPassword.setProgress(0d);
    }

    private void passwordOutput(String generatedPassword) {
        Alert password = new Alert(Alert.AlertType.INFORMATION);
        password.setTitle("suushie password generator");
        password.setHeaderText("Password was created");
        password.setContentText(generatedPassword);
        password.showAndWait();
    }
}
