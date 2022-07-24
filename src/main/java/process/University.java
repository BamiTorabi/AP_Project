package process;

import java.util.List;

public class University {

    private static University uni;
    private String uniName;

    private University(String name){
        this.uniName = name;
    }

    public static University getInstance(){
        if (uni == null){
            uni = new University("Sharif");
        }
        return uni;
    }

    public static User getUser(String username, String password){
        User rando = null;
        for (CollegeType type : CollegeType.values()){
            rando = College.getInstance(type).getUser(username, password);
            if (rando != null)
                break;
        }
        return rando;
    }

    public static User getUser(String username){
        User rando = null;
        for (CollegeType type : CollegeType.values()){
            rando = College.getInstance(type).getUser(username);
            if (rando != null)
                break;
        }
        return rando;
    }

    public static Course getCourse(String coursename){
        Course stuff = null;
        for (CollegeType type : CollegeType.values()){
            stuff = College.getInstance(type).getCourse(coursename);
            if (stuff != null)
                break;
        }
        return stuff;
    }

    public static void debugUsers(){
        for (CollegeType type : CollegeType.values()){
            College college = College.getInstance(type);
            if (!college.getProfessorList().isEmpty())
                for (Professor prof : college.getProfessorList())
                    System.out.println(prof.giveName() + " " + prof.getUniversityID());
            if (!college.getStudentList().isEmpty())
                for (Student student : college.getStudentList())
                    System.out.println(student.giveName() + " " + student.getUniversityID());
            if (college.getDeputy() != null)
                System.out.println(college.getType() + " DEPUTY: " + college.getDeputy().giveName());
            if (college.getHead() != null)
                System.out.println(college.getType() + " HEAD: " + college.getHead().giveName());
            System.out.println("-----------------------------------");
        }
    }

    public static void setSpecialUsers(){
        for (CollegeType type : CollegeType.values()){
            College college = College.getInstance(type);
            for (Professor prof : college.getProfessorList()){
                if (prof.isDeputy())
                    college.setDeputy(prof);
                if (prof.isHead())
                    college.setHead(prof);
            }
        }
    }
}
