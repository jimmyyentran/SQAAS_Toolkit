package com.sqasquared.toolkit;

/**
 * Created by jimmytran on 12/4/16.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class TableViewSample extends Application {

    public static final String Column1MapKey = "Key";
    public static final String Column2MapKey = "Value";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(450);
        stage.setHeight(600);

        final Label label = new Label("Student IDs");
        label.setFont(new Font("Arial", 20));

        TableColumn<Map, String> firstDataColumn = new TableColumn<>("key");
        TableColumn<Map, String> secondDataColumn = new TableColumn<>("value");

        firstDataColumn.setCellValueFactory(new MapValueFactory(Column1MapKey));
        firstDataColumn.setMinWidth(200);
        secondDataColumn.setCellValueFactory(new MapValueFactory(Column2MapKey));
        secondDataColumn.setMinWidth(200);

        TableView table_view = null;
        try {
            table_view = new TableView<>(generateDataInMap());
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        table_view.setEditable(true);
        table_view.getSelectionModel().setCellSelectionEnabled(true);
        table_view.getColumns().setAll(firstDataColumn, secondDataColumn);
        Callback<TableColumn<Map, String>, TableCell<Map, String>>
                cellFactoryForMap = new Callback<TableColumn<Map, String>,
                TableCell<Map, String>>() {
            @Override
            public TableCell call(TableColumn p) {
                return new TextFieldTableCell(new StringConverter() {

                    @Override
                    public String toString(Object t) {
                        return t.toString();
                    }

                    @Override
                    public Object fromString(String string) {
//                        System.out.println("string = " + string);
//                        p.getColumns().get(0);
//                        System.out.println(p.getCellData(0));
                        return string;
                    }
                }){
                    @Override
                    public void commitEdit(Object newValue) {
                        Map map = (Map)this.getTableView().getItems().get(this.getIndex());
                        Preferences.userNodeForPackage(UserSession.class).put((String)map.get
                                (Column1MapKey), (String)newValue);
                        super.commitEdit(newValue);
                    }
                };
            }
        };
        firstDataColumn.setCellFactory(cellFactoryForMap);
        secondDataColumn.setCellFactory(cellFactoryForMap);

        final VBox vbox = new VBox();

        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table_view);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);

        stage.show();
    }

    private ObservableList<Map> generateDataInMap() throws BackingStoreException {
        Preferences prop = Preferences.userNodeForPackage(UserSession.class);
        int max = 10;
        String[] keys = prop.keys();

        ObservableList<Map> allData = FXCollections.observableArrayList();
        for (int i = 0; i < keys.length; i++) {
            Map<String, String> dataRow = new HashMap<>();

            dataRow.put(Column1MapKey, keys[i]);
            dataRow.put(Column2MapKey, prop.get(keys[i], ""));
            allData.add(dataRow);
            System.out.println(keys[i] + " = " + prop.get(keys[i], ""));
        }
        return allData;
    }
}
