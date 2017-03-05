var ZOOM = 1;

var socket;
var mouseX, mouseY;
var gameData;
var tiles;
var shapeInFocus = null;
var player;

var renderTiles = function (context) {
    tiles.forEach((t)=> {
        t.render(context);
    });
};
function render(context) {
    if (gameData) {
        context.fillStyle = "#ffffff";
        context.fillStyle = "#000000";
        context.fillRect(0, 0, 800, 800);
        context.save();
        context.scale(ZOOM, ZOOM);

        console.log(mouseX, mouseY);
        tiles.filter(e => e.containsPoint(mouseX, mouseY))
            .forEach(shape => shape.mouseIsOver());

        renderTiles(context);
        context.restore();
    }
    requestAnimationFrame(()=>render(context));

}

function connect(playerInfo) {
    socket = new WebSocket("ws://localhost:8080/game-state");
    socket.onmessage = (message) => {

        if (message.data) {
            gameData = JSON.parse(message.data);
            tiles = gameData.tiles.map(t=> new Tile(t));
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

            // var containingShapes = tiles.filter(e => e.containsPoint(mouseX, mouseY));
            // if (containingShapes.length > 0) {
            //     var oneOfTheContainingShapes = containingShapes[0];
            //     if (shapeInFocus != oneOfTheContainingShapes) {
            //         oneOfTheContainingShapes.mouseIsOver();
            //         if (shapeInFocus) {
            //             shapeInFocus.mouseIsNotOver();
            //         }
            //
            //     }
            //     shapeInFocus = oneOfTheContainingShapes;
            //
            // } else {
            //     if (shapeInFocus != null) {
            //         shapeInFocus = null;
            //     }
            // }
            //
            // if (shapeInFocus != null) {
            //     canvas.style.cursor = "pointer";
            // } else {
            //     canvas.style.cursor = "auto";
            // }
        }
    };

    canvas.onclick = (e) => {
        if (shapeInFocus) {
            socket.send(JSON.stringify({
                type: "SHAPE_CLICKED",
                id: shapeInFocus.data.id,
                playerName: player.name
            }))
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