var hexagonAngle = 0.523598776, // 30 degrees in radians
    sideLength = TILE_SIZE;

var hexHeight = Math.sin(hexagonAngle) * sideLength;
var hexRadius = Math.cos(hexagonAngle) * sideLength;
var hexRectangleHeight = sideLength + 2 * hexHeight;
var hexRectangleWidth = 2 * hexRadius;

class Tile {

    constructor(data) {
        this.data = data;
        this.x = data.x * hexRectangleWidth + (data.y % 2 * hexRadius);
        this.y = data.y * (hexHeight + sideLength);
        this.type = data.type;
    }

    render(ctx) {
        if("BLOCKER" === this.type){
            ctx.fillStyle = "gray";
        }else{
            var fillGrade = this.data.pheromoneAmount/25;
            ctx.fillStyle = "rgba(110,200,110,"+ fillGrade+")";
        }

        ctx.save();
        ctx.translate(this.x, this.y);

        ctx.beginPath();
        ctx.moveTo(hexRadius, 0);
        ctx.lineTo(hexRectangleWidth, hexHeight);
        ctx.lineTo(hexRectangleWidth, hexHeight + sideLength);
        ctx.lineTo(hexRadius,  hexRectangleHeight);
        ctx.lineTo(0, sideLength + hexHeight);
        ctx.lineTo(0, hexHeight);
        ctx.closePath();

        ctx.fill();

        ctx.strokeStyle = "black";
        ctx.stroke();

        ctx.fillStyle = "green";

        if(this.data.type == "PRODUCER"){
            ctx.fillRect(
                hexRectangleWidth/2-hexRadius/2,
                hexRectangleHeight/2-hexRadius/2,
                hexRadius, hexRadius
            )
            ctx.fillStyle = "white";
        }

        if(this.data.type == "CONSUMER"){
            ctx.fillStyle = "RED";
            ctx.fillRect(
                hexRectangleWidth/2-hexRadius/2,
                hexRectangleHeight/2-hexRadius/2,
                hexRadius, hexRadius
            )
            ctx.fillStyle = "white";
        }

        // var text = this.data.pheromoneAmount;
        // if(this.data.type == "CONSUMER"){
        //     text = this.data.storedPheromone;
        // }
        // var textWidth = ctx.measureText(text).width;
        // ctx.fillText(text, hexRectangleWidth/2-textWidth/2, hexRectangleHeight/2);




        ctx.restore();
    }
}