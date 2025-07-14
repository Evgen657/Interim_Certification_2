package Test.Java.Tests;

import Lesson8.Player;
import Lesson8.PlayerService;
import Lesson8.PlayerServiceJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.Collection;

public class PlayerServiceNegativeTest {

    private PlayerService service;
    private final String filePath = "players.json"; // Путь к JSON-файлу

    @BeforeEach
    public void setUp() {
        // Удаляем JSON-файл перед каждым тестом, чтобы протестировать отсутствие файла
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        service = new PlayerServiceJSON(); // Инициализируем сервис перед каждым тестом
    }

    @Test
    @DisplayName("Проверка удаления несуществующего ID") // тест 1
    public void testDeletePlayerThatDoesNotExist() {
        // Пытаемся удалить игрока с ID 10, хотя последний ID - 8
        Player removedPlayer = service.deletePlayer(10);
        assertNull(removedPlayer, "Removed player should be null, as player with ID 10 does not exist");
    }


    @Test
    @DisplayName("Проверка получения несуществующего ID") // тест 3
    public void testGetPlayerByIdThatDoesNotExist() {
        // Пытаемся получить игрока с ID 999, который не существует
        Player fetchedPlayer = service.getPlayerById(999);
        assertNull(fetchedPlayer, "Player should be null, as player with ID 999 does not exist");
    }


    @Test
    @DisplayName("Проверка на соотвествия ID после загрузки с JSON ") // тест 11
    public void testLoadSystemWithDifferentJsonFile() {
        // Создаем нового игрока и сохраняем в файл
        int playerId1 = service.createPlayer("Player1");
        int playerId2 = service.createPlayer("Player2");

        // Проверяем, что JSON-файл был создан
        assertTrue(new File(filePath).exists(), "JSON-файл должен быть создан");

        // Создаем новый экземпляр PlayerServiceJSON с другим файлом
        PlayerService newService = new PlayerServiceJSON(); // Предполагается, что он загружает из того же файла

        // Получаем игроков из нового сервиса
        Collection<Player> loadedPlayers = newService.getPlayers();

        // Проверяем, что количество игроков в файле соответствует добавленным игрокам
        assertEquals(2, loadedPlayers.size(), "Список загруженных игроков должен содержать 2 игрока");
    }


}