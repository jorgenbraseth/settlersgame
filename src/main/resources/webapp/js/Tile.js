var hexagonAngle = 0.523598776, // 30 degrees in radians
    sideLength = TILE_SIZE;

var hexHeight = Math.sin(hexagonAngle) * sideLength;
var hexRadius = Math.cos(hexagonAngle) * sideLength;
var hexRectangleHeight = sideLength + 2 * hexHeight;
var hexRectangleWidth = 2 * hexRadius;

class Tile {

    constructor(data, buildMode, playerName, gameScreen) {
        this.gameScreen = gameScreen
        this.playerName = playerName;
        this.buildMode = buildMode;
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

    render(ctx) {
        ctx.save();
        ctx.translate(this.x, this.y);
        this.renderBorder(ctx);
        this.renderFill(ctx);
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

        if (this.hover && this.buildableForCurrentPlayer()) {
            ctx.fillStyle = "yellow";
        } else if (this.buildMode() && this.buildableForCurrentPlayer()) {
            ctx.fillStyle = `rgba(100,250,0,0.6)`;
        } else {
            ctx.fillStyle = bg;
        }

        ctx.beginPath();
        this.poly.forEach(point => {
            ctx.lineTo(point[0] - this.x, point[1] - this.y);
        });
        ctx.closePath();

        ctx.fill();
    }

    renderFill(ctx) {
        ctx.save();
        ctx.translate(hexRadius, hexRectangleHeight / 2);
        ctx.scale(0.95, 0.95);
        ctx.translate(-hexRadius, -hexRectangleHeight / 2);
        ctx.beginPath();
        this.poly.forEach(point => {
            ctx.lineTo(point[0] - this.x, point[1] - this.y);
        });
        ctx.closePath();

        ctx.fillStyle = "black";
        ctx.fill();

        if (this.data.owner) {
            var color = this.data.owner.color;
            ctx.fillStyle = hexToRGBA(color, 0.5);
        } else if (this.data.type == "PRODUCER") {
            ctx.fillStyle = TILE_TYPE_COLORS[this.data.type].background;
        } else if (this.data.resourcePheromones) {
            var fillGrade = this.data.resourcePheromones.resource / 500;
            ctx.fillStyle = `rgba(0,50,250,${fillGrade})`;


            ctx.globalAlpha = fillGrade;
            var fillSize = TILE_SIZE * Math.min(0.8, fillGrade);
            var grd = ctx.createRadialGradient(hexRectangleWidth / 2, hexRectangleHeight / 2, 0, hexRectangleWidth / 2, hexRectangleHeight / 2, fillSize);
            grd.addColorStop(0.000, 'rgba(255, 255, 255, 1.000)');
            grd.addColorStop(0.5, 'rgba(255, 255, 255, 1.000)');
            grd.addColorStop(1.000, 'rgba(0, 0, 0, 1.000)');

            ctx.fillStyle = grd;
        }
        ctx.fill();

        // if (this.buildMode() && this.buildableForCurrentPlayer()) {
        //     ctx.fillStyle = "rgba(100,250,0,0.1)";
        //     ctx.fill();
        // }

        ctx.restore();

    }

    buildableForCurrentPlayer() {
        return this.type === "FREE" && this.data.highestPheromonePlayer && this.data.highestPheromonePlayer.name == this.playerName;
    }

    renderText(ctx) {
        var icon = TILE_ICONS[this.data.type];
        if (icon) {
            ctx.save();
            ctx.translate(hexRadius, hexRectangleHeight / 2);
            if (this.data.health != undefined) {
                var healthLeftPercent = this.data.health / this.data.MAX_HEALTH;
                ctx.scale(healthLeftPercent, healthLeftPercent);
            }
            ctx.font = `${TILE_SIZE * 0.8}pt FontAwesome`;

            ctx.fillStyle = this.data.owner != undefined ? this.data.owner.color : TILE_TYPE_COLORS[this.data.type].foreground;
            ctx.fillText(icon, -ctx.measureText(icon).width / 2, TILE_SIZE * 0.8 / 2);
            ctx.restore();
        }


        if (this.hover && this.buildMode() && this.buildableForCurrentPlayer()) {
            ctx.save();
            ctx.translate(hexRadius, hexRectangleHeight / 2);
            ctx.font = `${TILE_SIZE * 0.8}pt FontAwesome`;
            ctx.fillStyle = this.gameScreen.player.color;
            var icon = TILE_ICONS[this.buildMode()];
            ctx.fillText(icon, -ctx.measureText(icon).width / 2, TILE_SIZE * 0.8 / 2);
            ctx.restore();
        }

    }
}