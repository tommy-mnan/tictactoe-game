const url = 'http://localhost:8080';
let stompClient;
let gameId;
let playerType;

function connectToSocket(gameId) {
    console.log("connecting to the game");
    let socket = new SockJS(url + "/gameplay");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            console.log(data);
            displayResponse(data);
        })
    })
}

function create_game() {
    var boardSize = $("#boardSize").val();
    if(boardSize != 0){
        let login = document.getElementById("login").value;
        if (login == null || login === '') {
            alert("Please enter login");
        } else {
            $.ajax({
                url: url + "/game/start",
                type: 'POST',
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify({
                    "player": {
                        "login": login
                    },
                    "size": boardSize
                }),
                success: function (data) {
                    gameId = data.gameId;
                    playerType = 'X';
                    reset();
                    connectToSocket(gameId);
                    alert("Your created a game. Game id is: " + data.gameId);
                    gameOn = true;
                },
                error: function (error) {
                    console.log(error);
                }
            })
        }
    } else{
        alert("Please Select Board Size");
    }
}


function connectToRandom() {
    var boardSize = $("#boardSize").val();
    if(boardSize != 0) {
        let login = document.getElementById("login").value;
        if (login == null || login === '') {
            alert("Please enter login");
        } else {
            $.ajax({
                url: url + "/game/connect/random",
                type: 'POST',
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify({
                    "player": {
                        "login": login
                    },
                    "size": boardSize
                }),
                success: function (data) {
                    gameId = data.gameId;
                    playerType = 'O';
                    reset();
                    connectToSocket(gameId);
                    displayResponse(data);
                    alert("Congrats you're playing with: " + data.player1.login);
                },
                error: function (error) {
                    console.log(error);
                    alert("Game Not Found");
                }
            })
        }
    } else{
        alert("Please Select Board Size");
    }
}

function connectToSpecificGame() {
    var boardSize = $("#boardSize").val();
    if(boardSize != 0) {
        let login = document.getElementById("login").value;
        if (login == null || login === '') {
            alert("Please enter login");
        } else {
            let gameIdc = document.getElementById("game_id").value;
            if (gameIdc == null || gameIdc === '') {
                alert("Please enter game id");
            }
            $.ajax({
                url: url + "/game/connect",
                type: 'POST',
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify({
                    "player": {
                        "login": login
                    },
                    "gameId": gameIdc,
                    "size" : boardSize
                }),
                success: function (data) {
                    gameId = data.gameId;
                    playerType = 'O';
                    reset();
                    connectToSocket(gameId);
                    alert("Congrats you're playing with: " + data.player1.login);
                },
                error: function (error) {
                    console.log(error);
                    alert("Game Not Found");
                }
            })
        }
    }else{
        alert("Please Select Board Size");
    }
}
