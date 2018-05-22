package ja.burhanrashid52.photoeditor;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Define your custom effect using {@link Builder} class
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/22/2018
 */
public class CustomEffect {

    private String mEffectFactoryType;
    private Map<String, Object> parametersMap;

    private CustomEffect(Builder builder) {
        mEffectFactoryType = builder.mEffectFactoryType;
        parametersMap = builder.parametersMap;
    }

    public String getEffectFactoryType() {
        return mEffectFactoryType;
    }

    public Map<String, Object> getParameters() {
        return parametersMap;
    }

    public static class Builder {

        private String mEffectFactoryType;
        private Map<String, Object> parametersMap = new HashMap<>();

        public Builder() {
        }

        public Builder setEffectFactoryType(String effectFactoryType) {
            mEffectFactoryType = effectFactoryType;
            return this;
        }

        public Builder setParameter(@NonNull String paramKey, Object paramValue) {
            parametersMap.put(paramKey, paramValue);
            return this;
        }

        public CustomEffect build() {
            return new CustomEffect(this);
        }
    }
}
