package client.logic;

public class Special extends User{

    public Special(){}

    public Special(String name, String ID){
        setFirstName(name);
        setUniversityID(ID);
        setUserType("Special");
    }

}
