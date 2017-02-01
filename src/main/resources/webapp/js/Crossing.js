class Crossing {
    constructor(d){
        this.data = d;
    }
    
    render(context) {
        context.save();
        context.translate(this.data.x*100,this.data.y*100);
        context.fillStyle = "rgba(0,0,0,0.8)";
        context.strokeStyle = "rgba(250,250,250,0.55)";
        context.beginPath();
        context.arc(0,0,15,0,2 * Math.PI);
        context.fill();
        context.stroke();
        context.closePath();
        context.restore();
    }
}