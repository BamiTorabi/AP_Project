package data;

import process.*;

import java.util.Scanner;

public class DemoCourseAdder {
    public static void main(String[] args) {
        Data.initialize();
        Scanner sc = new Scanner(System.in);
        int S = sc.nextInt();
        while (S != 0){
            if (S == 1){
                System.out.println("Course name");
                String name = sc.next();
                System.out.println("Which college?");
                int num = sc.nextInt();
                CollegeType college = CollegeType.values()[num];
                System.out.println("Professor ID");
                String profID = sc.next();
                System.out.println("Units");
                int units = sc.nextInt();
                System.out.println("How many time periods?");
                int periods = sc.nextInt();
                ClassTime[] times = new ClassTime[periods];
                for (int i = 0; i < periods; i++) {
                    System.out.println("Class day");
                    String day = sc.next();
                    System.out.println("Class start time");
                    String startTime = sc.next();
                    System.out.println("Class end time");
                    String endTime = sc.next();
                    times[i] = new ClassTime(day, startTime, endTime);
                }
                System.out.println("Exam date");
                String examDate = sc.next();
                System.out.println("Exam time");
                String examTime = sc.next();
                System.out.println("Class level");
                int temp = sc.nextInt();
                ClassLevel level = ClassLevel.values()[temp];
                Course course = new Course(name, profID, units, college, times, examDate, examTime, level);
                Data.writeCourse(course);
            }
            S = sc.nextInt();
        }
    }
}
