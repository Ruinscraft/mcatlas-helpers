package net.mcatlas.helpers;

public class Coordinate {

    private static final int SCALING = 120;

    private double x; // long
    private double y; // lat

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static Coordinate getLifeFromMC(int mcX, int mcY) {
        double x = (double) mcX / SCALING;
        double y = (double) mcY / SCALING * -1;
        return new Coordinate(x, y);
    }

    public static Coordinate getMCFromLife(double lat, double lon) {
        int x = (int) (lon * SCALING);
        int y = (int) (lat * SCALING) * -1;
        return new Coordinate(x, y);
    }

}
