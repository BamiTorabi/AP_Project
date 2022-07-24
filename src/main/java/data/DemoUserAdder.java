package data;

import process.*;

import java.util.Scanner;

public class DemoUserAdder {
    public static void main(String[] args) {
        Data.initialize();
        Scanner sc = new Scanner(System.in);
        int S = sc.nextInt();
        while (S != 0) {
            if (S == 1) {
                System.out.println("Is student?");
                String answer = sc.next();
                boolean isStudent = (answer.equals("yes"));
                System.out.println("First name");
                String firstName = sc.next();
                System.out.println("Last name");
                String lastName = sc.next();
                System.out.println("National ID");
                String ID = sc.next();
                System.out.println("University ID");
                String uniID = sc.next();
                System.out.println("Phone number");
                String phoneNumber = sc.next();
                System.out.println("Email address");
                String emailAddress = sc.next();
                System.out.println("College");
                int num = sc.nextInt();
                CollegeType college = CollegeType.values()[num];
                if (isStudent) {
                    System.out.println("Student Type");
                    num = sc.nextInt();
                    StudentType type = StudentType.values()[num];
                    Student newUser = new Student(firstName, lastName, ID, phoneNumber, emailAddress, college, null, type);
                    newUser.setUniversityID(uniID);
                    Data.writeUser(newUser);
                } else {
                    System.out.println("Professor Type");
                    num = sc.nextInt();
                    ProfessorType type = ProfessorType.values()[num];
                    System.out.println("Room number");
                    int roomNumber = sc.nextInt();
                    Professor newUser = new Professor(firstName, lastName, ID, phoneNumber, emailAddress, college, roomNumber, type, false, false);
                    newUser.setUniversityID(uniID);
                    Data.writeUser(newUser);
                }
            }
            S = sc.nextInt();
        }
    }
}
