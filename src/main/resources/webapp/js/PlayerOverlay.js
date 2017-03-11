class PlayerOverlay {

    render(ctx, players) {

        ctx.save();
        ctx.translate(20, 20);

        ctx.font = "16pt arial";
        players.forEach(p => {
            ctx.fillStyle = p.color;
            ctx.fillText(p.name, 0, 0);

            ctx.translate(ctx.measureText(p.name).width + 10, 0);
            ctx.fillStyle = "white";
            ctx.fillText(p.resources.resource, 0, 0);
            ctx.translate(-(ctx.measureText(p.name).width + 10), 20);
        });
        ctx.restore();
    }
}