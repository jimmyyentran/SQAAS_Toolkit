package com.sqasquared.toolkit.ui;

import com.sqasquared.toolkit.App;
import com.sqasquared.toolkit.UserSession;
import com.sqasquared.toolkit.connection.ASM;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.util.Callback;
import org.apache.commons.mail.EmailException;
import org.apache.http.auth.InvalidCredentialsException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by JTran on 11/17/2016.
 */
public class MainController implements Initializable, ControlledScreen {
    public Button SSUButton;
    public HTMLEditor editor;
    public Label fullName;
    public TextField textFieldCc;
    public TextField textFieldTo;
    public TextField textFieldSubject;
    public Button genEmailButton;
    public Button SSUPButton;
    public Button TCRButton;
    public BorderPane basePane;
    public Label statusLabel;
    private ScreensController screensController;
    private HostServices hostServices;

    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void setHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void active() {
        fullName.setText(UserSession.getFullName());
    }

    public void initialize(URL location, ResourceBundle resources) {
//        WebView webview = (WebView) editor.lookup(".web-view");

        // Removing internal rallyLoader
//        webview.getEngine().getLoadWorker().stateProperty().addListener(new
// ChangeListener<State>() {
//            @Override
//            public void changed(ObservableValue<? extends State>
// observable, State oldValue, State newValue) {
//                Platform.runLater(() -> webview.getEngine().getLoadWorker()
// .cancel());
//            }
//        });
//
//        // Adding external host services
//        webview.getEngine().locationProperty().addListener(new
// ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String>
// observable, String oldValue, String newValue) {
//                System.out.println("observable = " + observable);
//                System.out.println("oldValue = " + oldValue);
//                System.out.println("newValue = " + newValue);
//                hostServices.showDocument(newValue);
//            }
//        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Email Generator Error");
        alert.setContentText(message);
        alert.showAndWait();

    }

    public void ssuClick(ActionEvent actionEvent) {
        try {
            String html = App.userSession.generateHtml(UserSession.SSU);
            String to = App.userSession.getEmailTo(UserSession.SSU);
            String cc = App.userSession.getEmailCC();
            String subject = App.userSession.getEmailSubject();
            textFieldTo.setText(to);
            textFieldCc.setText(cc);
            textFieldSubject.setText(subject);
            editor.setHtmlText(html);

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Story status update successful!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.getMessage());

            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Story status update failed!");
        }
        return;
    }

    public void ssupClick(ActionEvent actionEvent) {
        try {
            String html = App.userSession.generateHtml(UserSession.SSUP);
            String to = App.userSession.getEmailTo(UserSession.SSUP);
            String cc = App.userSession.getEmailCC();
            String subject = App.userSession.getEmailSubject();
            textFieldTo.setText(to);
            textFieldCc.setText(cc);
            textFieldSubject.setText(subject);
            editor.setHtmlText(html);

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Story status update progress successful!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.getMessage());

            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Story status update progress failed!");
        }
        return;
    }

    public void eodClick(ActionEvent actionEvent) {
        try {
            String html = App.userSession.generateHtml(UserSession.EOD);
            editor.setHtmlText(html);

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("End of day successful!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.getMessage());

            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("End of day failed!");
        }
        return;
    }

    public void refreshClick(ActionEvent actionEvent) {
        clearFields();
        try {
            App.userSession.refreshTasks();

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Refresh successful!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.getMessage());

            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Refresh failed!");
        }
    }

    public void tcrClick(ActionEvent actionEvent) {
        try {
            App.userSession.loginASM();
        } catch (Exception e) {
            Optional<List<String>> result = tfsLoginDialog();

            if (result.isPresent()) {
                try {
                    App.userSession.loginASM(result.get().get(0).toString(), result.get().get(1).toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Internal Error! " + ex.getMessage());
                    return;
                } catch (InvalidCredentialsException ex) {
                    ex.printStackTrace();
                    showAlert("Invalid Login: " + ex.getMessage());
                    return;
                } catch (Exception ex){
                    ex.printStackTrace();
                    showAlert("Internal Error! " + ex.getMessage());
                    return;
                }
            }
        }

        // Login successful
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("ASM login successful!");

        try {
            Optional<List<String>> res = tfsItemChooser();

            String pbi = res.get().get(0);
            String project = "";

            if(pbi.length() == 0){
                return;
            }

            if(res.get().get(1).equals("Instep 1.0")){
                project = ASM.INSTEP1;
            } else if(res.get().get(1).equals("Instep 2.0")){
                project = ASM.INSTEP2;
            }

            editor.setHtmlText(App.userSession.generateTestCases(pbi, project, UserSession.TCR));

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Test case ... successful!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Query failed: " + e.getMessage());
            return;
        }
    }

    private Optional<List<String>> tfsLoginDialog() {
        // Login Dialog
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("ASM Login");
        dialog.setHeaderText("Please enter your ASM credentials");
        dialog.setResizable(true);

        Label label1 = new Label("Username: ");
        Label label2 = new Label("Password: ");
        TextField text1 = new TextField();
        TextField text2 = new PasswordField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData
                .OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, List<String>>() {
            @Override
            public List<String> call(ButtonType b) {

                if (b == buttonTypeOk) {
                    List<String> credentials = new ArrayList<String>();
                    credentials.add(text1.getText());
                    credentials.add(text2.getText());

                    return credentials;
                }

                return null;
            }
        });

        return dialog.showAndWait();
    }

    private Optional<List<String>> tfsItemChooser() {
        // Login Dialog
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Test Case Generator");
        dialog.setHeaderText("Please enter the Product Backlog Item ID and Project");
        dialog.setResizable(true);

        ObservableList<String> options = FXCollections.observableArrayList(
                "Instep 1.0",
                "Instep 2.0"
        );

        Label label1 = new Label("PBI id:" );
        Label label2 = new Label("Project: ");
        TextField text1 = new TextField();

        //Force numbers only
        text1.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    text1.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ComboBox comboBox = new ComboBox(options);
        comboBox.getSelectionModel().select(0);

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(comboBox, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData
                .OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, List<String>>() {
            @Override
            public List<String> call(ButtonType b) {

                if (b == buttonTypeOk) {
                    List<String> credentials = new ArrayList<String>();
                    credentials.add(text1.getText());
                    credentials.add((String)comboBox.getValue());

                    return credentials;
                }

                return null;
            }
        });

        return dialog.showAndWait();
    }

    public void generateEmail(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Email");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("Email", "*.eml"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user" +
                ".home") + System.getProperty("file.separator")
                + "/Desktop"));
        File selectedFile = fileChooser.showSaveDialog(App.stage);
        if (selectedFile == null) {
            return;
        }

        String to = textFieldTo.getText();
        String cc = textFieldCc.getText();
        String subject = textFieldSubject.getText();
        String html = editor.getHtmlText();
        String loc = selectedFile.getAbsolutePath();

        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Save email successful!");
        try {
            App.userSession.generateEmail(to, cc, subject, html, loc);
        } catch (EmailException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return;
    }

    public void sendEmail(ActionEvent actionEvent) {
        String to = textFieldTo.getText();
        String cc = textFieldCc.getText();
        String subject = textFieldSubject.getText();
        String html = editor.getHtmlText();

        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Send email successful!");
        try {
            App.userSession.sendEmail(to, cc, subject, html);
        } catch (InvalidCredentialsException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Credentials");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return;
    }

    private void clearFields() {
        textFieldTo.clear();
        textFieldSubject.clear();
        textFieldCc.clear();
        editor.setHtmlText("");
    }

    public void preferencesClick(ActionEvent actionEvent) {
        statusLabel.setTextFill(Color.RED);
        statusLabel.setText("WARNING: Becareful what you edit!");
//        final Popup popup = new Popup();
        final Dialog dialog = new Dialog();
        BorderPane borderPane = new BorderPane();
        PreferencesView preferencesView = new PreferencesView();
        TableView tableView = preferencesView.generatePreferences();
        borderPane.setCenter(tableView);
        dialog.getDialogPane().setContent(tableView);
        ButtonType buttonTypeOk = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.showAndWait();
//        popup.getContent().addAll(tableView);
//        popup.show(fullName.getScene().getWindow());
    }
}
