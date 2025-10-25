import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

class Cell {
    private int x, y;
    private boolean isFloor = false;
    private boolean isDoor = false;
    private boolean isRock = false;
    private boolean rockBroken = false;
    private boolean isWater = false;
    private int waterCount = 0;
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setFloor() {
        isFloor = true;
    }
    public void setDoor() {
        isFloor = true;
        isDoor = true;
    }
    public void setWater() {
        isWater = true;
    }
    public void removeWater() {
        isWater = false;
    }
    public void toggleRock() {
        if (isFloor) {
            if (isRock) {
                isRock = false;
                rockBroken = true;
            }
            else
                isRock = true;
        }
    }
    public void resetCell() {
        rockBroken = false;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public boolean isFloor() {
        return isFloor;
    }
    public boolean isDoor() {
        return isDoor;
    }
    public boolean isRock() {
        return isRock;
    }
    public boolean rockBroken() {
        return rockBroken;
    }
    public boolean isWater() {
        return isWater;
    }
}

class Frame extends JFrame {
    public Frame(MyPanel panel) {
        super("Layout Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}

class MyPanel extends JPanel implements MouseMotionListener, MouseListener {
    static final int GRID_SIZE = 700;
    static final int CELL_SIZE = 50;
    static final int CELL_NUM = GRID_SIZE / CELL_SIZE;

    // 현재 커서가 있는 칸의 좌표값 변수
    // 칸 * CELL_SIZE = 셀의 왼쪽 위 기준 실제 Frame 좌표값
    private int cellRow = -1;
    private int cellCol = -1;

    protected ArrayList<ArrayList<Cell>> cellState = new ArrayList<>();


    public MyPanel() {
        // 패널 안에서 MouseListener 이용하기 위해서.
        addMouseMotionListener(this);
        addMouseListener(this);
        for (int i = 0; i < CELL_NUM;i++) {
            cellState.add(new ArrayList<Cell>());
            for (int j = 0; j < CELL_NUM; j++) {
                cellState.get(i).add(new Cell(j, i));
            }
        }
        MapSelector.getNewMap(cellState);
    }

    public Dimension getPreferredSize() {
        return new Dimension(GRID_SIZE+1, GRID_SIZE+10);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        // 현재 커서가 위치한 셀 색칠
        if (cellRow != -1 && cellCol != -1) {
            g.setColor(new Color(68, 138, 255, 50));
            int xCoord = cellCol * CELL_SIZE;
            int yCoord = cellRow * CELL_SIZE;
            g.fillRect(xCoord+1, yCoord+1, CELL_SIZE, CELL_SIZE);
        }
        
        // 각 셀마다 요소 생성
        for (int i = 0; i < CELL_NUM; i++) {
            for (int j = 0; j < CELL_NUM; j++) {
                // 돌 생성
                if (cellState.get(i).get(j).isRock()) {
                    g.setColor(new Color(0, 0, 0, 80));
                    g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE, CELL_SIZE);
                }
                // 돌 소멸
                else if (cellState.get(i).get(j).rockBroken()) {
                    g.setColor(new Color(238, 238, 238));
                    g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE, CELL_SIZE);
                    cellState.get(i).get(j).resetCell();
                    
                }
                // 문 생성
                if (cellState.get(i).get(j).isDoor()) {
                    g.setColor(Color.RED);
                    if (j == 0) 
                    g.fillRect(0, i*CELL_SIZE + 1, 4, CELL_SIZE);
                    else
                    g.fillRect(GRID_SIZE - 4, i*CELL_SIZE, 4, CELL_SIZE);
                }
                // 바닥 생성
                if (cellState.get(i).get(j).isFloor()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(j * CELL_SIZE + 1, (i+1) * CELL_SIZE - 2, CELL_SIZE, 3);
                }
                // 물 생성
                if (cellState.get(i).get(j).isWater()) {
                    g.setColor(Color.RED);
                    g.fillRect(j * CELL_SIZE + 13, i * CELL_SIZE + 13, CELL_SIZE/2, CELL_SIZE/2);
                }
            }
        }
        // 그리드 생성
        g.setColor(new Color(0, 0, 0, 40)); // 색은 조금 더 투명하게 바꿔도 됨.. 자유
        for (int i=0;i<=700;i+=50) {
            g.drawLine(i, 0, i, 700);
        }
    
        for (int i=0;i<=700;i+=50) {
            g.drawLine(0, i, 700, i);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // 실제 좌표 -> 셀 좌표
        int tempRow = e.getY() / CELL_SIZE;
        int tempCol = e.getX() / CELL_SIZE;
        
        // 커서가 있는 셀이 바뀌지 않으면 굳이 다시 그리지 않음.
        if (tempRow != cellRow || tempCol != cellCol) {
            cellRow = tempRow;
            cellCol = tempCol;
            
            repaint();
        }
    }

    // MouseMotionListener 인터페이스에서 무조건 구현해야 하는 메서드
    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {
        // 나가면 리셋
        cellRow = -1;
        cellCol = -1;
        repaint();
    }

    // MouseListener 인터페이스에서 무조건 구현해야 하는 메서드들
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {
        int tempRow = e.getY() / CELL_SIZE;
        int tempCol = e.getX() / CELL_SIZE;

        Cell currentCell = cellState.get(tempRow).get(tempCol);
        if (currentCell.isFloor() && !currentCell.isDoor()) {
            currentCell.toggleRock();
            repaint();
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
}

class FlowWater implements Runnable {
    int start;
    MyPanel panel;
    private final int maxLength = 6;
    private Queue<Cell> waterQueue = new LinkedList<>();

    public FlowWater(MyPanel panel, int start) {
        this.panel = panel;
        this.start = start;
    }
    
    public void run() {
        try {
            Thread.sleep(1000); 
            Cell currentHead = panel.cellState.get(0).get(start);
            currentHead.setWater();
            waterQueue.offer(currentHead);
            panel.repaint();
            Thread.sleep(100);
            boolean initialFall = true;
            int direction = 0;
            while(true) {
                int x = currentHead.getX();
                int y = currentHead.getY();
                if (currentHead.isFloor()) {
                    if (initialFall) {
                        direction = MapSelector.getLongDirection(panel.cellState, currentHead, MyPanel.CELL_NUM); // -1: 왼쪽, 0: 동일, 1: 오른쪽
                        // direction이 0이면 랜덤으로 1이나 -1로 배정
                        if (direction == 0) {
                            int random = (int)(Math.random()*2);
                            direction = random==0 ? 1 : -1;
                        }
                        initialFall = false;
                    }

                    if (x == 0 && direction == -1) {
                        direction = 1;
                    }
                    else if (x == MyPanel.CELL_NUM-1 && direction == 1) {
                        direction = -1;
                    }

                    if (direction == 1)
                        currentHead = panel.cellState.get(y).get(x+1);
                    else
                        currentHead = panel.cellState.get(y).get(x-1);

                }
                else {
                    initialFall = true;
                    if (y < MyPanel.CELL_NUM-1)
                        currentHead = panel.cellState.get(y+1).get(x);
                    else {
                        System.out.println("GAME OVER");
                        break;
                    }
                }
                if (waterQueue.size() == maxLength) {
                    Cell tail = waterQueue.poll();
                    tail.removeWater();
                }
                currentHead.setWater();
                waterQueue.offer(currentHead);
                System.out.println(waterQueue);
                panel.repaint();
                Thread.sleep(1000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("FlowWater END");
    }
}

class MapSelector {
    static int mapType;
    public static void getNewMap(ArrayList<ArrayList<Cell>> gridState) {
        mapType = (int)Math.random();
        if (mapType == 0) {
            for (int i = 5; i<10; i++) {
                gridState.get(2).get(i).setFloor();
            }
            for (int i = 9;i<14;i++) {
                gridState.get(4).get(i).setFloor();
            }
            for (int i = 2; i< 6;i++) {
                gridState.get(5).get(i).setFloor();
            }
            for (int i = 4; i < 10; i++) {
                gridState.get(7).get(i).setFloor();
            }
            for (int i = 3; i < 6; i++) {
                gridState.get(9).get(i).setFloor();
            }
            for (int i = 9; i < 12; i++) 
                gridState.get(9).get(i).setFloor();
            for (int i = 3; i < 11; i++)
                gridState.get(12).get(i).setFloor();
            for (int i = 0, j = 11; i < 3 && j < 14; i++, j++) {
                gridState.get(11).get(i).setFloor();
                gridState.get(11).get(j).setFloor();
            }
            for (int i = 0, j = 11; i < 3 && j < 14; i++, j++) {
                gridState.get(13).get(i).setFloor();
                gridState.get(13).get(j).setFloor();
            }
            gridState.get(11).get(0).setDoor();
            gridState.get(11).get(13).setDoor();
            gridState.get(13).get(0).setDoor();
            gridState.get(13).get(13).setDoor();
        }
    }
    public static int getLongDirection(ArrayList<ArrayList<Cell>> gridState, Cell currentCell, int cellNum) {
        int leftCounter = 0;
        int rightCounter = 0;
        int x = currentCell.getX();
        int y = currentCell.getY();
        for (int i = x; gridState.get(y).get(i).isFloor(); i++) {
            rightCounter++;
            if (i == cellNum-1) {
                break;
            }
        }
        for (int i = x; gridState.get(y).get(i).isFloor(); i--) {
            leftCounter++;
            if (i == 0) {
                break;
            }
        }
        if (rightCounter > leftCounter)
            return 1;
        else if (rightCounter < leftCounter)
            return -1;
        else
            return 0;
    }
}

public class main {
    public static void main(String[] args) {
        MyPanel mainPanel = new MyPanel();
        Frame frame = new Frame(mainPanel);
        Thread flow = new Thread(new FlowWater(mainPanel, 6));
        flow.start();
    }
}
