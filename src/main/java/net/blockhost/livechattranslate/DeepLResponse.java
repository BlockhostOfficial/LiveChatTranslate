package net.blockhost.livechattranslate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeepLResponse {
    @SerializedName("translations")
    private List<Translation> translations;

    @SerializedName("detected_language")
    private String detectedLanguage;

    public List<Translation> getTranslations() {
        return translations;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public static class Translation {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;
        }
    }
}