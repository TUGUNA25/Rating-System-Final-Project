package com.tuguna.rating_system.config;

import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameSeeder implements CommandLineRunner {

    private final GameRepository gameRepository;

    @Override
    public void run(String... args) {

        if (gameRepository.count() > 0) {
            return;
        }

        // Using constructor (because of your entity)
        gameRepository.save(new Game(null, "Valorant", null));
        gameRepository.save(new Game(null, "CS2", null));
        gameRepository.save(new Game(null, "League of Legends", null));
        gameRepository.save(new Game(null, "Fortnite", null));
        gameRepository.save(new Game(null, "Dota 2", null));
        gameRepository.save(new Game(null, "PUBG", null));
        gameRepository.save(new Game(null, "Apex Legends", null));
        gameRepository.save(new Game(null, "Minecraft", null));
        gameRepository.save(new Game(null, "GTA V", null));
        gameRepository.save(new Game(null, "Overwatch 2", null));
        gameRepository.save(new Game(null, "Rainbow Six Siege", null));

    }
}