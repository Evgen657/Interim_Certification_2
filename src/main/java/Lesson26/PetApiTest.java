package Lesson26;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetApiTest {

    private static PetApiHelper helper;
    private static Pet testPet;
    private static long createdPetId;

    @BeforeAll
    public static void setup() {
        helper = new PetApiHelper();

        Pet.Category category = new Pet.Category();
        category.setId(1);
        category.setName("Dogs");

        Pet.Tag tag = new Pet.Tag();
        tag.setId(101);
        tag.setName("friendly");

        testPet = new Pet();
        testPet.setCategory(category);
        testPet.setName("doggie");
        testPet.setPhotoUrls(Collections.singletonList("string"));
        testPet.setTags(Collections.singletonList(tag));
        testPet.setStatus("available");
    }

    @Test
    @Order(1)
    @DisplayName("Создание питомца")
    public void testCreatePet() throws IOException {
        Pet created = helper.createPet(testPet);
        assertNotNull(created, "Созданный питомец не должен быть null");
        assertTrue(created.getId() > 0, "ID созданного питомца должен быть положительным");
        createdPetId = created.getId();
        testPet.setId(createdPetId); // сохраняем ID для дальнейших тестов
    }

    @Test
    @Order(2)
    @DisplayName("Получение питомца")
    public void testGetPet() throws IOException {
        Pet pet = helper.getPet(createdPetId);
        assertNotNull(pet, "Питомец не найден по ID");
        assertEquals(testPet.getName(), pet.getName());
    }

    @Test
    @Order(3)
    @DisplayName("Обновление питомца")
    public void testUpdatePet() throws IOException {
        testPet.setStatus("sold");
        Pet updated = helper.updatePet(testPet);
        assertNotNull(updated);
        assertEquals("sold", updated.getStatus());
    }

    @Test
    @Order(4)
    @DisplayName("Удаление питомца")
    public void testDeletePet() throws IOException {
        helper.deletePet(createdPetId);
        Pet deleted = helper.getPet(createdPetId);
        assertNull(deleted, "Питомец должен быть удалён и не найден");
    }
}