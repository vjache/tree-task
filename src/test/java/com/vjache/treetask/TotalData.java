package com.vjache.treetask;

/**
 *
 */
class TotalData {
    private volatile Boolean isPasportValid;
    private volatile Boolean isWanted;
    private volatile Boolean isTerrorist;

    public Boolean getPasportValid() {
        return isPasportValid;
    }

    public void setPasportValid(Boolean pasportValid) {
        isPasportValid = pasportValid;
    }

    public Boolean getWanted() {
        return isWanted;
    }

    public void setWanted(Boolean wanted) {
        isWanted = wanted;
    }

    public Boolean getTerrorist() {
        return isTerrorist;
    }

    public void setTerrorist(Boolean terrorist) {
        isTerrorist = terrorist;
    }
}
