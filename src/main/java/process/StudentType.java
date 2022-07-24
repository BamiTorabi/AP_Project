package process;

public enum StudentType {
    UNDERGRADUATE, MASTER, PHD;

    public static String[] list(){
        int n = StudentType.values().length;
        String[] S = new String[n];
        for (int i = 0; i < n; i++){
            S[i] = StudentType.values()[i].toString();
        }
        return S;
    }

}
