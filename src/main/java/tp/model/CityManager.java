package tp.model;

import javax.jws.WebService;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represent a city manager, it can  
 * <ul>
 * 	<li>add a city</li>
 * 	<li>remove a city</li>
 * 	<li>return the list of cities</li>	
 * 	<li>search a city with a given name</li>
 *  <li>search a city at a position</li>
 * 	<li>return the list of cities near 10km of the given position</li>
 * </ul>
 *
 */
@WebService(endpointInterface = "tp.model.CityManagerService",
			serviceName = "CityManagerService")
public class CityManager implements CityManagerService {

	private List<City> cities;
	
	public CityManager() {
		this.cities = new LinkedList<>();
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}
	
	public boolean addCity(City city){
		if (!cities.contains(city)) return cities.add(city);
        return false;
	}
	
	public boolean removeCity(City city){
		return cities.remove(city);
	}
	
	public List<City> searchFor(String cityName){
		
		List<City> citiesByNames = new LinkedList<>();

        for(City city : this.cities) {
            if (city.getName().equals(cityName)) citiesByNames.add(city);
        }

		return citiesByNames;
	}
	
	public City searchExactPosition(Position position) throws CityNotFound {
		for(City city:cities){
			if (position.equals(city.getPosition())){
				return city;
			}
		}
		throw new CityNotFound("City Not Found");
	}

	public List<City> searchNear(Position position, int radius) throws CityNotFound {

		List<City> lCity = new LinkedList<>();

		for (City city : cities) {

			int    earthPerimeter = 6371000;

			double lat1 = Math.toRadians(position.getLatitude());
			double lat2 = Math.toRadians(city.getPosition().getLatitude());

			double lon1 = Math.toRadians(position.getLongitude());
			double lon2 = Math.toRadians(city.getPosition().getLongitude());

			double deltaLat = Math.toRadians(lat2 - lat1);
			double deltaLon = Math.toRadians(lon2 - lon1);

			double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
					Math.cos(lat1) * Math.cos(lat2) *
							Math.sin(deltaLon/2) * Math.sin(deltaLon/2);

			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			double d = earthPerimeter * c;
			System.out.println("Distance : " + d);

			if (d <= radius) {
				lCity.add(city);
			}

		}

		if (lCity.isEmpty()) throw new CityNotFound("No City Arround");

		return lCity;
	}
}
