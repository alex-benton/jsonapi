package net.frogrock.jsonapi.config;

/**
 * <p>Automatically generate "link" elements when serializing JSON API elements.</p>
 * 
 * @author abenton
 */
public class LinkGenerator {

	public static LinkGenerator defaultConfiguration() {
		LinkGenerator generator = new LinkGenerator();
		return generator;
	}
	
	private LinkGenerator() {
		
	}
}
