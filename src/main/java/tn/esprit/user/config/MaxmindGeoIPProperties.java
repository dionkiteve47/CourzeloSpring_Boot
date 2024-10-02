package tn.esprit.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = MaxmindGeoIPProperties.PROPERTY_PREFIX)
public class MaxmindGeoIPProperties {

    public static final String PROPERTY_PREFIX = "maxmind.geoip";

    private Boolean enabled = true;

    private Resource geolite2CityMmdb;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Resource getGeolite2CityMmdb() {
        return geolite2CityMmdb;
    }

    public void setGeolite2CityMmdb(Resource geolite2CityMmdb) {
        this.geolite2CityMmdb = geolite2CityMmdb;
    }
}
