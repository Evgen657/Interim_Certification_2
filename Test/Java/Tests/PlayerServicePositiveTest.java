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

public class PlayerServicePositiveTest {

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
    @DisplayName("Создан игрок без JSON-файла")
    public void testAddPlayerWhenJsonFileDoesNotExist() {
        // Добавляем игрока
        int playerId = service.createPlayer("PlayerWithoutFile");

        // Проверяем, что игрок добавлен
        Player addedPlayer = service.getPlayerById(playerId);
        assertNotNull(addedPlayer, "Игрок должен быть добавлен и не равен null");
        assertEquals("PlayerWithoutFile", addedPlayer.getNick(), "Ник игрока должен совпадать");

        // Проверяем, что JSON-файл был создан и содержит информацию об игроке
        assertTrue(new File(filePath).exists(), "JSON-файл должен быть создан");
        Collection<Player> players = service.getPlayers();
        assertEquals(1, players.size(), "Список игроков должен содержать 1 игрока");
    }

    @Test
    @DisplayName("Создан игрок с JSON-файлом")
    public void testAddPlayerWhenJsonFileExists() {
        // Сначала создаем игрока, чтобы создать JSON-файл
        int playerId = service.createPlayer("InitialPlayer");

        // Убедимся, что файл создан
        assertTrue(new File(filePath).exists(), "JSON-файл должен существовать");

        // Добавляем нового игрока
        int newPlayerId = service.createPlayer("PlayerWithFile");

        // Проверяем, что новый игрок добавлен
        Player addedPlayer = service.getPlayerById(newPlayerId);
        assertNotNull(addedPlayer, "Игрок должен быть добавлен и не равен null");
        assertEquals("PlayerWithFile", addedPlayer.getNick(), "Ник нового игрока должен совпадать");

        // Проверяем, что JSON-файл все еще существует и содержит информацию о двух игроках
        Collection<Player> players = service.getPlayers();
        assertEquals(2, players.size(), "Список игроков должен содержать 2 игрока");
    }

    @Test
    @DisplayName("Проверка списка игроков")
    public void testAddPlayerAndCheckInfo() {
        // Добавляем первого игрока
        int playerId1 = service.createPlayer("PlayerOne");

        // Проверяем наличие первого игрока в списке
        Player playerOne = service.getPlayerById(playerId1);
        assertNotNull(playerOne, "PlayerOne должен быть в списке");

        // Получаем список игроков
        Collection<Player> players = service.getPlayers();
        assertFalse(players.isEmpty(), "Список игроков не должен быть пустым");

        // Добавляем второго игрока
        int playerId2 = service.createPlayer("PlayerTwo");

        // Получаем информацию о втором игроке
        Player playerTwo = service.getPlayerById(playerId2);
        assertNotNull(playerTwo, "PlayerTwo должен быть в списке");

        // Проверяем, что информация о втором игроке соответствует ожиданиям
        assertEquals("PlayerTwo", playerTwo.getNick(), "Ник PlayerTwo должен совпадать");
        assertEquals(playerId2, playerTwo.getId(), "ID PlayerTwo должен совпадать");
        assertEquals(0, playerTwo.getPoints(), "Очки PlayerTwo должны быть 0");


        // Удаляем созданных игроков
        service.deletePlayer(playerId1);
        service.deletePlayer(playerId2);
    }

    @Test
    @DisplayName("Проверка на удаление игрока")
    public void testAddAndRemovePlayer() {
        // Добавляем игрока
        int playerId = service.createPlayer("TestPlayer");

        // Проверяем, что игрок добавлен
        Player addedPlayer = service.getPlayerById(playerId);
        assertNotNull(addedPlayer, "Игрок должен быть добавлен и не равен null");
        assertEquals("TestPlayer", addedPlayer.getNick(), "Ник игрока должен совпадать");

        // Удаляем игрока
        Player removedPlayer = service.deletePlayer(playerId);

        // Проверяем, что игрок удален
        assertNotNull(removedPlayer, "Удаленный игрок не должен быть равен null");
        assertEquals("TestPlayer", removedPlayer.getNick(), "Ник удаленного игрока должен совпадать");

        // Проверяем, что игрока больше нет в списке
        Player fetchedPlayer = service.getPlayerById(playerId);
        assertNull(fetchedPlayer, "Игрок должен быть удален и не найден в списке");
    }

    @Test
    @DisplayName("Удаление всех игроков")
    public void testRemoveAllPlayers() {
        // Добавляем нескольких игроков
        service.createPlayer("Player1");
        service.createPlayer("Player2");
        service.createPlayer("Player3");

        // Получаем список игроков
        Collection<Player> players = service.getPlayers();
        assertEquals(3, players.size(), "Список игроков должен содержать 3 игрока");

        // Удаляем всех игроков
        for (Player player : players) {
            service.deletePlayer(player.getId());
        }

        // Проверяем, что список игроков пуст
        players = service.getPlayers();
        assertTrue(players.isEmpty(), "Список игроков должен быть пустым после удаления всех игроков");
    }

    @Test
    @DisplayName("Начисление баллов игроку")
    public void testAddPointsToExistingPlayer() {
        // Добавляем игрока
        int playerId = service.createPlayer("PlayerWithPoints");

        // Начисляем очки игроку
        int updatedPoints = service.addPoints(playerId, 50);

        // Проверяем, что очки обновлены
        Player player = service.getPlayerById(playerId);
        assertNotNull(player, "Игрок должен быть найден");
        assertEquals(50, player.getPoints(), "Очки игрока должны быть 50");
    }

    @Test
    @DisplayName("Начисление баллов игроку у которого есть баллы")
    public void testAddAdditionalPointsToExistingPlayer() {
        // Добавляем игрока
        int playerId = service.createPlayer("PlayerWithAdditionalPoints");

        // Начисляем начальные очки
        service.addPoints(playerId, 30);
        Player player = service.getPlayerById(playerId);
        assertNotNull(player, "Игрок должен быть найден");
        assertEquals(30, player.getPoints(), "Очки игрока должны быть 30");

        // Начисляем дополнительные очки
        int updatedPoints = service.addPoints(playerId, 20);

        // Проверяем, что очки обновлены
        player = service.getPlayerById(playerId);
        assertNotNull(player, "Игрок должен быть найден");
        assertEquals(50, player.getPoints(), "Очки игрока должны быть 50 после добавления дополнительных");
    }

    @Test
    @DisplayName("Поиск игрока по ID")
    public void testGetPlayerById() {
        // Добавляем игрока
        int playerId = service.createPlayer("PlayerToFetch");

        // Получаем игрока по ID
        Player fetchedPlayer = service.getPlayerById(playerId);

        // Проверяем, что полученный игрок соответствует ожиданиям
        assertNotNull(fetchedPlayer, "Игрок должен быть найден");
        assertEquals("PlayerToFetch", fetchedPlayer.getNick(), "Ник игрока должен совпадать");
        assertEquals(playerId, fetchedPlayer.getId(), "ID игрока должен совпадать");
        assertEquals(0, fetchedPlayer.getPoints(), "Очки игрока должны быть 0");
    }

    @Test
    @DisplayName("Проверка на корректность заполнения данных игрока")
    public void testSaveToFile() {
        // Добавляем нескольких игроков
        service.createPlayer("Player1");
        service.createPlayer("Player2");

        // Проверяем, что JSON-файл был создан
        assertTrue(new File(filePath).exists(), "JSON-файл должен быть создан");

        // Загружаем игроков из файла
        Collection<Player> playersFromFile = service.getPlayers();

        // Проверяем, что количество игроков в файле соответствует добавленным игрокам
        assertEquals(2, playersFromFile.size(), "Список игроков из файла должен содержать 2 игрока");

        // Проверяем, что имена игроков соответствуют ожидаемым
        assertTrue(playersFromFile.stream().anyMatch(p -> p.getNick().equals("Player1")), "Player1 должен быть в списке");
        assertTrue(playersFromFile.stream().anyMatch(p -> p.getNick().equals("Player2")), "Player2 должен быть в списке");
    }

    @Test
    @DisplayName("Проверка записей при копировании из другого файла")
    public void testLoadPlayersFromFile_NoLossOfRecords() {
        // Добавляем нескольких игроков и сохраняем их в файл
        int playerId1 = service.createPlayer("Player1");
        int playerId2 = service.createPlayer("Player2");

        // Проверяем, что JSON-файл был создан
        assertTrue(new File(filePath).exists(), "JSON-файл должен быть создан");

        // Создаем новый экземпляр PlayerServiceJSON, чтобы загрузить игроков из файла
        PlayerService newService = new PlayerServiceJSON();

        // Получаем игроков из нового сервиса
        Collection<Player> loadedPlayers = newService.getPlayers();

        // Проверяем, что количество игроков в файле соответствует добавленным игрокам
        assertEquals(2, loadedPlayers.size(), "Список загруженных игроков должен содержать 2 игрока");

        // Проверяем, что имена игроков соответствуют ожидаемым
        assertTrue(loadedPlayers.stream().anyMatch(p -> p.getNick().equals("Player1")), "Player1 должен быть в списке");
        assertTrue(loadedPlayers.stream().anyMatch(p -> p.getNick().equals("Player2")), "Player2 должен быть в списке");
    }

    @Test
    @DisplayName("Проверка, что все записи при копировании на месте")
    public void testLoadPlayersFromFile_NoOverwriteRecords() {
        // Добавляем нескольких игроков и сохраняем их в файл
        int playerId1 = service.createPlayer("Player1");
        int playerId2 = service.createPlayer("Player2");

        // Проверяем, что JSON-файл был создан
        assertTrue(new File(filePath).exists(), "JSON-файл должен быть создан");

        // Создаем новый экземпляр PlayerServiceJSON, чтобы загрузить игроков из файла
        PlayerService newService = new PlayerServiceJSON();

        // Получаем игроков из нового сервиса
        Collection<Player> loadedPlayers = newService.getPlayers();

        // Проверяем, что количество игроков в файле соответствует добавленным игрокам
        assertEquals(2, loadedPlayers.size(), "Список загруженных игроков должен содержать 2 игрока");

        // Добавляем еще одного игрока в новый сервис
        int playerId3 = newService.createPlayer("Player3");

        // Проверяем, что старые записи не были перезаписаны
        loadedPlayers = newService.getPlayers();
        assertEquals(3, loadedPlayers.size(), "Список загруженных игроков должен содержать 3 игрока");

        // Проверяем, что все имена игроков соответствуют ожидаемым
        assertTrue(loadedPlayers.stream().anyMatch(p -> p.getNick().equals("Player1")), "Player1 должен быть в списке");
        assertTrue(loadedPlayers.stream().anyMatch(p -> p.getNick().equals("Player2")), "Player2 должен быть в списке");
        assertTrue(loadedPlayers.stream().anyMatch(p -> p.getNick().equals("Player3")), "Player3 должен быть в списке");
    }

    @Test
    @DisplayName("Проверка на уникальность записей")
    public void testUniqueIdAfterDeletion() {
        // Создаем 5 игроков
        int playerId1 = service.createPlayer("Player1");
        int playerId2 = service.createPlayer("Player2");
        int playerId3 = service.createPlayer("Player3");
        int playerId4 = service.createPlayer("Player4");
        int playerId5 = service.createPlayer("Player5");

        // Удаляем третьего игрока
        service.deletePlayer(playerId3);

        // Добавляем еще одного игрока
        int newPlayerId = service.createPlayer("Player6");

        // Проверяем, что ID нового игрока равен 6
        assertEquals(6, newPlayerId, "ID нового игрока должен быть 6, а не 3");
    }

    @Test
    @DisplayName("Проверка запроса списка игроков при отсутствии JSON-файла")
    public void testGetPlayersWhenJsonFileDoesNotExist() {
        // Убедимся, что JSON-файл отсутствует
        File file = new File(filePath);
        assertFalse(file.exists(), "JSON-файл не должен существовать");

        // Запрашиваем список игроков
        Collection<Player> players = service.getPlayers();

        // Проверяем, что список игроков пуст
        assertTrue(players.isEmpty(), "Список игроков должен быть пустым, так как JSON-файл отсутствует");
    }

    @Test
    @DisplayName("Проверка создания игрока с 15 символами")
    public void testCreatePlayerWith15Characters() {
        // Создаем игрока с ником, состоящим из 15 символов
        String nickname = "Супермен-Крипто";
        int playerId = service.createPlayer(nickname);

        // Проверяем, что игрок добавлен
        Player addedPlayer = service.getPlayerById(playerId);
        assertNotNull(addedPlayer, "Игрок должен быть добавлен и не равен null");
        assertEquals(nickname, addedPlayer.getNick(), "Ник игрока должен совпадать");
    }



}