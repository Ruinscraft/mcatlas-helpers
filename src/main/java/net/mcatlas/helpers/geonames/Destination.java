package net.mcatlas.helpers.geonames;

import java.util.HashSet;
import java.util.Set;

public class Destination implements Comparable<Destination> {

    private String asciiname;
    private double latitude;
    private double longitude;

    private Set<String> alternateNames = new HashSet<String>();

    private long population = 0;

    private String fcode;
    private String country;
    private String adminZone;
    private String timezone;

    private Destination(Builder builder) {
        this.asciiname = builder.asciiname;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.alternateNames = builder.alternateNames;
        this.population = builder.population;
        this.fcode = builder.fcode;
        this.country = builder.country;
        this.adminZone = builder.adminZone;
        this.timezone = builder.timezone;
    }

    public String getName() {
        return asciiname;
    }

    public double getLat() {
        return latitude;
    }

    public double getLong() {
        return longitude;
    }

    public Set<String> getAltNames() {
        return alternateNames;
    }

    public long getPopulation() {
        return population;
    }

    public String getFCode() {
        return fcode;
    }

    public String getCountry() {
        return country;
    }

    public String getAdminZone() {
        return adminZone;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getFormattedName() {
        return getName() + ", " + getAdminZone() + ", " + getCountry();
    }

    public long popularityIndex() {
        long popular = this.population + (4000 * this.getAltNames().size());

        if (this.getFCode().equals("ADM1") || this.getFCode().equals("ADM2") || // admin district (counties, states, etc)
                this.getFCode().equals("ADM3") || this.getFCode().equals("ADM4") ||
                this.getFCode().equals("ADM5") || this.getFCode().equals("ADMD") ||
                this.getFCode().equals("CONT")) {
            popular = popular - this.population;
            popular = popular / 10;
        } else if (this.getCountry() == null) {
            popular = popular / 2;
        } else if (this.getCountry().equals("USA") || this.getCountry().equals("Canada")) {
            popular = popular * 2;
        } else if (this.getFCode().equals("MT")) { // mountain
            popular = popular * 2;
        } else if (this.getFCode().equals("PCLI")) { // actual country
            popular = popular - this.population;
            popular = popular / 5;
        } else if (this.getFCode().equals("UNIV")) { // university
            popular = popular / 2;
        } else if (this.getFCode().equals("RGNE") || this.getFCode().equals("RGN")) { // region
            popular = popular - this.population;
            popular = popular / 3;
        } else if (this.getFCode().equals("PPLX")) { // populated section
            popular = popular / 4;
        } else if (this.getFCode().equals("TMPL")) {
            popular = popular * 2;
        } else if (this.getFCode().equals("STM")) {
            popular = popular / 4;
        }
        return popular;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Destination)) return false;
        Destination other = (Destination) object;
        if (!other.getFormattedName().equals(this.getFormattedName())) return false;
        if (other.popularityIndex() != this.popularityIndex()) return false;
        if (other.getLat() != this.getLat()) return false;
        if (other.getLong() != this.getLong()) return false;
        if (other.getName() != this.getName()) return false;

        return true;
    }

    @Override
    public int compareTo(Destination other) {
        // eventually incorporate alt names too
        long popular = this.popularityIndex();
        long otherPopular = other.popularityIndex();
        if (otherPopular > popular) {
            return 1;
        } else if (otherPopular == popular) {
            return 0;
        } else if (otherPopular < popular) {
            return -1;
        }
        return 0;
    }

    // a builder just for fun
    public static class Builder {
        private String asciiname;
        private double latitude;
        private double longitude;
        private Set<String> alternateNames;
        private long population;
        private String fcode;
        private String country;
        private String adminZone;
        private String timezone;

        // required values
        public Builder(String asciiname, double latitude, double longitude) {
            this.asciiname = asciiname;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Builder alternateNames(String names) {
            this.alternateNames = new HashSet<String>();
            String[] splitNames = names.split(",");

            for (String alt : splitNames) {
                alternateNames.add(alt);
            }

            return this;
        }

        public Builder population(long pop) {
            this.population = pop;
            return this;
        }

        public Builder fcode(String fcode) {
            this.fcode = fcode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder adminZone(String adminZone) {
            this.adminZone = adminZone;
            return this;
        }

        public Builder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Destination build() {
            return new Destination(this);
        }
    }

}
