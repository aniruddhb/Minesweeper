import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Minesweeper {
    private JPanel[][] m_grid = new JPanel[15][15];
    private char[][] a_grid = new char[15][15];
    private int[][] state = new int[15][15];
    private char[][] f_grid = new char[15][15];
    private boolean[][] t_grid = new boolean[15][15];
    private boolean[][] a_touch = new boolean[15][15];
    private boolean hasMine = false;
    private boolean FIRST_TIME = true;
    private boolean rdd = false;
    private JFrame window;
    private JPanel mid_board, time;
    private JLabel time_done, mines_had;
    private JMenuBar menu_bar;
    private JMenu game, options, help;
    private JMenuItem new_game, exit, mine_amount, how_to_play, version;
    private JTextPane how_to_play_text, version_num;
    private JTextField mine_nums;
    private JScrollPane how_to_play_scroll;
    private static int MINE_NUM, TIME_COUNTER;
    private static BufferedImage p1, p2, p3, p4, p5, p6, p7, p8, pmine, pflag, pquestion, pspace, punpressed;
    private javax.swing.Timer timer;

    public void loadImg() {
        try {
            // reads the number pngs
            File f = new File("numbers\\1.png");
            p1 = ImageIO.read(f);
            File f2 = new File("numbers\\2.png");
            p2 = ImageIO.read(f2);
            File f3 = new File("numbers\\3.png");
            p3 = ImageIO.read(f3);
            File f4 = new File("numbers\\4.png");
            p4 = ImageIO.read(f4);
            File f5 = new File("numbers\\5.png");
            p5 = ImageIO.read(f5);
            File f6 = new File("numbers\\6.png");
            p6 = ImageIO.read(f6);
            File f7 = new File("numbers\\7.png");
            p7 = ImageIO.read(f7);
            File f8 = new File("numbers\\8.png");
            p8 = ImageIO.read(f8);
            File mine = new File("numbers\\mine.png");
            pmine = ImageIO.read(mine);
            File flag = new File("numbers\\flag.png");
            pflag = ImageIO.read(flag);
            File question = new File("numbers\\question.png");
            pquestion = ImageIO.read(question);
            File space = new File("numbers\\space.png");
            pspace = ImageIO.read(space);
            File unpressed = new File("numbers\\unpressed.png");
            punpressed = ImageIO.read(unpressed);
        } catch (Exception v) {
            // nada
        }
    }

    public void createGUI() {
        window = new JFrame("MineSweeper Program");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(200, 200, 480, 540);
        window.getContentPane().setLayout(new BorderLayout());

        // GUI ITEMS TO BE ADDED
        for (int i = 0; i < m_grid.length; i++) {
            for (int j = 0; j < m_grid[0].length; j++) {
                JPanel jp = new JPanel();
                jp.setBackground(Color.DARK_GRAY);
                jp.setBorder(new LineBorder(Color.BLACK, 1));
                jp.setPreferredSize(new Dimension(5, 5));
                m_grid[i][j] = jp;
                state[i][j] = 1;
            }
        }

        mid_board = new JPanel();
        mid_board.setLayout(new GridLayout(15, 15));
        mid_board.setBorder(new BevelBorder(BevelBorder.RAISED));

        for (int r = 0; r < m_grid.length; r++) {
            for (int c = 0; c < m_grid[0].length; c++) {
                mid_board.add(m_grid[r][c]);
            }
        }

        JPanel low_board_timer = new JPanel();
        low_board_timer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 0));
        time = new JPanel();
        time.setBorder(new TitledBorder("Time Elapsed"));
        time.setPreferredSize(new Dimension(120, 60));
        time.setLayout(new FlowLayout(FlowLayout.CENTER));
        time_done = new JLabel("0");
        time.add(time_done);
        JPanel mines = new JPanel();
        mines.setBorder(new TitledBorder("Mines"));
        mines.setPreferredSize(new Dimension(120, 60));
        mines.setLayout(new FlowLayout(FlowLayout.CENTER));
        mines_had = new JLabel("~");
        mines.add(mines_had);
        low_board_timer.add(time);
        low_board_timer.add(mines);
        JPanel sub = new JPanel();
        sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));
        sub.add(new Box.Filler(new Dimension(10, 20), new Dimension(10, 20), new Dimension(10, 20)));
        sub.add(low_board_timer);
        sub.add(new Box.Filler(new Dimension(10, 20), new Dimension(10, 20), new Dimension(10, 20)));

        window.getContentPane().add(new Box.Filler(new Dimension(10, 10), new Dimension(10, 10), new Dimension(10, 10)), BorderLayout.NORTH);
        window.getContentPane().add(sub, BorderLayout.SOUTH);
        window.getContentPane().add(new Box.Filler(new Dimension(10, 10), new Dimension(10, 10), new Dimension(10, 10)), BorderLayout.EAST);
        window.getContentPane().add(new Box.Filler(new Dimension(10, 10), new Dimension(10, 10), new Dimension(10, 10)), BorderLayout.WEST);
        window.getContentPane().add(mid_board, BorderLayout.CENTER);

        // menu stuff
        menu_bar = new JMenuBar();
        game = new JMenu("Game");
        options = new JMenu("Options");
        help = new JMenu("Help");
        new_game = new JMenuItem("New Game");
        exit = new JMenuItem("Exit");
        mine_amount = new JMenuItem("Set Mine Amt.");
        how_to_play = new JMenuItem("How To Play");
        version = new JMenuItem("About");

        // adds stuff to menus
        game.add(new_game);
        game.add(exit);
        options.add(mine_amount);
        help.add(how_to_play);
        help.add(version);
        menu_bar.add(game);
        menu_bar.add(options);
        menu_bar.add(help);
        window.setJMenuBar(menu_bar);

        // window setting stuff
        window.setResizable(false);
        window.setVisible(true);
        loadImg();
        TIME_COUNTER = 0;

        // registration of GUI elements
        myMouseListener m = new myMouseListener();
        for (int x = 0; x < m_grid.length; x++) {
            for (int y = 0; y < m_grid[0].length; y++) {
                m_grid[x][y].addMouseListener(m);
            }
        }
        myActionListener a = new myActionListener();
        how_to_play.addActionListener(a);
        version.addActionListener(a);
        mine_amount.addActionListener(a);
        new_game.addActionListener(a);
        exit.addActionListener(a);
        timer = new javax.swing.Timer(1000, a);
    }

    /* This class was the hardest for me to write, as I had to make a LOT of stylistic decisions about
     * my program. For example, unflagging a mine increments the mine counter, and my program forces
     * a user to select a mine amt. or start a new game so that they specify mine amt. before the game 
     * is actually played. In addition, the game will end if the user has flags on all mines, even if 
     * the user has other non-mine tiles flagged or questioned. These are style decisions I made about 
     * my GUI program.
     */
    private class myMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (hasMine) {
                    if (FIRST_TIME) {
                        timer.restart();
                        FIRST_TIME = false;
                    }
                    int x = 0;
                    int y = 0;
                    for (int r = 0; r < m_grid.length; r++) {
                        boolean isDone = false;
                        for (int c = 0; c < m_grid[0].length; c++) {
                            if (m_grid[r][c] == e.getSource()) {
                                x = r;
                                y = c;
                                isDone = true;
                                break;
                            }
                        }
                        if (isDone == true) {
                            break;
                        }
                    }

                    if (t_grid[x][y] == false) {
                        runGame(x, y);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please choose a number of mines!\nEither play a new game or choose a \nnumber of mines in the 

menu.");
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                // switch between images here
                int x = 0;
                int y = 0;

                for (int r = 0; r < m_grid.length; r++) {
                    boolean isDone = false;
                    for (int c = 0; c < m_grid[0].length; c++) {
                        if (m_grid[r][c] == e.getSource()) {
                            x = r;
                            y = c;
                            isDone = true;
                            break;
                        }
                    }
                    if (isDone == true) {
                        break;
                    }
                }

                Graphics g = m_grid[x][y].getGraphics();
                Graphics2D g2 = (Graphics2D)g;
                if (t_grid[x][y] == false) {
                    if (state[x][y] % 3 == 1) {
                        g2.setColor(m_grid[x][y].getBackground());
                        g2.fillRect(1, 0, 28, 24);
                        g2.drawImage(pflag, 1, 0, 28, 24, null, null);
                        f_grid[x][y] = 'F';
                        if (state[x][y] != 0) {
                            rdd = true;
                        }
                        state[x][y]++;
                    } else if (state[x][y] % 3 == 2) {
                        g2.setColor(m_grid[x][y].getBackground());
                        g2.fillRect(1, 0, 28, 24);
                        g2.drawImage(pquestion, 1, 0, 28, 24, null, null);
                        f_grid[x][y] = 'Q';
                        state[x][y]++;
                    } else if (state[x][y] % 3 == 0) {
                        g2.setColor(m_grid[x][y].getBackground());
                        g2.fillRect(1, 0, 28, 24);
                        f_grid[x][y] = ' ';
                        state[x][y]++;
                    }
                }
                boolean firsttouch = false;
                if (rdd) {
                    firsttouch = true;
                }
                if (a_touch[x][y] == false) {
                    a_touch[x][y] = true;
                    firsttouch = true;
                }
                if (f_grid[x][y] == 'F' && firsttouch == true) {
                    int counter = Integer.parseInt(mines_had.getText());
                    counter--;
                    if (counter <= 0) {
                        counter = 0;
                    }                    
                    if (counter >= MINE_NUM) {
                        counter = MINE_NUM;
                    }     
                    boolean won1 = checkWin();
                    if (won1) {
                        counter = 0;
                    }
                    mines_had.setText(counter + "");
                    if (counter == 0) {
                        boolean won = checkWin();
                        if (won) {
                            if (timer.isRunning()) {
                                timer.stop();
                            }
                            JOptionPane.showMessageDialog(null, "You won! \nThere are 0 mines left!");
                        }
                    }
                } else if (f_grid[x][y] != 'F' && f_grid[x][y] == 'Q') {
                    int counter = Integer.parseInt(mines_had.getText());
                    counter++;
                    if (counter < 0) {
                        counter = 0;
                    }                    
                    if (counter > MINE_NUM) {
                        counter = MINE_NUM;
                    }          
                    boolean won1 = checkWin();
                    if (won1) {
                        counter = 0;
                    }
                    mines_had.setText(counter + "");
                    if (counter == 0) {
                        boolean won = checkWin();
                        if (won) {
                            if (timer.isRunning()) {
                                timer.stop();
                            }
                            JOptionPane.showMessageDialog(null, "You won! \nThere are 0 mines left!");
                        }
                    }
                }
            }
        }
    }

    public boolean checkWin() {
        boolean win = true;
        for (int r = 0; r < f_grid.length; r++) {
            for (int c = 0; c < f_grid[0].length; c++) {
                if ((f_grid[r][c] != 'F' && a_grid[r][c] == 'M')) {
                    win = false;
                } else {

                }
            }
        }
        return win;
    }

    public void runGame(int x, int y) {
        if (a_grid[x][y] == 'M') {
            // draw mine on x, y of m_grid
            Graphics g = m_grid[x][y].getGraphics();
            g.drawImage(pmine, 1, 0, 28, 24, null, null);
            if (timer.isRunning()) {
                timer.stop();
            }
            for (int r = 0; r < t_grid.length; r++) {
                for (int c = 0; c < t_grid[0].length; c++) {
                    t_grid[r][c] = true;
                }
            }
            JOptionPane.showMessageDialog(null, "Game Over");
        } else if (a_grid[x][y] != 'M' && a_grid[x][y] != '-') {
            // program recursion stops
            Graphics g = m_grid[x][y].getGraphics();
            if (a_grid[x][y] == '1') {
                g.drawImage(p1, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '2') {
                g.drawImage(p2, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '3') {
                g.drawImage(p3, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '4') {
                g.drawImage(p4, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '5') {
                g.drawImage(p5, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '6') {
                g.drawImage(p6, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '7') {
                g.drawImage(p7, 1, 0, 28, 24, null, null);
            } else if (a_grid[x][y] == '8') {
                g.drawImage(p8, 1, 0, 28, 24, null, null);
            }
            t_grid[x][y] = true;
        } else if (a_grid[x][y] == '-') {
            // draw whatever on x, y of m_grid (whatever number is here)
            a_grid[x][y] = 'E';
            t_grid[x][y] = true;
            Graphics g = m_grid[x][y].getGraphics();
            g.drawImage(pspace, 1, 0, 28, 24, null, null);
            int[][] surr = {{-1, -1}, {-1, 1}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {1, 1}, {1, 0}};
            for (int[] sub : surr) {
                if (x + sub[0] < 0 || x + sub[0] > 14 || y + sub[1] < 0 || y + sub[1] > 14) {

                } else if ((x + sub[0] >= 0 && x + sub[1] <= 14 && y + sub[1] >= 0 && y + sub[1] <= 14) &&  a_grid[x + sub[0]][y + sub[1]] != 'E') {
                    runGame(x + sub[0], y + sub[1]);
                }
            }
        }
    }

    public void fillBoard() {
        Random rd = new Random();
        for (int mine_count = 0; mine_count < MINE_NUM; mine_count++) {
            int rand_x = rd.nextInt(15);
            int rand_y = rd.nextInt(15);
            if (a_grid[rand_x][rand_y] != 'M') {
                a_grid[rand_x][rand_y] = 'M';
            } else {
                mine_count--;
            }
        }
        countNeighborsOfWholeBoard();
    }

    public void resetBoard() {
        for (int r = 0; r < m_grid.length; r++) {
            for (int c = 0; c < m_grid[0].length; c++) {
                a_grid[r][c] = ' ';
                state[r][c] = 1;
                f_grid[r][c] = ' ';
                t_grid[r][c] = false;
            }
        }
    }

    public void countNeighborsOfWholeBoard() {
        for (int r = 0; r < a_grid.length; r++) {
            for (int c = 0; c < a_grid[0].length; c++) {
                if (a_grid[r][c] != 'M') {
                    int neighbors = count(r, c);
                    if (neighbors == 1) {
                        a_grid[r][c] = '1';
                    } else if (neighbors == 2) {
                        a_grid[r][c] = '2';
                    } else if (neighbors == 3) {
                        a_grid[r][c] = '3';
                    } else if (neighbors == 4) {
                        a_grid[r][c] = '4';
                    } else if (neighbors == 5) {
                        a_grid[r][c] = '5';
                    } else if (neighbors == 6) {
                        a_grid[r][c] = '6';
                    } else if (neighbors == 7) {
                        a_grid[r][c] = '7';
                    } else if (neighbors == 8) {
                        a_grid[r][c] = '8';
                    } else {
                        a_grid[r][c] = '-';
                    }
                }
            }
        }
    }

    public int count(int r, int c) {
        int count = 0;
        int[][] surr = {{-1, -1}, {-1, 1}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {1, 1}, {1, 0}};
        for (int[] sub : surr) {
            try {
                int new_r = r + sub[0];
                int new_c = c + sub[1];
                if (a_grid[new_r][new_c] == 'M') {
                    count++;
                }
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                // nada
            }
        }
        return count;
    }

    public void printChar() {
        for (int r = 0; r < m_grid.length; r++) {
            for (int c = 0; c < m_grid[0].length; c++) {
                System.out.print(a_grid[r][c]);
            }
            System.out.println();
        }
    }

    private class myActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == how_to_play) {
                try {
                    how_to_play_text = new JTextPane();
                    how_to_play_text.setPage(new URL("file:howtoplay.html"));
                } catch (IOException io) {
                    // do nothing
                }

                how_to_play_text.setPreferredSize(new Dimension(480, 480));
                how_to_play_text.setEditable(false);

                how_to_play_scroll = new JScrollPane(how_to_play_text);
                how_to_play_scroll.setPreferredSize(new Dimension(480, 480));

                JOptionPane.showMessageDialog(null, how_to_play_scroll, "How To Play", JOptionPane.PLAIN_MESSAGE, null);
            } else if (e.getSource() == version) {
                try {
                    version_num = new JTextPane();
                    version_num.setPage(new URL("file:about.html"));
                } catch (IOException io) {
                    // do nothing
                }

                version_num.setPreferredSize(new Dimension(150, 150));
                version_num.setEditable(false);

                JOptionPane.showMessageDialog(null, version_num, "About", JOptionPane.PLAIN_MESSAGE, null);
            } else if (e.getSource() == mine_amount) {
                if (timer.isRunning()) {
                    timer.stop();
                }
                TIME_COUNTER = 0;
                FIRST_TIME = true;
                hasMine = true;
                MINE_NUM = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter number of mines you wish to play with!"));
                while (MINE_NUM < 0 || MINE_NUM > 225) {
                    JOptionPane.showMessageDialog(null, "Invalid number of mines!");
                    MINE_NUM = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter number of mines you wish to play with!"));
                }
                window.invalidate();
                for (int r = 0; r < m_grid.length; r++) {
                    for (int c = 0; c < m_grid.length; c++) {
                        m_grid[r][c] = null;
                        JPanel newp = new JPanel();
                        newp.setBackground(Color.DARK_GRAY);
                        newp.setBorder(new LineBorder(Color.BLACK, 1));
                        newp.setPreferredSize(new Dimension(5, 5));
                        newp.addMouseListener(new myMouseListener());
                        m_grid[r][c] = newp;
                        state[r][c] = 1;
                    }
                }

                window.remove(mid_board);
                mid_board = null;
                mid_board = new JPanel();
                mid_board.setLayout(new GridLayout(15, 15));
                mid_board.setBorder(new BevelBorder(BevelBorder.RAISED));

                for (int i = 0; i < m_grid.length; i++) {
                    for (int j = 0; j < m_grid[0].length; j++) {
                        mid_board.add(m_grid[i][j]);
                    }
                }
                window.add(mid_board, BorderLayout.CENTER);
                time_done.setText("0");
                mines_had.setText(MINE_NUM + "");
                window.revalidate();
                resetBoard();
                fillBoard();
                JOptionPane.showMessageDialog(null, "Game will reset/start!");
            } else if (e.getSource() == new_game) {
                if (timer.isRunning()) {
                    timer.stop();
                }
                TIME_COUNTER = 0;
                FIRST_TIME = true;
                hasMine = true;
                MINE_NUM = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter number of mines you wish to play with!"));
                while (MINE_NUM < 0 || MINE_NUM > 225) {
                    JOptionPane.showMessageDialog(null, "Invalid number of mines!");
                    MINE_NUM = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter number of mines you wish to play with!"));
                }
                window.invalidate();
                for (int r = 0; r < m_grid.length; r++) {
                    for (int c = 0; c < m_grid.length; c++) {
                        m_grid[r][c] = null;
                        JPanel newp = new JPanel();
                        newp.setBackground(Color.DARK_GRAY);
                        newp.setBorder(new LineBorder(Color.BLACK, 1));
                        newp.setPreferredSize(new Dimension(5, 5));
                        newp.addMouseListener(new myMouseListener());
                        m_grid[r][c] = newp;
                        state[r][c] = 1;
                    }
                }

                window.remove(mid_board);
                mid_board = null;
                mid_board = new JPanel();
                mid_board.setLayout(new GridLayout(15, 15));
                mid_board.setBorder(new BevelBorder(BevelBorder.RAISED));

                for (int i = 0; i < m_grid.length; i++) {
                    for (int j = 0; j < m_grid[0].length; j++) {
                        mid_board.add(m_grid[i][j]);
                    }
                }
                window.add(mid_board, BorderLayout.CENTER);
                time_done.setText("0");
                mines_had.setText(MINE_NUM + "");
                window.revalidate();
                resetBoard();
                fillBoard();
                JOptionPane.showMessageDialog(null, "There are " + MINE_NUM + " mines on this board.");  
            } else if (e.getSource() == exit) {
                System.exit(0);
            } else if (e.getSource() == timer) {
                TIME_COUNTER++;
                time_done.setText(TIME_COUNTER + "");
            }
        }
    }

    public static void main(String[] args) {
        Minesweeper m = new Minesweeper();
        m.createGUI();
    }
}
