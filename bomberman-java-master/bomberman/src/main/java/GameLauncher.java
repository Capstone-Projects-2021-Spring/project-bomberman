import util.ResourceCollection;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains the main method to launch the game.
 */
public class GameLauncher {

    // The one and only window for the game to run
    static GameWindow window;

    public static String mapBlock = "";
    public static String[][] map;
    public static Boolean spawn1Set = false;
    public static Boolean spawn2Set = false;
    public static Boolean spawn3Set = false;
    public static Boolean spawn4Set = false;

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        // Create main menu frame
        JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        // Create buttons for main menu
        JButton singleButton = new JButton("Single Player");
        // JButton multiButton = new JButton("Multiplayer");
        JButton localButton = new JButton("Local Multiplayer");
        JButton tutorialButton = new JButton("Tutorial");
        JButton mapManagerButton = new JButton("Map Manager");
        JButton mapCreatorButton = new JButton("Map Creator");

        // Create main menu panel
        JPanel mainMenuPanel = new JPanel();

        // Add main menu buttons to main menu panel
        mainMenuPanel.add(singleButton);
        // mainMenuPanel.add(multiButton);
        mainMenuPanel.add(localButton);
        mainMenuPanel.add(tutorialButton);
        mainMenuPanel.add(mapManagerButton);
        mainMenuPanel.add(mapCreatorButton);

        // Initalize sound files
        String menuMusicPath = "./src/main/resources/awesomeness.wav";
        String singleMusicPath = "./src/main/resources/solo.wav";
        String localMusicPath = "./src/main/resources/local.wav";
        String tutorialMusicPath = "./src/main/resources/tutorial.wav";
        String mapManagerMusicPath = "./src/main/resources/mapmanager.wav";
        String mapCreatorMusicPath = "./src/main/resources/mapmaker.wav";
        File menuMusic = new File(menuMusicPath);
        File singleMusic = new File(singleMusicPath);
        File localMusic = new File(localMusicPath);
        File tutorialMusic = new File(tutorialMusicPath);
        File mapManagerMusic = new File(mapManagerMusicPath);
        File mapCreatorMusic = new File(mapCreatorMusicPath);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(menuMusic);
        AudioInputStream audioIn_s = AudioSystem.getAudioInputStream(singleMusic);
        AudioInputStream audioIn_l = AudioSystem.getAudioInputStream(localMusic);
        AudioInputStream audioIn_t = AudioSystem.getAudioInputStream(tutorialMusic);
        AudioInputStream audioIn_mm = AudioSystem.getAudioInputStream(mapManagerMusic);
        AudioInputStream audioIn_mc = AudioSystem.getAudioInputStream(mapCreatorMusic);
        AudioFormat format = audioIn.getFormat();
        AudioFormat format_s = audioIn_s.getFormat();
        AudioFormat format_l = audioIn_l.getFormat();
        AudioFormat format_t = audioIn_t.getFormat();
        AudioFormat format_mm = audioIn_mm.getFormat();
        AudioFormat format_mc = audioIn_mc.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        DataLine.Info info_s = new DataLine.Info(Clip.class, format_s);
        DataLine.Info info_l = new DataLine.Info(Clip.class, format_l);
        DataLine.Info info_t = new DataLine.Info(Clip.class, format_t);
        DataLine.Info info_mm = new DataLine.Info(Clip.class, format_mm);
        DataLine.Info info_mc = new DataLine.Info(Clip.class, format_mc);
        Clip clip = (Clip) AudioSystem.getLine(info);
        Clip clipSingle = (Clip) AudioSystem.getLine(info_s);
        Clip clipLocal = (Clip) AudioSystem.getLine(info_l);
        Clip clipTutorial = (Clip) AudioSystem.getLine(info_t);
        Clip clipMapManager = (Clip) AudioSystem.getLine(info_mm);
        Clip clipMapCreator = (Clip) AudioSystem.getLine(info_mc);
        clip.open(audioIn);
        clipSingle.open(audioIn_s);
        clipLocal.open(audioIn_l);
        clipTutorial.open(audioIn_t);
        clipMapManager.open(audioIn_mm);
        clipMapCreator.open(audioIn_mc);

        // Turns on menu music
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);

        // Event for start button press
        localButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                /*
                 * clip.stop(); clipLocal.start(); clipLocal.loop(Clip.LOOP_CONTINUOUSLY);
                 */
                ResourceCollection.readFiles();
                ResourceCollection.init();

                JFrame frame2 = new JFrame();
                String mapname = JOptionPane.showInputDialog(frame2, "Map Name", "Map Name", JOptionPane.PLAIN_MESSAGE);

                GamePanel game;
                try {
                    game = new GamePanel("../Maps/" + mapname);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(e + ": Program args not given");
                    game = new GamePanel(null);
                }

                game.init();
                window = new GameWindow(game);

                System.gc();
            }
        });

        singleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // clip.stop();
                // clipSingle.start();
                // clipSingle.loop(Clip.LOOP_CONTINUOUSLY);
                S_A_M_G_1.rand_map_10();
                GamePanel game;
                String singlePlayer = "single";
                ResourceCollection.readFileSingle(); // dont need this method when loading next map
                ResourceCollection.initSingle(); // dont need this method when making next map
                game = new GamePanel(singlePlayer, 1); // mapphase 1
                game.initSingle();
                window = new GameWindow(game);
                System.gc();
            }
        });

        // multiButton.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent ae) {
        // bomberClient client = new bomberClient();
        // //frame settings
        // client.frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // client.frame2.setVisible(true);
        // //start client
        // try {
        // client.run();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        // });

        tutorialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // clip.stop();
                // clipTutorial.start();
                // clipTutorial.loop(Clip.LOOP_CONTINUOUSLY);
                ResourceCollection.readFiles();
                ResourceCollection.init();

                GamePanel game;
                try {
                    game = new GamePanel("../Maps/tutorialpart1.csv");
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(e + ": Program args not given");
                    game = new GamePanel(null);
                }

                game.tutorial_init();
                window = new GameWindow(game);
                window.tutorial();

                System.gc();
            }
        });

        // Event for map manager button press
        mapManagerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // clip.stop();
                // clipMapManager.start();
                // clipMapManager.loop(Clip.LOOP_CONTINUOUSLY);
                // Create AWS Objects
                BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATZZ6LHXNIHI6PCWU",
                        "nJNomgXnz/C8W2m5ma7p1Os1s4F2ygvlnQontDCK");
                final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion("us-east-2").build();

                // Create Frame
                JFrame frame = new JFrame("Bomber Man");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);
                // Get the map names
                final File folder = new File("../maps");
                ArrayList<String> maps = getMapFiles(folder);
                DefaultListModel model = new DefaultListModel();
                JList<String> list = new JList(model);
                for (int x = 0; x < maps.size(); x++) {
                    if (maps.get(x).contains(".csv")) {
                        model.addElement(maps.get(x));
                    }
                }
                list.setModel(model);
                // Set styling for the list
                list.setBounds(80, 80, 50, 50);
                list.setLayoutOrientation(JList.VERTICAL);
                DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
                renderer.setHorizontalAlignment(SwingConstants.CENTER);
                // Create panel for the list
                JPanel listPanel = new JPanel();
                listPanel.add(new JScrollPane(list));
                // Add the panel to the frame
                frame.add(listPanel, BorderLayout.WEST);
                // Create Panel for button
                JPanel buttonPanel = new JPanel();
                // Create add map button
                JButton addMap = new JButton("Add Map");
                // Create action listener
                addMap.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        JFileChooser fileChooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.csv", "csv");
                        fileChooser.setFileFilter(filter);
                        int retVal = fileChooser.showOpenDialog(null);
                        if (retVal == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            try {
                                Path src = Paths.get(selectedFile.getPath());
                                Path dest = Paths.get("../maps/" + selectedFile.getName());
                                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                                model.addElement(selectedFile.getName());
                                list.setModel(model);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                // Make panels for download
                DefaultListModel modelDownload = new DefaultListModel();
                JList<String> listDownload = new JList(modelDownload);
                ArrayList<String> serverMaps = getServerMaps(s3);
                for (int y = 0; y < serverMaps.size(); y++) {
                    if (serverMaps.get(y).contains(".csv")) {
                        modelDownload.addElement(serverMaps.get(y));
                    }
                }
                listDownload.setModel(modelDownload);
                // Set styling for the list
                listDownload.setBounds(80, 80, 50, 50);
                listDownload.setLayoutOrientation(JList.VERTICAL);
                DefaultListCellRenderer renderer2 = (DefaultListCellRenderer) listDownload.getCellRenderer();
                renderer2.setHorizontalAlignment(SwingConstants.CENTER);

                JPanel listDownloadPanel = new JPanel();
                listDownloadPanel.add(listDownload);
                listDownloadPanel.setVisible(false);
                frame.add(listDownloadPanel, BorderLayout.NORTH);

                JPanel buttonPanel2 = new JPanel();
                // Create add map button
                JButton download = new JButton("Download");
                JButton back = new JButton("Back");

                buttonPanel2.add(download);
                buttonPanel2.add(back);
                buttonPanel2.setVisible(false);
                frame.add(buttonPanel2, BorderLayout.CENTER);

                back.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        listPanel.setVisible(true);
                        buttonPanel.setVisible(true);
                        listDownloadPanel.setVisible(false);
                        buttonPanel2.setVisible(false);
                    }
                });

                download.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String currentMap = listDownload.getSelectedValue();
                        if (!(currentMap == null)) {
                            String bucket = "bombermanmaps";
                            S3Object retMap = downloadMap(s3, bucket, currentMap);
                            InputStream reader = new BufferedInputStream(retMap.getObjectContent());
                            File file = new File("../Maps/" + currentMap);
                            OutputStream writer = null;
                            try {
                                writer = new BufferedOutputStream(new FileOutputStream(file));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            int read = -1;

                            try {
                                while ((read = reader.read()) != -1) {
                                    writer.write(read);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                writer.flush();
                                writer.close();
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            model.addElement(currentMap);
                        }
                    }
                });

                JButton uploadMap = new JButton("Upload Map");
                uploadMap.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String currentMap = list.getSelectedValue();
                        if (!(currentMap == null)) {
                            String currentMapPath = "../maps/" + list.getSelectedValue();
                            String bucket = "bombermanmaps";
                            uploadMap(s3, bucket, currentMap, currentMapPath);
                            modelDownload.addElement(currentMap);
                        }
                    }
                });
                JButton downloadMap = new JButton("Download Map");
                downloadMap.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        listPanel.setVisible(false);
                        buttonPanel.setVisible(false);
                        listDownloadPanel.setVisible(true);
                        buttonPanel2.setVisible(true);
                    }
                });
                JButton deleteMap = new JButton("Delete");
                deleteMap.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String currentMap = list.getSelectedValue();
                        if (!(currentMap == null)) {
                            String currentMapPath = "../maps/" + list.getSelectedValue();
                            File fileToDelete = new File(currentMapPath);
                            fileToDelete.delete();
                            model.removeElement(currentMap);
                        }
                    }
                });
                JButton back2 = new JButton("Back");
                back2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        frame.setVisible(false);
                    }
                });

                GridLayout blockTypeLayout = new GridLayout(0, 5);
                buttonPanel.setLayout(blockTypeLayout);

                buttonPanel.add(addMap);
                buttonPanel.add(uploadMap);
                buttonPanel.add(downloadMap);
                buttonPanel.add(deleteMap);
                buttonPanel.add(back2);

                // Add panel to the frame
                frame.add(buttonPanel, BorderLayout.SOUTH);
            }
        });

        // Event for map creator button press
        mapCreatorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                clip.stop();
                clipMapCreator.start();
                clipMapCreator.loop(Clip.LOOP_CONTINUOUSLY);

                JFrame frame2 = new JFrame();
                String mapsizes = JOptionPane.showInputDialog(frame2, "Map Size",
                        "Please enter the map size as rows,columns", JOptionPane.PLAIN_MESSAGE);
                String[] mapsizes2 = mapsizes.split(",");
                int xsize = Integer.parseInt(mapsizes2[0]);
                int ysize = Integer.parseInt(mapsizes2[1]);
                map = new String[xsize][ysize];
                // Create Frame
                JFrame frame = new JFrame("Bomber Man");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);

                // Configure Gridlayout
                GridLayout mapGridLayout = new GridLayout(xsize, ysize);

                // Create Map Panel
                JPanel mapPanel = new JPanel();

                // Set the layout for the panel
                mapPanel.setLayout(mapGridLayout);

                // Create buttons for the map panel
                for (int i = 0; i < xsize; i++) {
                    for (int j = 0; j < ysize; j++) {

                        if (i == 0 || j == 0 || i == xsize - 1 || j == ysize - 1) {
                            JButton temp = new JButton("H");
                            // Add the button with listener to grid panel
                            mapPanel.add(temp);
                            map[i][j] = "H";
                        } else {
                            // Create a button for each grid block
                            JButton temp = new JButton("");

                            // Store Cordinates in button
                            temp.setActionCommand("" + i + "," + j);
                            // Add event listener to update to the type of grid block
                            // the user wants
                            temp.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent ae) {
                                    // Check to make sure they can only set
                                    // one of each spawn point
                                    if (mapBlock.equals("1")) {
                                        if (temp.getText().equals("1")) {
                                            spawn1Set = false;
                                        } else if (temp.getText().equals("2")) {
                                            spawn2Set = false;
                                        } else if (temp.getText().equals("3")) {
                                            spawn3Set = false;
                                        } else if (temp.getText().equals("4")) {
                                            spawn4Set = false;
                                        }

                                        if (!spawn1Set) {
                                            spawn1Set = true;
                                        } else {
                                            return;
                                        }
                                    } else if (mapBlock.equals("2")) {
                                        if (temp.getText().equals("1")) {
                                            spawn1Set = false;
                                        } else if (temp.getText().equals("2")) {
                                            spawn2Set = false;
                                        } else if (temp.getText().equals("3")) {
                                            spawn3Set = false;
                                        } else if (temp.getText().equals("4")) {
                                            spawn4Set = false;
                                        }

                                        if (!spawn2Set) {
                                            spawn2Set = true;
                                        } else {
                                            return;
                                        }
                                    } else if (mapBlock.equals("3")) {
                                        if (temp.getText().equals("1")) {
                                            spawn1Set = false;
                                        } else if (temp.getText().equals("2")) {
                                            spawn2Set = false;
                                        } else if (temp.getText().equals("3")) {
                                            spawn3Set = false;
                                        } else if (temp.getText().equals("4")) {
                                            spawn4Set = false;
                                        }

                                        if (!spawn3Set) {
                                            spawn3Set = true;
                                        } else {
                                            return;
                                        }
                                    } else if (mapBlock.equals("4")) {
                                        if (temp.getText().equals("1")) {
                                            spawn1Set = false;
                                        } else if (temp.getText().equals("2")) {
                                            spawn2Set = false;
                                        } else if (temp.getText().equals("3")) {
                                            spawn3Set = false;
                                        } else if (temp.getText().equals("4")) {
                                            spawn4Set = false;
                                        }

                                        if (!spawn4Set) {
                                            spawn4Set = true;
                                        } else {
                                            return;
                                        }
                                    } else {
                                        if (temp.getText().equals("1")) {
                                            spawn1Set = false;
                                        } else if (temp.getText().equals("2")) {
                                            spawn2Set = false;
                                        } else if (temp.getText().equals("3")) {
                                            spawn3Set = false;
                                        } else if (temp.getText().equals("4")) {
                                            spawn4Set = false;
                                        }
                                    }

                                    // Set the block type
                                    temp.setText(mapBlock);

                                    // Get cordinates
                                    String location = temp.getActionCommand();
                                    String[] cordinates = location.split(",");
                                    int x = Integer.parseInt(cordinates[0]);
                                    int y = Integer.parseInt(cordinates[1]);

                                    // Update the map
                                    map[x][y] = mapBlock;
                                }
                            });
                            // Add the button with listener to grid panel
                            mapPanel.add(temp);
                        }
                    }
                }

                // Panel for user buttons
                JPanel blockTypePanel = new JPanel();

                // Configure GridLayour
                GridLayout blockTypeLayout = new GridLayout(2, 14);

                // Set the layout for the panel
                blockTypePanel.setLayout(blockTypeLayout);

                // Create button for firm wall
                JButton firmWall = new JButton("Hard Wall");
                firmWall.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "H";
                    }
                });

                // Create button for soft wall
                JButton softWall = new JButton("Soft Wall");
                softWall.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "S";
                    }
                });

                // Create button for spawn point 1
                JButton spawnOne = new JButton("Spawn 1");
                spawnOne.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "1";
                    }
                });

                // Create button for spawn point 2
                JButton spawnTwo = new JButton("Spawn 2");
                spawnTwo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "2";
                    }
                });

                // Create button for spawn point 3
                JButton spawnThree = new JButton("Spawn 3");
                spawnThree.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "3";
                    }
                });

                // Create button for spawn point 4
                JButton spawnFour = new JButton("Spawn 4");
                spawnFour.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "4";
                    }
                });

                // Create button for enemy balloon
                JButton enemyBallon = new JButton("Enemy");
                enemyBallon.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "EB";
                    }
                });

                JButton PB = new JButton("PB");
                PB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PB";
                    }
                });

                JButton PU = new JButton("PU");
                PU.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PU";
                    }
                });

                JButton PM = new JButton("PM");
                PM.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PM";
                    }
                });

                JButton PS = new JButton("PS");
                PS.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PS";
                    }
                });

                JButton PP = new JButton("PP");
                PP.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PP";
                    }
                });

                JButton PK = new JButton("PK");
                PK.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PK";
                    }
                });

                JButton PT = new JButton("PT");
                PT.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        mapBlock = "PT";
                    }
                });

                // Add the buttons to the panel
                blockTypePanel.add(firmWall);
                blockTypePanel.add(softWall);
                blockTypePanel.add(spawnOne);
                blockTypePanel.add(spawnTwo);
                blockTypePanel.add(spawnThree);
                blockTypePanel.add(spawnFour);
                blockTypePanel.add(enemyBallon);
                blockTypePanel.add(PB);
                blockTypePanel.add(PU);
                blockTypePanel.add(PM);
                blockTypePanel.add(PS);
                blockTypePanel.add(PP);
                blockTypePanel.add(PK);
                blockTypePanel.add(PT);

                // Panel for map name and submission
                JPanel submitPanel = new JPanel();

                // Create input and submit button
                JTextField mapName = new JTextField(15);
                JButton submitButton = new JButton("Create");
                submitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String name = mapName.getText();
                        try {
                            FileWriter writer = new FileWriter("../maps/" + name + ".csv");
                            for (int i = 0; i < xsize; i++) {
                                for (int j = 0; j < ysize; j++) {
                                    String currentTile = map[i][j];
                                    if (currentTile == "H") {
                                        writer.write("H,");
                                    } else if (currentTile == "S") {
                                        writer.write("S,");
                                    } else if (currentTile == "1") {
                                        writer.write("1,");
                                    } else if (currentTile == "2") {
                                        writer.write("2,");
                                    } else if (currentTile == "3") {
                                        writer.write("3,");
                                    } else if (currentTile == "4") {
                                        writer.write("4,");
                                    } else if (currentTile == "EB") {
                                        writer.write("EB,");
                                    } else {
                                        writer.write("-1,");
                                    }
                                }
                                writer.write("\n");
                            }
                            writer.close();
                            // Clear Variables in case they want to make another
                            map = new String[xsize][ysize];
                            spawn1Set = false;
                            spawn2Set = false;
                            spawn3Set = false;
                            spawn4Set = false;
                            frame.setVisible(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Add them to the pane;
                submitPanel.add(mapName);
                submitPanel.add(submitButton);

                // Add the panels to the frame
                frame.add(mapPanel, BorderLayout.NORTH);
                frame.add(blockTypePanel, BorderLayout.CENTER);
                frame.add(submitPanel, BorderLayout.SOUTH);
                frame.pack();
            }
        });
        frame.add(mainMenuPanel, BorderLayout.NORTH);
        frame.add(new JLabel(new ImageIcon("./bomberman.jpg")));
        frame.pack();
    }

    // Helper functions for map manager
    public static ArrayList<String> getMapFiles(final File directory) {
        ArrayList<String> files = new ArrayList<String>();
        for (final File fileEntry : directory.listFiles()) {
            files.add(fileEntry.getName());
        }

        game.init();
        window = new GameWindow(game);

        System.gc();
    }

}

/**
 * Game window that contains the game panel seen by the user.
 */
class GameWindow extends JFrame {

    /**
     * Screen width and height is determined by the map size. Map size is set when
     * loading the map in the GamePanel class. For best results, do not use a map
     * that is smaller than the default map provided in resources.
     */

    static final int HUD_HEIGHT = 48; // Size of the HUD. The HUD displays score.
    static final String TITLE = "Bomberman by Brian Lai";

    /**
     * Constructs a game window with the necessary configurations.
     * 
     * @param game Game panel that will be contained inside the game window
     */
    GameWindow(GamePanel game) {
        this.setTitle(TITLE);
        this.setIconImage(ResourceCollection.Images.ICON.getImage());
        this.setLayout(new BorderLayout());
        this.add(game, BorderLayout.CENTER);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Called every second. Updates the FPS and Ticks counters and prints them to
     * the console with the current time.
     * 
     * @param fps   FPS counter
     * @param ticks Ticks counter
     */
    public void update(int fps, int ticks) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime time = LocalDateTime.now();
        System.out.println("[" + dtf.format(time) + "]" + " FPS: " + fps + ", Ticks: " + ticks);
        GameLauncher.window.setTitle(GameWindow.TITLE + " | " + "FPS: " + fps + ", Ticks: " + ticks);
    }

}
