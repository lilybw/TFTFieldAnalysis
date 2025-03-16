package gbw.riot.tftfieldanalysis.core;

public enum ServerLocations {

    BR1("br1", Regions.AMERICAS.name), EUN1("eun1", Regions.EUROPE.name),
    EUW1("euw1", Regions.EUROPE.name),
    JP1("jp1", Regions.ASIA.name), KR("kr", Regions.ASIA.name),
    LA1("la1", Regions.AMERICAS.name), LA2("la2", Regions.AMERICAS.name),
    NA1("na1", Regions.AMERICAS.name), OC1("oc1", Regions.ASIA.name),
    PH2("ph2", Regions.ASIA.name), RU("ru", Regions.ASIA.name),
    SG2("sg2", Regions.AMERICAS.name), TH2("th2", Regions.AMERICAS.name),
    TR1("tr1", Regions.ASIA.name), TW2("tw2", Regions.ASIA.name),
    VN2("vn2", Regions.ASIA.name), ERR_UNKNOWN("err_unknown", Regions.UNKNOWN.name);

    public enum Regions {
        AMERICAS("americas"), EUROPE("europe"), ASIA("asia"), ESPORTS("esports"), UNKNOWN("unknown");
        public final String name;
        Regions(final String name) {
            this.name = name;
        }
    }

    public final String domain, continent;
    ServerLocations(String domain, String continent){
        this.domain = domain; this.continent = continent;
    }
    public static ServerLocations byDomain(String string){
        for(ServerLocations location : ServerLocations.values()){
            if(string.equalsIgnoreCase(location.domain)){
                return location;
            }
        }
        return ERR_UNKNOWN;
    }
    public static ServerLocations byRegion(String string){
        for(ServerLocations location : ServerLocations.values()){
            if(string.equalsIgnoreCase(location.continent)){
                return location;
            }
        }
        return ERR_UNKNOWN;
    }
}
