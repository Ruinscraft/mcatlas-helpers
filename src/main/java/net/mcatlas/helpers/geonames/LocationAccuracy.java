package net.mcatlas.helpers.geonames;

/**
 * I added this with intents of displaying when typing /whereami
 * But it isn't used for that so it's just for cleaner code now
 */
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