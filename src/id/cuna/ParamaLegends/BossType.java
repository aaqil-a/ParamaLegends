package id.cuna.ParamaLegends;

public enum BossType {
    START,
    NATURE,
    EARTH,
    WATER,
    FIRE,
    VOID;

    public BossType next(){
        return values()[ordinal()+1];
    }
}
