var ZOOM = 0.8;
const COLORS = {
    "FOREST": "#007700", "MOUNTAIN": "#666666", "WATER": "blue", "PASTURE": "#66aa00", "DESERT": "#aa8800"
};

var socket;
var mouseX, mouseY;
var gameData;
var edges, tiles, crossings;
var shapeInFocus = null;

function render(context) {
    if (gameData) {
        context.fillStyle = "#ffffff";
        context.fillRect(0, 0, 800, 800);
        context.save();
        context.scale(ZOOM, ZOOM);

        edges.concat(crossings)
            .filter(e => e.containsPoint(mouseX, mouseY))
            .forEach(shape => shape.mouseIsOver());

        tiles.forEach((t)=> {
            context.save();
            context.translate(t.x * 100, t.y * 100);
            renderTile(context, t);
            context.restore();
        });


        edges.forEach((e)=> {
            e.render(context);
        });


        crossings.forEach((c)=> {
            c.render(context);
        });

        context.restore();

    }
    requestAnimationFrame(()=>render(context));

}


function renderTile(context, tile) {
    context.fillStyle = COLORS[tile.type] || "#ff00ff";
    context.fillRect(0, 0, 100, 100);
    context.fillStyle = "rgba(0,0,0,0.3)";
    context.fillRect(0, 0, 100, 100);

    context.fillStyle = COLORS[tile.type] || "#ff00ff";
    context.fillRect(1, 1, 98, 98);

    // context.fillStyle = "rgba(250,250,250,0.75)";
    // context.beginPath();
    // context.moveTo(80,80);
    // context.arc(80,80,10,0, 2 * Math.PI * (1-tile.production), false);
    // context.closePath();
    // context.fill();


}

function connect() {
    socket = new WebSocket("ws://localhost:8080/game-state");
    socket.onmessage = (message) => {

        if (message.data) {
            gameData = JSON.parse(message.data);
            edges = gameData.edges.map(e=> new Edge(e));
            crossings = gameData.crossings.map((c) => {
                return new Crossing(c)
            });
            tiles = gameData.tiles;
        }
    };
    socket.onclose = () => setTimeout(()=> {
        connect()
    }, 500);
};

function start() {

    var canvas = document.getElementById('gameScreen');
    var renderContext = canvas.getContext("2d");

    connect();
    render(renderContext);

    canvas.onmousemove = (e) => {
        if (gameData) {
            var x = e.offsetX;
            var y = e.offsetY;

            mouseX = x / ZOOM;
            mouseY = y / ZOOM;

            var containingShapes = edges.concat(crossings).filter(e => e.containsPoint(mouseX, mouseY));
            if (containingShapes.length > 0) {
                var oneOfTheContainingShapes = containingShapes[0];
                if (shapeInFocus != oneOfTheContainingShapes) {
                    oneOfTheContainingShapes.mouseIsOver();
                    if (shapeInFocus) {
                        shapeInFocus.mouseIsNotOver();
                    }

                }
                shapeInFocus = oneOfTheContainingShapes;

            } else {
                if (shapeInFocus != null) {
                    shapeInFocus = null;
                }
            }

            if (shapeInFocus != null) {
                canvas.style.cursor = "pointer";
            } else {
                canvas.style.cursor = "auto";
            }
        }
    };

    canvas.onclick = (e) => {
        if(shapeInFocus){
            socket.send(JSON.stringify({
                type: "SHAPE_CLICKED",
                id: shapeInFocus.data.id,
                player: document.getElementById("playerColor").value
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