package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.GithubConnection;
import com.sqasquared.toolkit.connection.RallyWrapper;
import com.sqasquared.toolkit.connection.TfsWrapper;
import com.sqasquared.toolkit.email.EmailGeneratorException;
import com.sqasquared.toolkit.ui.ScreensController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by jimmytran on 10/29/16.
 */
public class App extends Application {
    public static final String loginScreen = "login";
    public static final String loginScreenFile = "login.fxml";
    public static final String mainScreen = "main";
    public static final String mainScreenFile = "main.fxml";
    private static final Logger LOG = Logger.getLogger(App.class.getName());
    public static UserSession userSession;
    public static Stage stage;

    public static void initialize() throws IOException, URISyntaxException {
        userSession = new UserSession();

        //Algorithms
        TreeAlgorithmInterface timeAlgorithm = new TimeAlgorithm();
        TreeAlgorithmInterface containerAlgorithm = new ContainerAlgorithm();

        //Loaders
        RallyLoader rallyLoader = new RallyLoader();
        TfsLoader tfsLoader = new TfsLoader();

        //Managers
        RallyManager rallyManager = new RallyManager();
        TfsManager tfsManager = new TfsManager();
        FileResourceManager fileResourceManager = new FileResourceManager();

        AppDirector appDirector = new AppDirector(userSession);

        //Set up Rally
        rallyLoader.setObjectManager(rallyManager);
        rallyManager.setLoader(rallyLoader);
        rallyManager.setAlgorithm(timeAlgorithm);

        //Set up TFS
        tfsLoader.setObjectManager(tfsManager);
        tfsManager.setLoader(tfsLoader);
        tfsManager.setAlgorithm(containerAlgorithm);

        RallyWrapper.initialize();
        TfsWrapper.initialize();

        //Set up main app director
        appDirector.setRallyManager(rallyManager);
        appDirector.setTfsManager(tfsManager);
        appDirector.setFileResourceManager(fileResourceManager);
        appDirector.loadTemplates();
        userSession.setAppDirector(appDirector);
    }

    public static void main(String[] args) throws URISyntaxException,
            IOException, ParseException, BackingStoreException {
        try {
            InputStream configFile = UserSession.class.getResourceAsStream
                    ("/logger.properties");
            LogManager.getLogManager().readConfiguration(configFile);
        } catch (Exception e) {
            System.out.println("WARNING: Could not open configuration file");
            System.out.println("WARNING: Logging not configured (console " +
                    "output only)");
        }

        // Command line arguments
        CommandLine commandLine;
        Option option_noGui = Option.builder("ng").longOpt("nogui")
                .numberOfArgs(1).hasArg().build();
        Option option_clearPreferences = Option.builder("c").longOpt
                ("clear-preferences").build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(option_noGui);
        options.addOption(option_clearPreferences);

        commandLine = parser.parse(options, args);
        if (commandLine.hasOption("clear-preferences")) {
            Preferences prop = Preferences.userNodeForPackage(UserSession
                    .class);
            prop.clear();
        }

        if (commandLine.hasOption("nogui")) {
            String str = commandLine.getOptionValue("nogui");
            String template = null;
            if (str.toLowerCase().equals("ssu")) {
                template = UserSession.SSU;
            } else if (str.toLowerCase().equals("ssup")) {
                template = UserSession.SSUP;
            }
            userSession = new UserSession();
            RallyWrapper.initialize();
            initialize();
            userSession.loadRallyTasks();
            try {
                String html = userSession.generateHtml(template);
                String to = userSession.getEmailTo(template);
                String cc = userSession.getEmailCC();
                String subject = "";
                if (template.equals(UserSession.SSU)) {
                    subject = userSession.getEmailSubject();
                }
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
            for (String argument : remainder) {
                System.out.print(argument);
                System.out.print(" ");
            }

            System.out.println();
        }

        launch(args);
    }

    public void checkNewReleases() throws IOException {
        String currentVersion = "v1.2.1-alpha";
        String latestVersion = GithubConnection.getLastestRelease();
        LOG.log(Level.FINE, currentVersion);
        if (!currentVersion.equals(latestVersion)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New Release");
            alert.setHeaderText(String.format("New release: %1s", latestVersion));
            alert.setContentText("Please pull updates from the repository");
            alert.showAndWait();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();

        checkNewReleases();

        LOG.log(Level.FINE, "JavaFX starting stage");
        stage = primaryStage;


        ScreensController mainContainer = new ScreensController
                (getHostServices());

        mainContainer.loadScreen(App.loginScreen, App.loginScreenFile);
        mainContainer.loadScreen(App.mainScreen, App.mainScreenFile);

        if (userSession.isAPIKeySet() && userSession.isUserPreferencesValid()) {
            LOG.log(Level.FINE, "Initializing {0} screen", mainScreen);
            RallyWrapper.initialize();
            mainContainer.setScreen(App.mainScreen);
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

}
