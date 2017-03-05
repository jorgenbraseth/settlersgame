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
        return this.isInside([mouseX, mouseY], this.poly);
    }

    mouseIsOver() {
        this.hover = true;
    }

    mouseIsNotOver() {
        this.hover = false;
    }

    isInside(point, vs) {
        // ray-casting algorithm based on
        // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html

        var x = point[0], y = point[1];

        var inside = false;
        for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
            var xi = vs[i][0], yi = vs[i][1];
            var xj = vs[j][0], yj = vs[j][1];

            var intersect = ((yi > y) != (yj > y))
                && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    };


    render(ctx) {
        ctx.save();
        ctx.translate(this.x, this.y);

        if (this.hover) {
            ctx.fillStyle = "yellow";
        } else if (this.data.owner) {
            ctx.fillStyle = this.data.owner.color;
        } else {
            ctx.fillStyle = "rgba(250,250,250,0.1)";
        }


        ctx.beginPath();
        ctx.moveTo(hexRadius, 0);
        ctx.lineTo(hexRectangleWidth, hexHeight);
        ctx.lineTo(hexRectangleWidth, hexHeight + sideLength);
        ctx.lineTo(hexRadius, hexRectangleHeight);
        ctx.lineTo(0, sideLength + hexHeight);
        ctx.lineTo(0, hexHeight);
        ctx.closePath();

        ctx.fill();

        ctx.translate(hexRadius, hexRectangleHeight / 2);
        ctx.scale(0.9, 0.9);
        ctx.translate(-hexRadius, -hexRectangleHeight / 2);
        ctx.beginPath();
        ctx.moveTo(hexRadius, 0);
        ctx.lineTo(hexRectangleWidth, hexHeight);
        ctx.lineTo(hexRectangleWidth, hexHeight + sideLength);
        ctx.lineTo(hexRadius, hexRectangleHeight);
        ctx.lineTo(0, sideLength + hexHeight);
        ctx.lineTo(0, hexHeight);
        ctx.closePath();

        ctx.fillStyle = "black";
        ctx.fill();

        if ("BLOCKER" === this.type) {
            ctx.fillStyle = "rgba(250,250,250,0.1)";
        } else if (this.data.type == "PRODUCER") {
            ctx.fillStyle = "#0099ff";
        } else if (this.data.type == "CONSUMER") {
            ctx.fillStyle = "#aa0000";
        } else if (this.data.type == "OWNERSHIP_SPREADER") {
            ctx.fillStyle = this.data.owner.color;
        } else {
            var fillGrade = this.data.pheromoneAmount / 25;
            ctx.fillStyle = "rgba(0,50,250," + fillGrade + ")";
        }
        ctx.fill();

        if (this.data.type !== "PRODUCER") {
            ctx.fillStyle = "white";

            var text = this.data.pheromoneAmount;
            if (this.data.type == "CONSUMER") {
                text = this.data.storedPheromone;
            }
            var textWidth = ctx.measureText(text).width;
            if (text) {
                ctx.fillText(text, hexRectangleWidth / 2 - textWidth / 2, hexRectangleHeight / 2);
            }
        }

        ctx.restore();
    }
}