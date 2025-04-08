package com.mycompany.hospital_reception.main;

import com.mycompany.hospital_reception.UI.*;
import com.mycompany.hospital_reception.*;
import com.mycompany.hospital_reception.list.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HospitalMainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hospital Reception System");

        // Title Label with styling
        Label titleLabel = new Label("Hospital Reception System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Large, bold font
        titleLabel.setTextFill(Color.DARKBLUE); // Dark blue color for title

        // Main menu buttons 
        //call the createButton class below 
        Button showListButton = createButton("List");
        Button scheduleAppointmentButton = createButton("Schedule Appointment");
        Button scheduleAdmissionButton = createButton("Schedule Admission");
        Button inpatientOutpatientButton = createButton("Inpatient/Outpatient");
        Button bedAllotmentButton = createButton("Bed Allotment");
        Button insuranceButton = createButton("Insurance");
        Button medicalRecordButton = createButton("Medical Report");
        Button registrationButton = createButton("Patient Registration");
        Button manageAdmissionButton = createButton("Patient Admit");
        
        //action to link the button
        bedAllotmentButton.setOnAction(e -> openBedAllotment());
        manageAdmissionButton.setOnAction(e -> openManageAdmission());
        insuranceButton.setOnAction(e -> openInsurance());
        medicalRecordButton.setOnAction(e -> openMedicalReport());
        scheduleAppointmentButton.setOnAction(e -> openAppointment());
        scheduleAdmissionButton.setOnAction(e -> openAdmission());
        registrationButton.setOnAction(e -> openRegistration());
        
        // Sub-menu actions
        showListButton.setOnAction(event -> showListMenu());
        inpatientOutpatientButton.setOnAction(event -> inpatientOutpatientMenu());

        // Main layout styling
        VBox mainMenu = new VBox(15); 
        mainMenu.setPadding(new Insets(20));
        mainMenu.setAlignment(Pos.CENTER); // centered alignment
        //get color code from https://www.colorhexa.com/
        mainMenu.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #90CAF9; -fx-border-width: 2px; -fx-border-radius: 8px;");
        //add all button to scene
        mainMenu.getChildren().addAll(
                titleLabel,
                registrationButton,
                scheduleAppointmentButton,
                scheduleAdmissionButton,
                manageAdmissionButton,
                inpatientOutpatientButton,
                bedAllotmentButton,
                insuranceButton,
                medicalRecordButton,
                showListButton
        );

        Scene scene = new Scene(mainMenu, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    // the UI format of all button
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        button.setStyle("-fx-background-color: #64B5F6; -fx-text-fill: white; -fx-background-radius: 5px;"); // Mid blue background with rounded corners
        button.setMinWidth(200); // Set minimum width for buttons
        //source https://docs.oracle.com/javase/8/javafx/api/javafx/scene/input/MouseEvent.html
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white;")); // Darker blue on hover
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #64B5F6; -fx-text-fill: white;"));
        return button;
    }

    // Show List submenu
    private void showListMenu() {
        Stage showListStage = new Stage();
        showListStage.setTitle("Show List");

        Button appointmentListButton = createButton("Upcoming Appointment");
        Button admissionListButton = createButton("Upcomming Admission");
        Button unassignedInpatientListButton = createButton("Room Pending - Inpatients");
        Button assignedInpatientListButton = createButton("Occupied Rooms - Inpatients");
        Button outpatientListButton = createButton("Outpatient List");
        Button patientListButton = createButton("Patient Detail");

        VBox showListMenu = new VBox(10);
        showListMenu.setPadding(new Insets(15));
        showListMenu.setAlignment(Pos.CENTER);
        showListMenu.setStyle("-fx-background-color: #E1F5FE; -fx-border-color: #BBDEFB; -fx-border-width: 2px; -fx-border-radius: 8px;");
        showListMenu.getChildren().addAll(
                patientListButton,
                appointmentListButton,
                admissionListButton,
                outpatientListButton,
                unassignedInpatientListButton,
                assignedInpatientListButton
        );
        //action to link the class
        appointmentListButton.setOnAction(e -> openAppointmentList());
        outpatientListButton.setOnAction(e -> openOutpatientList());
        unassignedInpatientListButton.setOnAction(e -> openUnassignBedList());
        assignedInpatientListButton.setOnAction(e -> openAssignedBedList());
        admissionListButton.setOnAction(e -> openAdmissionList());
        patientListButton.setOnAction(e -> openPatientList());
        
        
        Scene showListScene = new Scene(showListMenu, 350, 300);
        showListStage.setScene(showListScene);
        showListStage.show();
    }

    // Inpatient/Outpatient submenu 
    private void inpatientOutpatientMenu() {
        Stage inpatientOutpatientStage = new Stage();
        inpatientOutpatientStage.setTitle("Inpatient/Outpatient");

        Button inpatientButton = createButton("Inpatient");
        Button outpatientButton = createButton("Outpatient");

        VBox inpatientOutpatientMenu = new VBox(10);
        inpatientOutpatientMenu.setPadding(new Insets(15));
        inpatientOutpatientMenu.setAlignment(Pos.CENTER);
        inpatientOutpatientMenu.setStyle("-fx-background-color: #E1F5FE; -fx-border-color: #BBDEFB; -fx-border-width: 2px; -fx-border-radius: 8px;");
        inpatientOutpatientMenu.getChildren().addAll(inpatientButton, outpatientButton);
        //action to open other class
        outpatientButton.setOnAction(e -> openOutpatient());
        inpatientButton.setOnAction(e -> openInpatient());
        
        Scene inpatientOutpatientScene = new Scene(inpatientOutpatientMenu, 300, 200);
        inpatientOutpatientStage.setScene(inpatientOutpatientScene);
        inpatientOutpatientStage.show();
    }
    
    //open the class 
    private void openAppointmentList(){
        new ScheduleAppointmentList().show();
    }
    private void openBedAllotment() {
        new BedAllotmentFX().show(); 
    }
    private void openInpatient() {
        new Inpatient().show();
    }
    private void openOutpatient(){
        new Outpatient().show();
    }
    private void openManageAdmission(){
        new PatientHospitalAdmission().show();
    }
    private void openOutpatientList(){
        new OutpatientList().show();
    }
    private void openUnassignBedList(){
        new UnassignInpatientList().show();
    }
    private void openAssignedBedList(){
        new AssignedInpatientList().show();
    }
    private void openAdmissionList(){
        new AdmissionList().show();
    }
    private void openInsurance(){
        new InsuranceClaimsFX().show();
    }
    private void openMedicalReport(){
        new MedicalReportsFX().show();
    }
    private void openPatientList(){
        new PatientList().show();
    }
    private void openAppointment() {
        Stage appointmentStage = new Stage();
        appointmentStage.setTitle("Appointment Scheduling");

        AppointmentGUI appointmentGUI = new AppointmentGUI();
        VBox layout = appointmentGUI.getLayout(); // Get the layout from AppointmentGUI

        Scene scene = new Scene(layout, 800, 350);
        appointmentStage.setScene(scene);
        appointmentStage.show();
    }
    private void openRegistration(){
        Stage registrationStage = new Stage();
        registrationStage.setTitle("Patient Registration");
        
        RegistrationGUI rgtGUI = new RegistrationGUI();
        VBox layout = rgtGUI.getLayout();
        
        Scene scene = new Scene(layout, 700,400);
        registrationStage.setScene(scene);
        registrationStage.show();
    }
    private void openAdmission(){
        Stage AdmissionStage = new Stage();
        AdmissionStage.setTitle("Admission Scheduling");
        
        AdmissionGUI admGUI = new AdmissionGUI();
        VBox layout = admGUI.getLayout();
        
        Scene scene = new Scene(layout, 770,350);
        AdmissionStage.setScene(scene);
        AdmissionStage.show();
    }
    //launch the app
    public static void main(String[] args) {
        launch(args);
    }
}

