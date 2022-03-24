package configs;

/***
 * Class to parse application config
 * @author anchitbhatia
 */
public class ApplicationConfig {
    private final String type;
    private final Object config;

    public ApplicationConfig(String type, Object config) {
        this.type = type;
        this.config = config;
    }

    public Object getConfig() {
        return config;
    }

    public String getType() {
        return type;
    }
}
