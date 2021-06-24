package net.mcatlas.helpers.geonames;

public enum LocationAccuracy {
    VERY_HIGH(5),
    HIGH(35),
    MEDIUM(250),
    LOW(2000);

    int range;

    LocationAccuracy(int range) {
        this.range = range;
    }
}