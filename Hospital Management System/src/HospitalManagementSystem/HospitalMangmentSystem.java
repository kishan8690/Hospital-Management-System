package HospitalManagementSystem;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Scanner;

public class HospitalMangmentSystem {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private  static  final  String username="root";
    private  static  final  String password="Kishan@211";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctors doctor = new Doctors(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice");
                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
                        //add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view patinet
                        patient.ViewPatient();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctor.ViewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid choice!!!!!!");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctors doctor,Connection connection,Scanner scanner){
        System.out.println("Enter Patient ID");
        int patientID = scanner.nextInt();
        System.out.println("Enter Doctor ID");
        int doctorID = scanner.nextInt();
        System.out.println("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientID) && doctor.getDoctorById(doctorID)){
            if(checkDoctorAvailabity(doctorID,appointmentDate,connection)){
                String appointmetQuery = "Insert into appointments (patients_id,doctor_id,appointment_date) values(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmetQuery);
                    preparedStatement.setInt(1,patientID);
                    preparedStatement.setInt(2,doctorID);
                    preparedStatement.setString(3,appointmentDate);
                    int rowAffected = preparedStatement.executeUpdate();
                    if(rowAffected>0){
                        System.out.println("Appointment Booked Successfully!!!");
                    }else{
                        System.out.println("Appointment Booking Failed!!!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor Not Available");
            }
        }else{
            System.out.println("Either doctor or patient does not exist");
        }
    }
    public static   boolean checkDoctorAvailabity(int doctorID,String appointmentDate,Connection connection){
        String sql = "select COUNT(*) from appointments where doctor_id=? and appointment_date=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,doctorID);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return  false;
    }
}
