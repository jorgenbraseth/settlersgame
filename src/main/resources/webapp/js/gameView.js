var game;
var gameControls;
var gameScreen;
var currentGame;
var socket;
var message;
var gameState;
var playerName;
var gameRendering = false;
var playerOverlay;
var buildMode = null;


function render() {
    if (gameState) {
        playerOverlay.render();
        gameScreen.render();
        gameControls.render();
    }
    requestAnimationFrame(()=>render());
}

function gameListReceived(message) {
    var listElm = document.getElementById("gameList");
    listElm.innerHTML = "";
    if (message.games.length > 0) {
        message.games.forEach(game => listElm.appendChild(gameInfoDomElement(game)));
        document.getElementById("existingGames").style.display = "flex";
    }

}

function gameInfoDomElement(gameInfo) {
    var players = "";
    var elm = document.createElement("li");
    var infoElm = document.createElement("div");
    infoElm.style.flex = 1;
    elm.appendChild(infoElm);
    elm.setAttribute("class", "gameInfo");

    infoElm.innerHTML = `<span class="gameName">${gameInfo.gameId}</span>`;
    var joinButton = document.createElement("button");
    joinButton.setAttribute("class", "joinGame");
    joinButton.setAttribute("data-gameId", gameInfo.gameId);
    joinButton.innerText = "Join";

    joinButton.onclick = (e) => {
        e.preventDefault();
        joinGame(gameInfo.gameId)
    };
    elm.appendChild(joinButton);


    var playerList = document.createElement("ul");

    playerList.setAttribute("class", "playerList");
    console.log(gameInfo.players);
    gameInfo.players.forEach(player => {
        console.log(player);
        var playerInfo = document.createElement("li");
        playerInfo.innerHTML = `<span class="playerName" style="color: ${player.color}">${player.name}</span>`;
        playerList.appendChild(playerInfo);
    });
    infoElm.appendChild(playerList);
    return elm;
}


function connect() {
    var protocol = window.location.protocol == "https:" ? "wss" : "ws";
    socket = new WebSocket(`${protocol}://${window.location.hostname}:${window.location.port}/game-state`);
    socket.onmessage = (msg) => {
        if (msg.data) {
            message = JSON.parse(msg.data);

            switch (message.type) {
                case "GAME_STATE":
                    gameState = message;
                    gameScreen.setMessage(message);
                    playerOverlay.setPlayers(message.players);
                    gameControls.setPlayerInfo(message.players.filter(player => player.name == playerName)[0]);
                    if (!gameRendering) {
                        render();
                        gameRendering = true;
                    }
                    break;
                case "CHAT":
                    chatMessageReceived(message);
                    break;
                case "GAME_LIST":
                    gameListReceived(message)
                    break;

            }
        }
    };
    socket.onopen = () => {
        send({
            type: "LIST_GAMES"
        })
    };
    socket.onclose = () => setTimeout(()=> {
        connect();
        joinGame(currentGame);
    }, 500);
}

function send(obj) {
    socket.send(JSON.stringify(obj));
}

function chatMessageReceived(msg) {
    var chat = document.getElementById("chatHistory");


    var newMsg = document.createElement("span");
    newMsg.innerHTML = `<span class="chatEntry"><span class="playerName" style="color: ${msg.player.color}">${msg.player.name}</span> ${msg.message}</span>`;

    chat.appendChild(newMsg);
    chat.scrollTop = chat.scrollHeight;


}

var joinGame = function (gameName) {
    playerName = document.getElementById('playerName').value;

    gameScreen.playerName = playerName;
    gameScreen.gameId = gameName;
    var color = document.querySelector('input[name = "playerColor"]:checked').value;
    currentGame = gameName;
    var player = {
        name: playerName,
        color: color
    };

    var joinGameMessage = Object.assign({}, player, {type: "JOIN_GAME", gameId: gameName});
    send(joinGameMessage);
    var joinForm = document.getElementById("joinForm");
    joinForm.parentNode.removeChild(joinForm);

    document.getElementById("game").style.display = "flex";
};
function createGameClicked(e) {
    e.preventDefault();
    currentGame = document.getElementById('gameName').value;
    joinGame(currentGame);
}

function start() {

    document.querySelectorAll("#gameScreenFrame canvas").forEach(canvasElm => {
        canvasElm.setAttribute("width", SCREEN_WIDTH_PIXELS);
        canvasElm.setAttribute("height", SCREEN_HEIGHT_PIXELS);
    });

    playerOverlay = new PlayerOverlay(document.getElementById('playerInfoOverlay'));

    var createGameButton = document.getElementById('createGameButton');
    createGameButton.onclick = createGameClicked;
    var gameScreenCanvas = document.getElementById('gameScreen');

    gameScreen = new GameScreen(gameScreenCanvas, currentGame, playerName, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS, MAP_WIDTH_TILES, MAP_HEIGHT_TILES);

    document.onkeydown = (e) => {
        switch (e.key) {
            case 'w':
                gameScreen.panUp();
                break;
            case 'a':
                gameScreen.panLeft();
                break;
            case 's':
                gameScreen.panDown();
                break;
            case 'd':
                gameScreen.panRight();
                break;
        }
    };

    var chatInput = document.getElementById("chatInput");
    chatInput.onkeydown = (e) => {
        if (e.keyCode === 13) {
            var messageText = chatInput.value;
            if (messageText !== "") {
                send({
                    type: "CHAT",
                    playerName: playerName,
                    message: messageText,
                    gameId: currentGame
                });
            }

            chatInput.value = "";
        }
    };


    gameControls = new GameControls(document.getElementById('gameControls'), gameScreenCanvas, playerName);
    gameControls.chooseBuildMode = (mode) => {
        console.log(`Changing mode to ${mode}`);
        buildMode = mode
    };

    game = new Game();

    connect();
}


// in case the document is already rendered
if (document.readyState != 'loading') start();
// modern browsers
else if (document.addEventListener) document.addEventListener('DOMContentLoaded', start);
// IE <= 8
else document.attachEvent('onreadystatechange', function () {
        if (document.readyState == 'complete') start();
    });