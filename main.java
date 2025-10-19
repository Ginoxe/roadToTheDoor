import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.Vector;
import java.util.ArrayList;

class Cell {
    private int x, y;
    private boolean isFloor = false;
    private boolean isDoor = false;
    private boolean isRock = false;
    private boolean rockBroken = false;
    private boolean isWater = false;
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
    public Frame() {
        super("Layout Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new MyPanel());
        pack();

        setVisible(true);
    }

}

class MyPanel extends JPanel implements MouseMotionListener, MouseListener {
    private static final int GRID_SIZE = 700;
    private static final int CELL_SIZE = 50;
    private static final int CELL_NUM = GRID_SIZE / CELL_SIZE;

    // 현재 커서가 있는 칸의 좌표값 변수
    // 칸 * CELL_SIZE = 셀의 왼쪽 위 기준 실제 Frame 좌표값
    private int cellRow = -1;
    private int cellCol = -1;

    // 각 셀에 대한 상태 2차원 배열
    // 0: 기본
    // 1: 바닥만 존재
    // 2: 돌 존재 (자명히 바닥도 존재)
    // 3: 물이 지나는 중
    // 4: 문으로 이어짐 (자명히 바닥도 존재)
    // 5: 돌을 다시 부수는 상태 -> 돌을 빼는 repaint() 할 때 필요
    // 6: 물이 지나는

    private int[][] gridState = new int[GRID_SIZE/CELL_SIZE][GRID_SIZE/CELL_SIZE];
    private ArrayList<ArrayList<Cell>> cellState = new ArrayList<>();


    public MyPanel() {
        // 패널 안에서 MouseListener 이용하기 위해서.
        addMouseMotionListener(this);
        addMouseListener(this);
        for (int i = 0; i<GRID_SIZE/CELL_SIZE;i++) {
            cellState.add(new ArrayList<Cell>());
            for (int j = 0; j < GRID_SIZE/CELL_SIZE; j++) {
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
        // 그리드 생성
        g.setColor(new Color(0, 0, 0, 40)); // 색은 조금 더 투명하게 바꿔도 됨.. 자유
        for (int i=0;i<=700;i+=50) {
            g.drawLine(i, 0, i, 700);
        }

        for (int i=0;i<=700;i+=50) {
            g.drawLine(0, i, 700, i);
        }
        // 현재 커서가 위치한 셀 색칠
        if (cellRow != -1 && cellCol != -1) {
            g.setColor(new Color(68, 138, 255, 50));
            int xCoord = cellCol * CELL_SIZE;
            int yCoord = cellRow * CELL_SIZE;
            g.fillRect(xCoord+1, yCoord+1, CELL_SIZE-1, CELL_SIZE-1);
        }

        // 바닥 생성
        for (int i = 0; i < CELL_NUM; i++) {
            for (int j = 0; j < CELL_NUM; j++) {

                // switch (gridState[i][j]) {
                //     // 문 생성
                //     case 4:
                //         g.setColor(Color.RED);
                //         if (j == 0) 
                //         g.fillRect(0, i*CELL_SIZE + 1, 4, CELL_SIZE);
                //         else
                //         g.fillRect(GRID_SIZE - 4, i*CELL_SIZE, 4, CELL_SIZE);
                //         break;
                //     // 돌 생성
                //     case 2:
                //         g.setColor(new Color(0, 0, 0, 80));
                //         g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE, CELL_SIZE);
                //     // 돌 클릭하면 없애기
                //     case 5:
                //         g.setColor(Color.WHITE);
                //         g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE, CELL_SIZE);
                //     // 바닥 생성
                //     case 1:
                //         g.setColor(Color.BLACK);
                //         g.fillRect(j * CELL_SIZE + 1, (i+1) * CELL_SIZE - 2, CELL_SIZE, 3);
                // }
                if (cellState.get(i).get(j).isRock()) {
                    g.setColor(new Color(0, 0, 0, 80));
                    g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE-1, CELL_SIZE-1);
                }
                else if (cellState.get(i).get(j).rockBroken()) {
                    g.setColor(new Color(238, 238, 238));
                    g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE-1, CELL_SIZE-1);
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
                // if (gridState[i][j] == 4) {
                //     g.setColor(Color.RED);
                //     if (j == 0) 
                //         g.fillRect(0, i*CELL_SIZE + 1, 4, CELL_SIZE);
                //     else
                //         g.fillRect(GRID_SIZE - 4, i*CELL_SIZE, 4, CELL_SIZE);
                //     g.setColor(Color.BLACK);
                //     g.fillRect(j * CELL_SIZE + 1, (i+1) * CELL_SIZE - 2, CELL_SIZE, 3);
                // }
                // else if (gridState[i][j] == 2) {
                //     g.setColor(new Color(0, 0, 0, 80));
                //     g.fillRect(j*CELL_SIZE+1, i*CELL_SIZE+1, CELL_SIZE, CELL_SIZE);
                // }
                // if (gridState[i][j] == 1) {
                //     g.setColor(Color.BLACK);
                //     g.fillRect(j * CELL_SIZE + 1, (i+1) * CELL_SIZE - 2, CELL_SIZE, 3);
                // }
            }
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
        // if (gridState[tempRow][tempCol] == 1) {
        //     if (gridState[tempRow][tempCol] != 2)
        //         gridState[tempRow][tempCol] = 2;
        //     else
        //         gridState[tempRow][tempCol] = 5;
        // }
        Cell currentCell = cellState.get(tempRow).get(tempCol);
        if (currentCell.isFloor() && !currentCell.isDoor()) {
            currentCell.toggleRock();
            repaint();
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
}

class MapSelector {
    public static void getNewMap(ArrayList<ArrayList<Cell>> gridState) {
        int mapType = (int)Math.random();
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
}

public class main {
    public static void main(String[] args) {
        Frame frame = new Frame();

    }
}