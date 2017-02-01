package no.porqpine.settlersgame;

import no.porqpine.settlersgame.api.EventServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add a websocket to a specific path spec
        EventServlet gameStateServlet = new EventServlet();
        ServletHolder holderEvents = new ServletHolder("ws-events", gameStateServlet);
        context.addServlet(holderEvents, "/game-state/*");

        GameLogic gameState = GameLogic.GAME;
        Thread gameLoop = new Thread(gameState);
        gameLoop.start();


        try
        {
            server.start();
            server.dump(System.err);
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gameState.stop();
            try {
                server.join();
                gameLoop.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }
}
