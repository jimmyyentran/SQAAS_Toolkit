package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.RallyWrapper;
import com.sqasquared.toolkit.email.EmailGeneratorException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by jimmytran on 10/29/16.
 */
public class App extends Application {
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public static final String loginScreen = "login";
    public static final String loginScreenFile = "login.fxml";
    public static final String mainScreen = "main";
    public static final String mainScreenFile = "main.fxml";
    public static UserSession userSession;
    public static Stage stage;

    public void initialize() throws IOException {
        userSession = new UserSession();
        TreeAlgorithmInterface timeAlgorithm = new TimeAlgorithm();
        RallyManager rallyManager = new RallyManager();
        AppDirector appDirector = new AppDirector(userSession);
        new Loader().loadTemplates(userSession);

        rallyManager.setAlgorithm(timeAlgorithm);
        appDirector.setRallyManager(rallyManager);
        userSession.setAppDirector(appDirector);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();
        LOG.log(Level.FINE, "JavaFX starting stage");
        stage = primaryStage;

        ScreensController mainContainer = new ScreensController(getHostServices());

        mainContainer.loadScreen(App.loginScreen, App.loginScreenFile);
        mainContainer.loadScreen(App.mainScreen, App.mainScreenFile);

        if (userSession.isAPIKeySet() && userSession.isUserPreferencesValid()) {
            LOG.log(Level.FINE, "Initializing {0} screen", mainScreen);
            RallyWrapper.initialize();
            mainContainer.setScreen(App.mainScreen);
//            new Loader().loadUserSession(userSession);
            userSession.loadRallyTasks();
        } else {
            LOG.log(Level.FINE, "Initializing {0} screen", loginScreen);
            mainContainer.setScreen(App.loginScreen);
        }

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SQAAS Toolkit");
        primaryStage.show();
    }

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException, BackingStoreException {
        LOG.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        LOG.addHandler(handler);

        // Command line arguments
        CommandLine commandLine;
        Option option_noGui = Option.builder("ng").longOpt("nogui").numberOfArgs(1).hasArg().build();
        Option option_clearPreferences = Option.builder("c").longOpt("clear-preferences").build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(option_noGui);
        options.addOption(option_clearPreferences);

        try{
            commandLine = parser.parse(options, args);
            if(commandLine.hasOption("clear-preferences")){
                Preferences prop = Preferences.userNodeForPackage(UserSession.class);
                prop.clear();
            }

            if(commandLine.hasOption("nogui")){
                String str = commandLine.getOptionValue("nogui");
                userSession = new UserSession();
                Loader loader = new Loader();
                RallyWrapper.initialize();
//                loader.loadUserSession(userSession);
                userSession.loadRallyTasks();
                try {
                    String html = userSession.generateHtml(UserSession.SSU);
                    String to = userSession.getEmailTo(UserSession.SSU);
                    String cc = userSession.getEmailCC();
                    String subject = userSession.getEmailSubject();
                    System.out.println("html = " + html);
                    System.out.println("to = " + to);
                    System.out.println("cc = " + cc);
                    System.out.println("subject = " + subject);
                } catch (EmailGeneratorException e) {
                    e.printStackTrace();
                }
                return;
            }

            {
                String[] remainder = commandLine.getArgs();
                System.out.print("Remaining arguments: ");
                for (String argument : remainder)
                {
                    System.out.print(argument);
                    System.out.print(" ");
                }

                System.out.println();
            }
        } catch (ParseException e) {
            throw e;
        }

        launch(args);
    }

}
