package tn.esprit.user.utils;

/* import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class GeoIPService {

    private DatabaseReader reader;

    public GeoIPService(DatabaseReader geoIpDatabaseReader) {
        this.reader = geoIpDatabaseReader;
    }

    public CityResponse cityResponse(InetAddress inetAddress) {
        try {
            return reader.city(inetAddress);
        } catch (IOException e) {
        } catch (GeoIp2Exception e) {
        }

        return null;
    }

    public CityResponse cityResponse(String ipAddress) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ipAddress);
            return cityResponse(inetAddress);
        } catch (UnknownHostException e) {
        }
        return null;
    }


    public String cityName(String ipAddress) {
        CityResponse cityResponse = cityResponse(ipAddress);
        if (cityResponse != null) {
            return cityResponse.getCity().getName();
        }
        return null;
    }

    public String cityCountry(InetAddress inetAddress) {
        CityResponse cityResponse = cityResponse(inetAddress);
        if (cityResponse != null) {
            return cityResponse.getCountry().getName();
        }
        return null;
    }
} */