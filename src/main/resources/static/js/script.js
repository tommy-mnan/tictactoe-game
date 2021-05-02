var turns = [];
var turnsReset = [];
var turn = "";
var gameOn = false;

function playerTurn(turn, id) {
    if (gameOn) {
        var spotTaken = $("#" + id).text();
        if (spotTaken === "#") {
            makeAMove(playerType, id.split("_")[0], id.split("_")[1]);
        }
    }
}

function makeAMove(type, xCoordinate, yCoordinate) {
    $.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "type": type,
            "coordinateX": xCoordinate,
            "coordinateY": yCoordinate,
            "gameId": gameId
        }),
        success: function (data) {
            displayResponse(data);
            gameOn = false;
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function displayResponse(data) {
    let board = data.board;
    for (let i = 0; i < board.length; i++) {
        for (let j = 0; j < board[i].length; j++) {
            if (board[i][j] === 1) {
                turns[i][j] = 'X'
            } else if (board[i][j] === 2) {
                turns[i][j] = 'O';
            }
            let id = i + "_" + j;
            $("#" + id).text(turns[i][j]);
        }
    }
    if (data.winner != null) {
        if(data.winner != "D"){
            alert("Winner is " + data.winner);
        } else{
            alert("The Game Is DRAW");
        }
    }
    gameOn = true;
}

function play(slot){
    playerTurn(turn, slot);
};

function reset() {
    turns = turnsReset;
    $(".tic").text("#");
}

$("#reset").click(function () {
    reset();
});

function createBoard(x) {
    $("#boardSize").val(x);
    $("#gameBoard").empty();
    var temp = [];
    for (let i = 0; i < x; i++) {
        for (let j = 0; j < x; j++){
            temp.push("#");
            $("#gameBoard").append("<li onclick = 'play(\""+i+"_"+j+"\")' class='tic' id='"+i+"_"+j+"'></li>");
        }
        turns.push(temp);
        temp = [];
    }
    $("#box").css("width",(113*x)+"px");
    if(x == 6){
        $("#box").css("width","630px");
    }
    console.log(turns);
    turnsReset = turns;
}
