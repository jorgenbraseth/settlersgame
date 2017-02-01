
var ZOOM = 0.8;
const COLORS = {
    "FOREST":"#007700","MOUNTAIN":"#666666","WATER":"blue","PASTURE":"#66aa00","DESERT":"#aa8800"
};

function render(context, data){
    context.fillStyle = "#ffffff";
    context.fillRect(0,0,800,800);
    context.save();
    context.scale(ZOOM,ZOOM);

    data.tiles.forEach((t)=> {
        context.save();
        context.translate(t.x*100,t.y*100);
        renderTile(context, t);
        context.restore();
    });

    data.edges.map(e=> new Edge(e)).forEach((e)=> {
        e.render(context);
    });

    data.crossings.map((c) => {return new Crossing(c)}).forEach((c)=> {
        c.render(context);
    });

    context.restore();
}


function renderTile(context, tile) {
    context.fillStyle = COLORS[tile.type] || "#ff00ff";
    context.fillRect(0,0,100,100);
    context.fillStyle = "rgba(0,0,0,0.3)";
    context.fillRect(0,0,100,100);

    context.fillStyle = COLORS[tile.type] || "#ff00ff";
    context.fillRect(1,1,98,98);

    // context.fillStyle = "rgba(250,250,250,0.75)";
    // context.beginPath();
    // context.moveTo(80,80);
    // context.arc(80,80,10,0, 2 * Math.PI * (1-tile.production), false);
    // context.closePath();
    // context.fill();


}
var connect = function (renderContext) {
    var socket = new WebSocket("ws://localhost:8080/game-state");
    socket.onmessage = (message) => {
        if(message.data) {
            render(renderContext, JSON.parse(message.data));
        }
    };
    socket.onclose = () => setTimeout(()=>{connect(renderContext)},500);
};

function start() {

    var canvas = document.getElementById('gameScreen');
    var renderContext = canvas.getContext("2d");

    connect(renderContext);

}

// in case the document is already rendered
if (document.readyState!='loading') start();
// modern browsers
else if (document.addEventListener) document.addEventListener('DOMContentLoaded', start);
// IE <= 8
else document.attachEvent('onreadystatechange', function(){
        if (document.readyState=='complete') start();
    });