const EDGE_LENGTH = TILE_SIZE / 2;
const EDGE_WIDTH = TILE_SIZE/12;
class Edge {
    constructor(e) {
        this.data = e;

        if ("VERTICAL" === this.data.orientation) {
            this.width = EDGE_WIDTH;
            this.height = EDGE_LENGTH;
            this.x = e.x * TILE_SIZE - this.width / 2;
            this.y = e.y * TILE_SIZE + (TILE_SIZE - this.height) / 2;
        } else {
            this.width = EDGE_LENGTH;
            this.height = EDGE_WIDTH;
            this.x = e.x * TILE_SIZE + (TILE_SIZE - this.width) / 2;
            this.y = (e.y * TILE_SIZE) - this.height / 2;
        }
    }

    render(context) {
        context.save();
        context.translate(this.x, this.y);

        context.fillStyle = "rgba(0,0,0,0.9)";
        context.strokeStyle = "rgba(250,250,250,0.75)";

        if(this.data.owner){
            var owningPlayer = gameData.players.filter(p => p.name == this.data.owner)[0];
            context.fillStyle = owningPlayer.color;
        }

        if (this.isMouseOver) {
            context.fillStyle = "rgba(0,150,150,0.9)";
        }

        context.fillRect(0, 0, this.width, this.height);
        context.strokeRect(0, 0, this.width, this.height);


        context.restore();
    }

    mouseIsOver() {
        this.isMouseOver = true;
    }
    mouseIsNotOver() {
        this.isMouseOver = false;
    }

    containsPoint(x, y) {
        return (x >= this.x && x <= (this.x + this.width)) && (y >= this.y && y <= (this.y + this.height));
    }
}