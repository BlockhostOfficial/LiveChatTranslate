package net.blockhost.livechattranslate;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class TranslationService {
    private final String apiKey;

    public TranslationService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String detectLanguage(String text) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.deepl.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeepLApi deepLApi = retrofit.create(DeepLApi.class);
        Call<DeepLResponse> call = deepLApi.detect(apiKey, text);

        try {
            Response<DeepLResponse> response = call.execute();
            if (response.isSuccessful()) {
                return response.body().getDetectedLanguage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String translate(String text, String targetLanguage) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.deepl.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeepLApi deepLApi = retrofit.create(DeepLApi.class);
        Call<DeepLResponse> call = deepLApi.translate(apiKey, text, targetLanguage);

        try {
            Response<DeepLResponse> response = call.execute();
            if (response.isSuccessful()) {
                List<DeepLResponse.Translation> translations = response.body().getTranslations();
                if (!translations.isEmpty()) {
                    return translations.get(0).getText();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

