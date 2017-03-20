var hexagonAngle = 0.523598776, // 30 degrees in radians
    sideLength = TILE_SIZE;

var hexHeight = Math.sin(hexagonAngle) * sideLength;
var hexRadius = Math.cos(hexagonAngle) * sideLength;
var hexRectangleHeight = sideLength + 2 * hexHeight;
var hexRectangleWidth = 2 * hexRadius;

class GameScreen {

    constructor(canvas, gameId, playerName, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS, MAP_WIDTH_TILES, MAP_HEIGHT_TILES) {
        this.canvas = canvas;
        this.panX = 0;
        this.panY = 0;
        this.mouseX = 0;
        this.mouseY = 0;
        this.zoom = 1;
        this.SCREEN_HEIGHT_PIXELS = SCREEN_HEIGHT_PIXELS;
        this.SCREEN_WIDTH_PIXELS = SCREEN_WIDTH_PIXELS;
        this.MAP_WIDTH_TILES = MAP_WIDTH_TILES;
        this.MAP_HEIGHT_TILES = MAP_HEIGHT_TILES;
        this.playerName = playerName;
        this.gameId = gameId;
        this.bindMouseEvents(canvas);
    }
    
    setMessage(message){
        this.message = message;
        this.player = message.players[this.playerName];
        this.tiles = message.tiles.map(t=> new Tile(t, () => {return this.buildMode}, this.playerName, this));
    }

    setBuildMode(mode){
        this.buildMode = mode;
    }

    bindMouseEvents(canvas) {
        canvas.onmousemove = (e) => {
            if (this.message) {
                var x = e.offsetX;
                var y = e.offsetY;

                this.mouseX = (x / this.zoom) + this.panX;
                this.mouseY = (y / this.zoom) + this.panY;
            }
        };

        canvas.onclick = (e) => {
            var containingShape = this.tiles.filter(e => e.containsPoint(this.mouseX, this.mouseY));
            if (containingShape.length >= 1) {
                var clickedShape = containingShape[0];
                if(this.buildMode){
                    send({
                        type: "BUILD",
                        tileToBuild: this.buildMode,
                        buildOnTileId: clickedShape.data.id,
                        playerName: this.playerName,
                        gameId: this.gameId
                    });
                }
            }
        };

        canvas.onmousewheel = (e)=> {
            var zoomIn = e.deltaY < 0;

            if (zoomIn) {
                this.zoom = Math.min(2, this.zoom + 0.1);
            } else {
                this.zoom = Math.max(0.5, this.zoom - 0.1);
            }
        };
    }

    panUp() {
        this.panY = Math.max(0, this.panY - (hexHeight + sideLength) / 2);
    }

    panLeft() {
        this.panX = Math.max(0, this.panX - hexRectangleWidth / 2);
    }

    panDown() {
        var maxDownPan = Math.max(0, this.MAP_HEIGHT_TILES * (hexHeight + sideLength) + hexHeight - this.SCREEN_HEIGHT_PIXELS / this.zoom);
        this.panY = Math.min(maxDownPan, this.panY + (hexHeight + sideLength) / 2);
    }

    panRight() {
        var maxRightPan = Math.max(0, (this.MAP_WIDTH_TILES + 0.5) * hexRectangleWidth - this.SCREEN_WIDTH_PIXELS / this.zoom);
        this.panX = Math.min(maxRightPan, this.panX + hexRectangleWidth / 2);
    }

    renderContext() {
        return this.canvas.getContext("2d");
    }

    render() {
        var ctx = this.renderContext();
        if (gameState) {
            ctx.fillStyle = "#ffffff";
            ctx.fillStyle = "#090909";
            ctx.fillRect(0, 0, this.SCREEN_WIDTH_PIXELS, this.SCREEN_HEIGHT_PIXELS);
            ctx.save();
            ctx.scale(this.zoom, this.zoom);
            ctx.translate(-this.panX, -this.panY);

            this.tiles.filter(e => e.containsPoint(this.mouseX, this.mouseY))
                .forEach(shape => shape.mouseIsOver());

            this.tiles.forEach((t)=> {
                t.render(ctx);
            });
            ctx.restore();
        }
    }
}