package net.frogrock.jsonapi.config;

public class MapperOptions {

	public static final int RESOLUTION_STRATEGY_EXPLICIT = 0;
	public static final int RESOLUTION_STRATEGY_ALL = 1;

	private int attributeResolutionStrategy = 0;

	public static MapperOptions defaultConfiguration() {
		return new MapperOptions().setAttributeResolutionStrategy(RESOLUTION_STRATEGY_EXPLICIT);
	}

	private MapperOptions() {

	}

	public MapperOptions setAttributeResolutionStrategy(int resolutionStrategy) {
		attributeResolutionStrategy = resolutionStrategy;
		return this;
	}

	public int getAttributeResolutionStrategy() {
		return attributeResolutionStrategy;
	}
}
