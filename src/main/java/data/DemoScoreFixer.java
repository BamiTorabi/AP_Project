package data;

import process.*;

import java.util.Scanner;

public class DemoScoreFixer {
    public static void main(String[] args) {
        Data.initialize();
        Data.readUsers();
        Data.readCourses();

        Scanner sc = new Scanner(System.in);
        int S = sc.nextInt();
        while (S != 0){
            if (S == 1){
                System.out.println("Student ID: ");
                String student = sc.next();
                System.out.println("Course ID: ");
                String course = sc.next();
                Score score = new Score(student, course, 0);
                User me = University.getUser(student);
                Course my = University.getCourse(course);
                me.addScore(score);
                my.addScore(score);
                Data.writeUser(me);
                Data.writeCourse(my);
            }
            if (S == 2){
                System.out.println("Student ID: ");
                String student = sc.next();
                System.out.println("Prof ID: ");
                String prof = sc.next();
                Student me = (Student) University.getUser(student);
                Professor you = (Professor) University.getUser(prof);
                me.setCounsellor(you.getUniversityID());
                Data.writeUser(me);
            }
            S = sc.nextInt();
        }
    }
}
