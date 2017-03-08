var ZOOM = 0.9;

var socket;
var mouseX, mouseY;
var gameData;
var tiles;
var shapeInFocus = null;
var player;

var renderTiles = function (context) {
    tiles.forEach((t)=> {
        t.render(context, player);
    });
};
function render(context) {
    if (gameData) {
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
    players.forEach((p)=>{

        newHtml += `<div>`;
        newHtml += `<span class="player-name" style="color: ${p.color}">${p.name}</span> `;
        newHtml += `<dl class="resource-list">`;
        for(var r in p.resources){
            newHtml += `<dt>${r}</dt><dd>${p.resources[r]}</dd>`
        }
        newHtml += `</dl>`;
        newHtml += `</div>`
    });
    elm.innerHTML = newHtml;

}
function connect(playerInfo) {
    socket = new WebSocket(`wss://${window.location.hostname}:${window.location.port}/game-state`);
    socket.onmessage = (message) => {

        if (message.data) {
            gameData = JSON.parse(message.data);
            tiles = gameData.tiles.map(t=> new Tile(t));

            displayPlayerInfo(gameData.players)
        }
    };
    socket.onopen = () => {
        socket.send(JSON.stringify(playerInfo))
    };
    socket.onclose = () => setTimeout(()=> {
        connect(playerInfo)
    }, 500);
};

function joinGame(e) {
    e.preventDefault();
    var playerName = document.getElementById('playerName').value;
    var color = document.getElementById("playerColor").value;
    player = {
        name: playerName,
        color: color
    };

    var joinGameMessage = Object.assign({}, player, {type: "JOIN_GAME"});
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
        if (gameData) {
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
                playerName: player.name
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
                playerName: player.name
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

}

// in case the document is already rendered
if (document.readyState != 'loading') start();
// modern browsers
else if (document.addEventListener) document.addEventListener('DOMContentLoaded', start);
// IE <= 8
else document.attachEvent('onreadystatechange', function () {
        if (document.readyState == 'complete') start();
    });