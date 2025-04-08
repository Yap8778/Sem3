
package com.mycompany.hospital_reception.UI;


import com.mycompany.hospital_reception.BedAllotment;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
//javafx class for BedAllotment class
public class BedAllotmentFX extends Stage {
    // instance of BedAllotment class
    private BedAllotment bedAllotment;
    // list down the patient form inpatient list 
    //source: https://www.educba.com/javafx-observablelist/
    private ObservableList<String> availablePatients;
    private ObservableList<String> assignedPatients;
    //ListView can observe this list for updates in real-time
    //source: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ListView.html
    private ListView<String> patientListView;
    private boolean isUpdateMode = false;  // Flag to check if Update mode is active
    //UI design for button
    public BedAllotmentFX() {
        //set instance
        bedAllotment = new BedAllotment();
        availablePatients = FXCollections.observableArrayList();
        assignedPatients = FXCollections.observableArrayList();
        patientListView = new ListView<>(availablePatients);

        // refresh button
        Button refreshButton = new Button("Refresh Patient List");
        //the updatemode off because when refresh it will switch back to unassign bed list
        refreshButton.setOnAction(e -> {
            isUpdateMode = false;  // Set to view unassigned patients
            refreshPatientList();
        });

        //allot the bed button
        Button allotBedButton = new Button("Allot Bed to Selected Patient");
        //action
        allotBedButton.setOnAction(e -> allotBedToPatient());

        // update button and the updatemode will be turn on then can click on the patient and promt out update the bedid
        //source https://www.tutorialspoint.com/how-to-implement-javafx-event-handling-using-lambda-in-java
        // e is for define an event handler for a button
        Button updateBedButton = new Button("Update Bed Assignment");
        updateBedButton.setOnAction(e -> {
            isUpdateMode = true;  // Set to view assigned patients for update
            showAssignedPatients();
        });

        VBox root = new VBox(10, patientListView, refreshButton, allotBedButton, updateBedButton);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 400, 350);
        this.setTitle("Bed Allotment System");
        this.setScene(scene);

        // load the initial patient list
        refreshPatientList();

        // update the bed when the update mode
        patientListView.setOnMouseClicked(event -> {
            String selectedPatient = patientListView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null && isUpdateMode) {
                // Only allow updating bed assignment when in update mode
                String patientId = selectedPatient.split(" ")[0];
                promptForNewBedId(patientId);
            }
        });
    }

    // get the unassign patient list 
    private void refreshPatientList() {
        try {
            //show the list
            List<String> patients = bedAllotment.getAvailablePatientList();
            availablePatients.setAll(patients);
            patientListView.setItems(availablePatients);
            //error 
        } catch (SQLException ex) {
            showAlert("Error", "Failed to retrieve patient list: " + ex.getMessage(), AlertType.ERROR);
        }
    }
    //allot button only run when have select the patient
    private void allotBedToPatient() {
        String selectedPatient = patientListView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("No Selection", "Please select a patient from the list.", AlertType.WARNING);
            return;
        }

        String patientId = selectedPatient.split(" ")[0];

        try {
            String result = bedAllotment.bedAllot(patientId);
            showAlert("Bed Allotment", result, AlertType.INFORMATION);
            refreshPatientList();
        } catch (SQLException ex) {
            showAlert("Error", "Failed to allot bed: " + ex.getMessage(), AlertType.ERROR);
        }
    }

    // show assigned bed list
    private void showAssignedPatients() {
        try {
            List<String> patients = bedAllotment.getAssignedPatients();
            assignedPatients.setAll(patients);
            patientListView.setItems(assignedPatients);
        } catch (SQLException ex) {
            showAlert("Error", "Failed to retrieve assigned patient list: " + ex.getMessage(), AlertType.ERROR);
        }
    }

    // update new bed id
    private void promptForNewBedId(String patientId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reassign Bed");
        dialog.setHeaderText("Enter new Bed ID for patient ID " + patientId);
        dialog.setContentText("New Bed ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(bedId -> {
            try {
                //query for update the list
                String updateResult = bedAllotment.updateBedAssignment(patientId, Integer.parseInt(bedId));
                showAlert("Update Bed Assignment", updateResult, AlertType.INFORMATION);
                refreshPatientList();
            } catch (SQLException ex) {
                showAlert("Error", "Failed to update bed assignment: " + ex.getMessage(), AlertType.ERROR);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid bed ID entered.", AlertType.ERROR);
            }
        });
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
