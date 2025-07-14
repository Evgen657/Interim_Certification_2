package Lesson8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerServiceJSON implements PlayerService {
    private static final String FILE_NAME = "players.json";
    private List<Player> players;
    private int nextId;
    private Gson gson;

    public PlayerServiceJSON() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.players = loadPlayers();
        this.nextId = players.size() > 0 ? players.get(players.size() - 1).getId() + 1 : 1; // Установка следующего ID
    }

    private List<Player> loadPlayers() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type playerListType = new TypeToken<ArrayList<Player>>() {}.getType();
            return gson.fromJson(reader, playerListType);
        } catch (FileNotFoundException e) {
            return new ArrayList<>(); // Если файл не найден, возвращаем пустой список
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void savePlayers() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Player getPlayerById(int id) {
        return players.stream().filter(player -> player.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Collection<Player> getPlayers() {
        return new ArrayList<>(players); // Возвращаем копию списка, чтобы избежать изменений
    }

    @Override
    public int createPlayer(String nickname) {
        Player newPlayer = new Player(nextId, nickname, 0, false);
        players.add(newPlayer);
        nextId++;
        savePlayers(); // Сохранение изменений в файл
        return newPlayer.getId();
    }

    @Override
    public Player deletePlayer(int id) {
        Player playerToRemove = getPlayerById(id);
        if (playerToRemove != null) {
            players.remove(playerToRemove);
            savePlayers(); // Сохранение изменений в файл
        }
        return playerToRemove;
    }

    @Override
    public int addPoints(int playerId, int points) {
        Player player = getPlayerById(playerId);
        if (player != null) {
            player.setPoints(player.getPoints() + points);
            savePlayers();                                 // Сохранение изменений в файл
            return player.getPoints();
        }
        return -1; // Если игрок не найден
    }
}
