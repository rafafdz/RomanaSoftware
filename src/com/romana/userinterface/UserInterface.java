package com.romana.userinterface;

import com.romana.database.DatabaseException;
import com.romana.devices.CardDatabase;
import com.romana.devices.SerialException;
import com.romana.devices.SystemOperations;
import com.romana.devices.WeightInfo;
import com.romana.userinterface.ErrorMessagePanel.ErrorType;
import com.romana.userinterface.commonwidgets.InteractivePanel;
import com.romana.utilities.Configuration;
import com.romana.utilities.LoggerCreator;
import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author rafael
 */
public final class UserInterface extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(UserInterface.class.getName());
    private static final String ROOT_LOGGER_NAME = UserInterface.class.getPackage().getName();
    private static final String LOG_FILE = "log/userinterface.log";
    private static final Logger USER_LOGGER = LoggerCreator.create(UserInterface.ROOT_LOGGER_NAME,
            UserInterface.LOG_FILE,
            Level.INFO);

    private WeightInfo actualWeightInfo;
    private InteractivePanel actualPanel;

    private Style.StyledJPanel cardPanel = new Style.StyledJPanel();
    private CardLayout cards = new CardLayout();

    private HashMap<String, InteractivePanel> panels;
    private HeaderPanel headerPanel;

    private SystemOperations systemOperations;
    private CardDatabase cardDatabase;

    public UserInterface() {

        try {
            systemOperations = new SystemOperations();
            systemOperations.initializeAllDevices();
        } catch (SerialException ex) {
            LOGGER.log(Level.SEVERE, "Could not initialize all devices", ex);
            System.exit(1);
        }

        try {
            cardDatabase = new CardDatabase();
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Database initialization error", ex);
            System.exit(1);
        }

        initComponents();
    }

    private void initComponents() {

        panels = new HashMap<>();

        cardPanel = new Style.StyledJPanel();
        cards = new CardLayout();
        cardPanel.setLayout(cards);

        headerPanel = new HeaderPanel();

        addCard(new TypePanel());
        addCard(new PlateEntryPanel());
        addCard(new AxisEntryPanel());
        addCard(new SummaryPanel());
        addCard(new CardReadPanel());
        addCard(new MoveTruckPanel());
        addCard(new WeighingPanel());
        addCard(new ReceiptPanel());
        addCard(new ErrorMessagePanel());
        addCard(new NoMoneyPanel());
        addCard(new NotRegisteredPanel());
        addCard(new NoCardPanel());
        addCard(new NoVehiclePanel());
        addCard(new InvalidCardPanel());
        addCard(new FirstPhaseFinishedPanel());
        addCard(new PullTicketPanel());
        addCard(new TimeExceededPanel());

        setInterfacesToPanels();

        setLayout(new GridBagLayout());

        GridBagConstraints gridBagHeader = new GridBagConstraints();
        gridBagHeader.gridy = 0;
        gridBagHeader.fill = GridBagConstraints.HORIZONTAL;
        add(headerPanel, gridBagHeader);

        GridBagConstraints gridBagCardPanel = new GridBagConstraints();
        gridBagCardPanel.gridy = 1;
        gridBagCardPanel.fill = GridBagConstraints.BOTH;
        gridBagCardPanel.weightx = 1;
        gridBagCardPanel.weighty = 1;
        add(cardPanel, gridBagCardPanel);

        setGlobalListenerTimerRestart();
        showPanel(TypePanel.class); // Set initial card
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("RomanaSoftware | Cliente"); // Needed for wmctrl

        int x_size = Configuration.getIntConfig("SIZE_X");
        int y_size = Configuration.getIntConfig("SIZE_Y");

        setSize(x_size, y_size);

        if (Configuration.isDebugMode()) {
            WeightInfo weightMock = new WeightInfo(SystemOperations.WeightType.SIMPLE);
            weightMock.setTotalPrice(420);
            actualWeightInfo = weightMock;
            actualWeightInfo.addWeight(420);
//            showPanel(TimeExceededPanel.class);
        } else {
            hideCursor();
        }
    }

    private void hideCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        this.setCursor(blankCursor);
    }

    /**
     * Adds the current panel to the panels map and to the cardlayout.
     *
     * @param panel
     */
    private void addCard(InteractivePanel panel) {
        String panelName = panel.getClass().getName();
        panels.put(panelName, panel);
        cardPanel.add(panel, panelName);
    }

    private void setInterfacesToPanels() {
        InterfaceActionsImpl interfaceActions = new InterfaceActionsImpl();
        SystemActionsImpl systemActions = new SystemActionsImpl();

        for (InteractivePanel panel : panels.values()) {
            panel.setInterfaceActions(interfaceActions);
            panel.setSystemActions(systemActions);
        }

    }

    
    
    /**
     * Restarts timer for every click on screen
     */
    private void setGlobalListenerTimerRestart() {
        Toolkit.getDefaultToolkit().addAWTEventListener((AWTEvent event) -> {
            if (event instanceof MouseEvent) {
                MouseEvent evt = (MouseEvent) event;
                if (evt.getID() == MouseEvent.MOUSE_CLICKED) {
                    actualPanel.restartTimer();
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    public void showPanel(Class classOfT) {
        if (actualPanel != null) {
            actualPanel.hideAction();
        }
        InteractivePanel newPanel = getPanel(classOfT);
        actualPanel = newPanel;
        
        if(Configuration.isDebugMode()){
            newPanel.ShowDebugAction();
        } else {
            newPanel.showAction();
        }
        cards.show(cardPanel, classOfT.getName());
        LOGGER.log(Level.FINE, "Panel Switched to {0}", classOfT.getName());
    }

    public void setErrorMessage(ErrorType error) {
        ErrorMessagePanel errorPanel = (ErrorMessagePanel) getPanel(ErrorMessagePanel.class);
        errorPanel.setError(error);
        showPanel(ErrorMessagePanel.class);
    }

    public InteractivePanel getPanel(Class classOfT) {
        String panelName = classOfT.getName();
        return panels.get(panelName);
    }

    public InteractivePanel getActualPanel() {
        return actualPanel;
    }

    public class InterfaceActionsImpl implements InterfaceActions {

        @Override
        public HeaderPanel getHeader() {
            return headerPanel;
        }

        @Override
        public void switchPanel(Class classOfT) {
            showPanel(classOfT);
        }

        @Override
        public void getPlateEntryFocus() {
            PlateEntryPanel plateEntry = (PlateEntryPanel) getPanel(PlateEntryPanel.class);
            plateEntry.requestEntryFocus();

        }

        @Override
        public void getAxisEntryFocus() {
            AxisEntryPanel axisEntry = (AxisEntryPanel) getPanel(AxisEntryPanel.class);
            axisEntry.requestEntryFocus();
        }

        @Override
        public void setError(ErrorType error) {
            setErrorMessage(error);
        }

        @Override
        public void setNoMoney(int balance, int price) {
            NoMoneyPanel noMoneyPanel = (NoMoneyPanel) getPanel(NoMoneyPanel.class);
            noMoneyPanel.setNoMoneyValues(balance, price);
        }

    }

    public class SystemActionsImpl implements SystemActions {

        @Override
        public WeightInfo getActualWeightInfo() {
            return actualWeightInfo;
        }

        @Override
        public void setActualWeightInfo(WeightInfo info) {
            actualWeightInfo = info;
        }

        @Override
        public void returnToMainMenu() {
            actualWeightInfo = null;
            headerPanel.setSubtitleText(null, 0); // Clear plate. Could be a method of HeaderPanel
            showPanel(TypePanel.class);
        }

        @Override
        public CardDatabase getCardDatabase() {
            return cardDatabase;
        }

        @Override
        public SystemOperations getSystemOperations() {
            return systemOperations;
        }

    }

    // To do: Move interfaces to another place!
    public interface InterfaceActions {

        public HeaderPanel getHeader();

        public void switchPanel(Class classOfT);

        public void getPlateEntryFocus();

        public void getAxisEntryFocus();

        public void setError(ErrorType error);

        public void setNoMoney(int balance, int price);

    }

    public interface SystemActions {

        public WeightInfo getActualWeightInfo();

        public void setActualWeightInfo(WeightInfo info);

        public void returnToMainMenu();

        public SystemOperations getSystemOperations();

        public CardDatabase getCardDatabase();

    }

    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        ui.setVisible(true);
        //ui.showPanel(FirstPhaseFinishedPanel.class);
        //ui.showPanel(PlateEntryPanel.class);
    }

}
