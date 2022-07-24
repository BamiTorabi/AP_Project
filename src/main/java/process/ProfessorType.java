package process;

public enum ProfessorType {
    ALL, ASSISTANT, ASSOCIATE, FULL;

    public static String[] list(){
        int n = ProfessorType.values().length;
        String[] S = new String[n];
        for (int i = 0; i < n; i++){
            S[i] = ProfessorType.values()[i].toString();
        }
        return S;
    }
}
