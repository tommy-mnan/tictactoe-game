package com.test.controller;

import com.test.controller.dto.ConnectRequest;
import com.test.controller.dto.CreateRequest;
import com.test.exception.InvalidGameException;
import com.test.exception.InvalidParamException;
import com.test.exception.NotFoundException;
import com.test.model.Game;
import com.test.model.GamePlay;
import com.test.model.Player;
import com.test.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody CreateRequest request) {
        log.info("start game request: {}", request);
        return ResponseEntity.ok(gameService.createGame(request.getPlayer(),request.getSize()));
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId(), request.getSize()));
    }

    @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom(@RequestBody CreateRequest request) throws NotFoundException {
        log.info("connect random {}", request);
        return ResponseEntity.ok(gameService.connectToRandomGame(request.getPlayer(),request.getSize()));
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGameException {
        log.info("gameplay: {}", request);
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }
}
