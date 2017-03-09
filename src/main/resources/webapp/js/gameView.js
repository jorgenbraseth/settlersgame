var ZOOM = 0.9;

var socket;
var mouseX, mouseY;
var message;
var tiles;
var shapeInFocus = null;
var player;

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
function connect(playerInfo) {
    var protocol = window.location.protocol == "https:" ? "wss" : "ws";
    socket = new WebSocket(`${protocol}://${window.location.hostname}:${window.location.port}/game-state`);
    socket.onmessage = (msg) => {
        if (msg.data) {
            message = JSON.parse(msg.data);

            switch (message.type) {
                case "GAME_STATE":
                    tiles = message.tiles.map(t=> new Tile(t));
                    displayPlayerInfo(message.players)
                    break;
                case "CHAT":
                    chatMessageReceived(message);

            }
        }
    };
    socket.onopen = () => {
        socket.send(JSON.stringify(playerInfo))
    };
    socket.onclose = () => setTimeout(()=> {
        connect(playerInfo)
    }, 500);
};

function chatMessageReceived(msg) {
    var chat = document.getElementById("chatHistory");


    var newMsg = document.createElement("span");
    newMsg.innerHTML = `<span class="chatEntry"><span class="playerName" style="color: ${msg.player.color}">${msg.player.name}</span> ${msg.message}</span>`;

    chat.appendChild(newMsg);
    chat.scrollTop = chat.scrollHeight;


}

function joinGame(e) {
    e.preventDefault();
    var playerName = document.getElementById('playerName').value;
    var color = document.getElementById("playerColor").value;
    player = {
        name: playerName,
        color: color
    };

    var joinGameMessage = Object.assign({}, player, {type: "JOIN_GAME", gameId: "foo"});
    connect(joinGameMessage);
    var joinForm = document.getElementById("joinForm");
    joinForm.parentNode.removeChild(joinForm);
}

function start() {

    var canvas = document.getElementById('gameScreen');
    var renderContext = canvas.getContext("2d");

    render(renderContext);

    var joinGameButton = document.getElementById('joinGame');
    joinGameButton.onclick = joinGame;

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
            socket.send(JSON.stringify({
                type: "SHAPE_RIGHT_CLICKED",
                id: clickedShape.data.id,
                coords: [clickedShape.data.x, clickedShape.data.y],
                playerName: player.name,
                gameId: "foo"
            }))
        }
    }
    canvas.onclick = (e) => {
        var containingShape = tiles.filter(e => e.containsPoint(mouseX, mouseY));
        if (containingShape.length >= 1) {
            var clickedShape = containingShape[0];
            socket.send(JSON.stringify({
                type: "SHAPE_CLICKED",
                id: clickedShape.data.id,
                coords: [clickedShape.data.x, clickedShape.data.y],
                playerName: player.name,
                gameId: "foo"
            }))
        }
    }

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
                socket.send(JSON.stringify({
                    type: "CHAT",
                    playerName: player.name,
                    message: messageText,
                    gameId: "foo"
                }));
            }

            chatInput.value = "";
        }
    }
}

// in case the document is already rendered
if (document.readyState != 'loading') start();
// modern browsers
else if (document.addEventListener) document.addEventListener('DOMContentLoaded', start);
// IE <= 8
else document.attachEvent('onreadystatechange', function () {
        if (document.readyState == 'complete') start();
    });