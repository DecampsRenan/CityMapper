package tp.model;

import javax.jws.WebService;
import java.util.List;

/**
 * Created by renan on 11/03/16.
 */
@WebService
public interface CityManagerService {

    boolean addCity(City city);
    boolean removeCity(City city);
    List<City> getCities();
    void setCities(List<City> cities);
    List<City> searchFor(String cityName);
    City searchExactPosition(Position position) throws CityNotFound;
    List<City> searchNear(Position position, int radius) throws CityNotFound;

}
