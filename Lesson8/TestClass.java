package Lesson8;

import org.junit.jupiter.api.DisplayName;

import java.util.Collection;

public class TestClass {  @DisplayName("Проверка на удаление игрока")

    public static void main(String[] args) {
        // Инициализация сервиса
        PlayerService service = new PlayerServiceJSON();

        // Создание игрока
        int playerId = service.createPlayer("WinMaster_777");
        System.out.println("Создан игрок с ID: " + playerId);

        // Добавление очков
        service.addPoints(playerId, 100);
        System.out.println("Добавлено 100 очков игроку с ID: " + playerId);

        // Получение списка игроков
        Collection<Player> players = service.getPlayers();
        System.out.println("Список игроков:");
        for (Player player : players) {
            System.out.println(player);
        }

        // Удаление игрока
        Player removedPlayer = service.deletePlayer(playerId);
        System.out.println("Удален игрок: " + removedPlayer);
    }
}