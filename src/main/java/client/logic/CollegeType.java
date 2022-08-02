package client.logic;

public enum CollegeType {
    ALL,
    MATHEMATICAL,
    AEROSPACE,
    PETROLEUM,
    CHEMISTRY,
    CIVIL,
    COMPUTER,
    ELECTRICAL,
    ENERGY,
    INDUSTRIAL,
    ECONOMICS,
    MATERIAL,
    MECHANICAL,
    PHYSICS,
    PHILOSOPHY,
    LINGUISTICS;

    public int getNumber(){
        return this.ordinal();
    }

    public static String[] list(){
        int n = CollegeType.values().length;
        String[] S = new String[n];
        for (int i = 0; i < n; i++){
            S[i] = CollegeType.values()[i].toString();
        }
        return S;
    }

}
