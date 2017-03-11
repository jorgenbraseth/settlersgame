var ZOOM = 0.9;

var currentGame;
var socket;
var mouseX, mouseY;
var message;
var tiles;
var shapeInFocus = null;
var player;
var rendering = false;

var renderTiles = function (context) {
    tiles.forEach((t)=> {
        t.render(context, player);
    });
};
function render(context) {
    if (message) {
        context.fillStyle = "#ffffff";
        context.fillStyle = "#000000";
        context.fillRect(0, 0, 800, 800);
        context.save();
        context.scale(ZOOM, ZOOM);

        tiles.filter(e => e.containsPoint(mouseX, mouseY))
            .forEach(shape => shape.mouseIsOver());

        renderTiles(context);
        context.restore();
    }
    requestAnimationFrame(()=>render(context));

}

function displayPlayerInfo(players) {
    var elm = document.getElementById("playerList");
    var newHtml = "";
    players.forEach((p)=> {

        newHtml += `<div>`;
        newHtml += `<span class="player-name" style="color: ${p.color}">${p.name}</span> `;
        newHtml += `<dl class="resource-list">`;
        for (var r in p.resources) {
            newHtml += `$${p.resources[r]}`
        }
        newHtml += `</dl>`;
        newHtml += `</div>`
    });
    elm.innerHTML = newHtml;

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
                    tiles = message.tiles.map(t=> new Tile(t));
                    if (!rendering) {
                        render(renderContext());
                        rendering = true;
                    }
                    displayPlayerInfo(message.players)
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
};

function send(obj) {
    console.log(obj);
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
    console.log(gameName);
    var playerName = document.getElementById('playerName').value;
    var color = document.querySelector('input[name = "playerColor"]:checked').value;
    currentGame = gameName;
    player = {
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

function renderContext() {
    var canvas = document.getElementById('gameScreen');
    return canvas.getContext("2d");
}

function start() {

    var createGameButton = document.getElementById('createGameButton');
    createGameButton.onclick = createGameClicked;
    var canvas = document.getElementById('gameScreen');
    canvas.onmousemove = (e) => {
        if (message) {
            var x = e.offsetX;
            var y = e.offsetY;

            mouseX = x / ZOOM;
            mouseY = y / ZOOM;
        }
    };

    canvas.oncontextmenu = (e) => {
        e.preventDefault();
        var containingShape = tiles.filter(e => e.containsPoint(mouseX, mouseY));
        if (containingShape.length >= 1) {
            var clickedShape = containingShape[0];
            send({
                type: "SHAPE_RIGHT_CLICKED",
                id: clickedShape.data.id,
                coords: [clickedShape.data.x, clickedShape.data.y],
                playerName: player.name,
                gameId: currentGame
            })
        }
    };
    canvas.onclick = (e) => {
        var containingShape = tiles.filter(e => e.containsPoint(mouseX, mouseY));
        if (containingShape.length >= 1) {
            var clickedShape = containingShape[0];
            send({
                type: "SHAPE_CLICKED",
                id: clickedShape.data.id,
                coords: [clickedShape.data.x, clickedShape.data.y],
                playerName: player.name,
                gameId: currentGame
            })
        }
    };

    canvas.onmousewheel = (e)=> {
        var zoomIn = e.deltaY < 0;

        if (zoomIn) {
            ZOOM = Math.min(2, ZOOM + 0.1);
        } else {
            ZOOM = Math.max(0.5, ZOOM - 0.1);
        }
    };

    var chatInput = document.getElementById("chatInput");
    chatInput.onkeydown = (e) => {
        if (e.keyCode === 13) {
            var messageText = chatInput.value;
            if (messageText !== "") {
                send({
                    type: "CHAT",
                    playerName: player.name,
                    message: messageText,
                    gameId: currentGame
                });
            }

            chatInput.value = "";
        }
    };

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