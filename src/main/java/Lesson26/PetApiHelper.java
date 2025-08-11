package Lesson26;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class PetApiHelper {
    private static final String BASE_URL = "https://petstore.swagger.io/v2/pet";

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public PetApiHelper() {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public Pet createPet(Pet pet) throws IOException {
        pet.setId(0); // Обнуляем id, чтобы API сгенерировал новый
        String json = mapper.writeValueAsString(pet);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Не удалось создать питомца: " + response.code() + " " + response.message());
            }
            String responseBody = response.body().string();
            Pet createdPet = mapper.readValue(responseBody, Pet.class);
            if (createdPet.getId() == 0) {
                throw new IOException("API не вернул действительный идентификатор питомца");
            }
            return createdPet;
        }
    }

    public Pet getPet(long id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + id)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 404) {
                return null; // питомец не найден
            }
            if (!response.isSuccessful()) {
                throw new IOException("Не удалось получить питомца: " + response.code() + " " + response.message());
            }
            String responseBody = response.body().string();
            return mapper.readValue(responseBody, Pet.class);
        }
    }

    public Pet updatePet(Pet pet) throws IOException {
        String json = mapper.writeValueAsString(pet);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL)
                .put(body)
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Не удалось обновить питомца: " + response.code() + " " + response.message());
            }
            String responseBody = response.body().string();
            return mapper.readValue(responseBody, Pet.class);
        }
    }

    public void deletePet(long id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + id)
                .delete()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Не удалось удалить питомца: " + response.code() + " " + response.message());
            }
        }
    }
}