package tp.rest;
import tp.model.City;
import tp.model.CityManager;
import tp.model.CityNotFound;
import tp.model.Position;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import java.util.LinkedList;
import java.util.List;

@WebServiceProvider

@ServiceMode(value=Service.Mode.MESSAGE)
public class MyServiceTP implements Provider<Source> {
	
	/**
	 * Gère les villes
	 */
	private CityManager cityManager = new CityManager();
	
	private JAXBContext jc;
	
	@javax.annotation.Resource(type=Object.class)
	protected WebServiceContext wsContext;

	/**
	 * Constructeur chargé d'initialiser le parseur XML/Objet Java avec
	 * Les classes CityManager, City, Position
	 */
	public MyServiceTP(){
		try {
            jc = JAXBContext.newInstance(CityManager.class,City.class,Position.class);
			System.out.println(jc);
		} catch(JAXBException je) {
            System.out.println("Exception " + je);
            throw new WebServiceException("Cannot create JAXBContext", je);
        }
        cityManager.addCity(new City("Rouen",49.437994,1.132965,"FR"));
        cityManager.addCity(new City("Neuor",12,42,"RF"));
	}

	/**
	 * Méthode appellée à chaque requête; dispatche en fonction
	 * de la méthode utilisée vers le traitement associé.
	 * @param source
	 * @return Réponse du serveur.
     */
    public Source invoke(Source source) {

		try {
            MessageContext mc = wsContext.getMessageContext();
            String path = (String)mc.get(MessageContext.PATH_INFO);
            String method = (String)mc.get(MessageContext.HTTP_REQUEST_METHOD);
            System.out.println("Got HTTP "+method+" request for "+path);
		    if (method.equals("GET"))
	                return get(mc, path);
			if (method.equals("POST"))
				    return post(source, mc, path);
           	if (method.equals("PUT"))
					return put(source, mc);
           	if (method.equals("DELETE")) {
					return delete(source, mc, path); }
			throw new WebServiceException("Unsupported method:" +method);  
        } catch(JAXBException je) {
            throw new WebServiceException(je);
        }

    }

	/**
	 * Méthode chargée d'ajouter la ville passée en paramètre à la liste
	 * de villes déjà connues.
	 * @param source
	 * @param mc Contenu de la requête (correspond aux variables -> ici une City)
	 * @return Source réponse du serveur
	 * @throws JAXBException
     */
	private Source put(Source source, MessageContext mc) throws JAXBException {

        Unmarshaller u = jc.createUnmarshaller();
        City city=(City)u.unmarshal(source);

        cityManager.addCity(city);

		return new JAXBSource(jc, city);
	}

	/**
	 * Méthode chargée de supprimer toutes les villes (all) ou seulement
	 * celle qui a été spéciifiée.
	 * @param source
	 * @param mc Contenu de la requête (correspond aux variables -> ici une City)
	 * @param path Chemin utilisé pour la requête
	 * @return Source réponse du serveur
	 * @throws JAXBException
     */
	private Source delete(Source source, MessageContext mc, String path) throws JAXBException {

        if (path.equals("all")) {

            // * effacer toute la liste de ville
            List<City> lCities = new LinkedList<>();
            lCities.addAll(cityManager.getCities());
            for(City city : lCities) cityManager.removeCity(city);

        } else {

            // * effacer la ville passée en paramètre
            Unmarshaller u = jc.createUnmarshaller();
            City city = (City) u.unmarshal(source);
            cityManager.removeCity(city);
        }

        return new JAXBSource(jc, cityManager);
	}

	/**
	 * Méthode chargée de retourner la/les villes situées sur/proche d'une Position.
	 * @param source
	 * @param mc Contenu de la requête (correspond aux variables -> ici une City)
	 * @param path Chemin utilisé pour la requête
	 * @return La ou les City trouvées.
	 * @throws JAXBException si aucune ville n'esst retournée.
     */
	private Source post(Source source, MessageContext mc, String path) throws JAXBException {

		Unmarshaller u = jc.createUnmarshaller();
		Position position=(Position)u.unmarshal(source);

		Object message;

		if (path != null) {
			try {
				message = cityManager.searchNear(position, Integer.parseInt(path));
			} catch (CityNotFound cityNotFound) {
				message = cityNotFound.getMessage();
			}

		} else {

			try {
				message = cityManager.searchExactPosition(position);
			} catch (CityNotFound cnf) {
				message = cnf.getMessage();
			}
		}

		return new JAXBSource(jc, message);
	}

	/**
	 * Méthode chargée de retourner toutes les City ou seulement celles dont
	 * le nom a été passé en paramètre.
	 * @param mc
	 * @param path Chemin utilisé pour la requête
	 * @return La/les villes trouvées
	 * @throws JAXBException si aucune City n'est retournée.
     */
	private Source get(MessageContext mc, String path) throws JAXBException {

        Object message;

		if (path.equals("all")) {
		    // * retourner tous les villes seulement si le chemin d'accès est "all"
            message = cityManager;
        } else {
            CityManager cities = new CityManager();
            cities.setCities(cityManager.searchFor(path));
            message = cities;
        }
		
		return new JAXBSource(jc, message);
	}

	/**
	 *
	 * @param args
     */
	public static void main(String args[]) {
	      Endpoint e = Endpoint.create( HTTPBinding.HTTP_BINDING,
	                                     new MyServiceTP());
	      e.publish("http://127.0.0.1:8084/");
	       // pour arrêter : e.stop();
	 }
}
