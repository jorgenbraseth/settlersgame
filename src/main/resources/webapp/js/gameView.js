const ZOOM = .8;
const COLORS = {
    "FOREST":"#007700","MOUNTAIN":"#666666","WATER":"blue","PASTURE":"#66aa00","DESERT":"#aa8800"
}

function render(context, data){
    context.fillStyle = "#ffffff";
    context.fillRect(0,0,800,800);
    context.save();
    context.scale(ZOOM,ZOOM);
    data.forEach((t)=> {
        context.save();
        context.translate(t.x*100,t.y*100);
        renderTile(context, t);
        context.restore();
    });
    context.restore();
}

function renderTile(context, tile) {
    context.fillStyle = COLORS[tile.type] || "#ff00ff";
    context.fillRect(0,0,100,100);
    context.fillStyle = "#000000";
    context.font = '18px Calibri';
    var textSize = context.measureText(tile.timer);
    context.fillText(tile.timer,100/2-textSize.width/2,100/2+9,100);

    context.lineWidth = 5;
    if(tile.timer == 0){
        context.strokeStyle = "rgba(250,250,0,0.5)";
    }else{
        context.strokeStyle = "rgba(0,0,0,0.5)"
    }
    context.strokeRect(0,0,100,100);
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