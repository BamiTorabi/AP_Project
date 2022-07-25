package data;

import process.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataUploader {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Data.initialize();
        Data.readUsers();
        Data.readCourses();

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eduInfo?user=root&password=MySQL!69775"
        );
        Statement statement = connect.createStatement();
        String studentString = "insert into eduInfo.Students values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String profString = "insert into eduInfo.Professors values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String courseString = "insert into eduInfo.Courses values (?, ?, ?, ?, ?, ?, ?, ?)";
        String scoreString = "insert into eduInfo.Scores values (?, ?, ?, ?, ?, ?)";
        String classTimeString = "insert into eduInfo.ClassTimes values (?, ?, ?, ?)";

        for (CollegeType type : CollegeType.values()){
            College college = College.getInstance(type);
            for (Student student : college.getStudentList()){
                PreparedStatement prepped = connect.prepareStatement(studentString);
                prepped.setString(1, student.getFirstName());
                prepped.setString(2, student.getLastName());
                prepped.setString(3, student.getNationalID());
                prepped.setString(4, student.getPhoneNumber());
                prepped.setString(5, student.getEmailAddress());
                prepped.setString(6, student.getUniversityID());
                prepped.setString(7, student.getPassword());
                prepped.setInt(8, student.getFirstYear());
                prepped.setString(9, String.valueOf(student.getCollege()));
                prepped.setString(10, String.valueOf(student.getType()));
                prepped.setDouble(11, student.getTotalScore());
                prepped.setInt(12, student.getEducationalStatus());
                prepped.setBoolean(13, student.isStudent());
                try {
                    prepped.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException e){

                }
            }
            for (Professor professor : college.getProfessorList()){
                PreparedStatement prepped = connect.prepareStatement(profString);
                prepped.setString(1, professor.getFirstName());
                prepped.setString(2, professor.getLastName());
                prepped.setString(3, professor.getNationalID());
                prepped.setString(4, professor.getPhoneNumber());
                prepped.setString(5, professor.getEmailAddress());
                prepped.setString(6, professor.getUniversityID());
                prepped.setString(7, professor.getPassword());
                prepped.setInt(8, professor.getFirstYear());
                prepped.setString(9, String.valueOf(professor.getCollege()));
                prepped.setString(10, String.valueOf(professor.getType()));
                prepped.setInt(11, professor.getRoomNumber());
                prepped.setBoolean(12, professor.isDeputy());
                prepped.setBoolean(13, professor.isHead());
                prepped.setBoolean(14, professor.isStudent());
                try {
                    prepped.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException e){

                }
            }

            for (Course course : college.getCourseList()){
                PreparedStatement prepped = connect.prepareStatement(courseString);
                prepped.setString(1, course.getCourseName());
                prepped.setString(2, String.valueOf(course.getCollegeLinked()));
                prepped.setString(3, String.valueOf(course.getLevel()));
                prepped.setString(4, course.getCourseID());
                String[] S = course.getExamTime().split(" ");
                ArrayList<String> R = new ArrayList<>(List.of(S[0].split("/")));
                Collections.reverse(R);
                String[] T = R.toArray(new String[0]);
                prepped.setString(5, String.join("-", T) + " " + S[1] + ":00");
                prepped.setDouble(6, course.getMeanScore());
                prepped.setInt(7, course.getUnits());
                prepped.setString(8, course.getProfessorID());
                try {
                    prepped.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException e){

                }
            }

            for (Course course : college.getCourseList()){
                for (Score score : course.getScoreList()){
                    PreparedStatement prepped = connect.prepareStatement(scoreString);
                    prepped.setString(1, score.getCourseLinked());
                    prepped.setString(2, score.getStudentLinked());
                    prepped.setDouble(3, score.getValue());
                    prepped.setString(4, String.valueOf(score.getStatus()));
                    prepped.setString(5, score.getStudentProtest());
                    prepped.setString(6, score.getProfessorAnswer());
                    try{
                        prepped.executeUpdate();
                    } catch (SQLIntegrityConstraintViolationException e){

                    }
                }
            }

            for (Course course : college.getCourseList()){
                for (ClassTime time : course.giveClassTimes()){
                    PreparedStatement prepped = connect.prepareStatement(classTimeString);
                    prepped.setString(1, course.getCourseID());
                    prepped.setInt(2, time.getDay());
                    String startTime = String.format("%02d", time.getStartHours()) + ":" + String.format("%02d", time.getStartMins()) + ":00";
                    String endTime = String.format("%02d", time.getEndHours()) + ":" + String.format("%02d", time.getEndMins()) + ":00";
                    prepped.setString(3, startTime);
                    prepped.setString(4, endTime);
                    try{
                        prepped.executeUpdate();
                    } catch (SQLIntegrityConstraintViolationException e){}
                }
            }
        }

        statement.close();
        connect.close();
    }
}
