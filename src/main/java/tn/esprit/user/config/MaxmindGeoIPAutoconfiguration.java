package tn.esprit.user.config;

import tn.esprit.user.utils.GeoIPService;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = MaxmindGeoIPProperties.PROPERTY_PREFIX, value = {"enabled"}, matchIfMissing = true)
@EnableConfigurationProperties(MaxmindGeoIPProperties.class)
public class MaxmindGeoIPAutoconfiguration {

    @Autowired
    private MaxmindGeoIPProperties properties;

    @Bean
    public DatabaseReader geoIpDatabaseReader() throws Exception {
        return new DatabaseReader.Builder(properties.getGeolite2CityMmdb().getInputStream()).build();
    }

    @Bean
    public GeoIPService geoIpService() throws Exception {
        return new GeoIPService(geoIpDatabaseReader());
    }

}
