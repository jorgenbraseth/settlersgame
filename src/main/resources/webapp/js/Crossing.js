
const WIDTH = 30;
const HEIGHT = 30;

class Crossing {
    constructor(d) {
        this.data = d;

        this.width = WIDTH;
        this.height = HEIGHT;
        this.x = this.data.x * TILE_SIZE - this.width / 2;
        this.y = this.data.y * TILE_SIZE - this.height / 2;
    }

    render(context) {
        context.save();
        context.translate(this.data.x * TILE_SIZE, this.data.y * TILE_SIZE);
        context.fillStyle = "rgba(0,0,0,0.8)";
        context.strokeStyle = "rgba(250,250,250,0.55)";

        if (this.isMouseOver) {
            context.fillStyle = "rgba(0,150,150,0.9)";
        }

        context.beginPath();
        context.arc(0, 0, this.width/2, 0, 2 * Math.PI);
        context.fill();
        context.stroke();
        context.closePath();
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