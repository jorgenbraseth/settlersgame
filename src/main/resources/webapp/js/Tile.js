var hexagonAngle = 0.523598776, // 30 degrees in radians
    sideLength = TILE_SIZE;

var hexHeight = Math.sin(hexagonAngle) * sideLength;
var hexRadius = Math.cos(hexagonAngle) * sideLength;
var hexRectangleHeight = sideLength + 2 * hexHeight;
var hexRectangleWidth = 2 * hexRadius;


class Tile {

    constructor(data) {
        this.id = data.id;
        this.x = data.x * hexRectangleWidth + (data.y % 2 * hexRadius);
        this.y = data.y * (hexHeight + sideLength);
        this.type = data.type;
        this.data = data;

        this.poly = [
            [this.x + hexRadius, this.y],
            [this.x + hexRectangleWidth, this.y + hexHeight],
            [this.x + hexRectangleWidth, this.y + hexHeight + sideLength],
            [this.x + hexRadius, this.y + hexRectangleHeight],
            [this.x, this.y + hexHeight + sideLength],
            [this.x, this.y + hexHeight]
        ];

        this.hover = false;
    }

    containsPoint(mouseX, mouseY) {
        return isInside([mouseX, mouseY], this.poly);
    }

    mouseIsOver() {
        this.hover = true;
    }

    mouseIsNotOver() {
        this.hover = false;
    }

    render(ctx, player) {
        ctx.save();
        ctx.translate(this.x, this.y);
        this.renderBorder(ctx);
        this.renderFill(ctx, player);
        this.renderText(ctx);

        ctx.restore();
    }

    renderBorder(ctx) {
        var bg = "rgba(250,250,250,0.0)";
        if (this.data.highestPheromonePlayer) {
            var color = this.data.highestPheromonePlayer.color;
            var o = this.data.pheromoneLeadOfHighestPheromone / 500 + 0.15;
            bg = hexToRGBA(color, o);
        }

        if (this.hover && this.data.highestPheromonePlayer && this.data.highestPheromonePlayer.name == player.name) {
            ctx.fillStyle = "yellow";
        } else {
            ctx.fillStyle = bg;
        }

        ctx.beginPath();
        this.poly.forEach(point => {
            ctx.lineTo(point[0]-this.x, point[1]-this.y);
        });
        ctx.closePath();

        ctx.fill();
    }

    renderFill(ctx) {
        ctx.translate(hexRadius, hexRectangleHeight / 2);
        ctx.scale(0.9, 0.9);
        ctx.translate(-hexRadius, -hexRectangleHeight / 2);
        ctx.beginPath();
        this.poly.forEach(point => {
            ctx.lineTo(point[0]-this.x, point[1]-this.y);
        });
        ctx.closePath();

        ctx.fillStyle = "black";
        ctx.fill();

        if ("BLOCKER" === this.type) {
            ctx.fillStyle = "rgba(250,250,250,0.9)";
        } else if (this.data.type == "PRODUCER") {
            ctx.fillStyle = "#0099ff";
        } else if(this.data.resourcePheromones) {
            var fillGrade = this.data.resourcePheromones.resource / 25;
            ctx.fillStyle = "rgba(0,50,250," + fillGrade + ")";
        }
        ctx.fill();
    }

    renderText(ctx) {
        ctx.save();
        ctx.translate(hexRadius, hexRectangleHeight / 2);
        ctx.globalAlpha = 0.7;
        if(this.data.health != undefined){
            var healthLeftPercent = this.data.health / this.data.MAX_HEALTH;
            ctx.scale(healthLeftPercent, healthLeftPercent);
        }
        if (this.data.type === "OWNERSHIP_SPREADER") {
            ctx.beginPath();
            ctx.arc(0, 0, hexRadius * 0.8, 0, 2 * Math.PI);
            ctx.closePath();
            ctx.fillStyle = this.data.owner.color;
            ctx.fill();
            IMAGE_MAP.EMITTER.render(ctx, TILE_SIZE);
        } else if (this.data.type === "HOME") {
            ctx.beginPath();
            ctx.arc(0, 0, hexRadius * 0.7, 0, 2 * Math.PI);
            ctx.closePath();
            ctx.fillStyle = this.data.owner.color;
            ctx.fill();
            IMAGE_MAP.HOME.render(ctx, TILE_SIZE);
        } else if (this.data.type === "BLOCKER") {
            ctx.beginPath();
            ctx.arc(0, 0, hexRadius * 0.7, 0, 2 * Math.PI);
            ctx.closePath();
            ctx.fillStyle = this.data.owner.color;
            ctx.fill();
            IMAGE_MAP.WALL.render(ctx, TILE_SIZE * 0.7);
        } else if (this.data.type === "SIPHON") {
            ctx.beginPath();
            ctx.arc(0, 0, hexRadius * 0.7, 0, 2 * Math.PI);
            ctx.closePath();
            ctx.fillStyle = this.data.owner.color;
            ctx.fill();
            IMAGE_MAP.SIPHON.render(ctx, TILE_SIZE * 0.8);
        }
        ctx.restore();
    }
}