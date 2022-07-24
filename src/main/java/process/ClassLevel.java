package process;

public enum ClassLevel {
    ALL, UNDERGRADUATE, GRADUATE, SHARED;

    public static String[] list(){
        int n = ClassLevel.values().length;
        String[] S = new String[n];
        for (int i = 0; i < n; i++){
            S[i] = ClassLevel.values()[i].toString();
        }
        return S;
    }
}
