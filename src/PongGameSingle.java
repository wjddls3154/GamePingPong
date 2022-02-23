import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

public class PongGameSingle {

    static class MyFrame extends JFrame {

        // 그래픽을 그릴 캔버스 역할
        static class MyPanel extends JPanel {
            public MyPanel() {
                this.setSize(800, 400);
                this.setBackground(Color.BLACK);
            }

            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                g2d.fillRect(barLeftPos.x, barLeftPos.y, 20, barHeight); // 왼쪽 바 위치 및 크기
                g2d.fillRect(barRightPos.x, barRightPos.y, 20, barHeight); // 오른쪽 바 위치 및 크기
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 50));
                g2d.drawString(scoreLeft + " vs " + scoreRight, 400 - 50, 50);
                g2d.fillOval(ballPos.x, ballPos.y, 20, 20); // 공 위치 및 크기
            }

        }

        // 변수
        static Timer timer = null;
        static int dir = 0; // 0 - Up-Right, 1 - Down-Right, 2 - Up-Left , 3 - Down-Left  공의 방향
        static Point ballPos = new Point(400 - 10, 200 - 10); // 볼의 위치 넣기
        static int ballSpeed = 3; // 공의 속도, 타이머 한 틱당 2픽셀 이동
        static int ballWidth = 20;
        static int ballHeight = 20;
        static int barHeight = 80;
        static Point barLeftPos = new Point(50, 200 - 40); // 왼쪽 바의 위치값 저장
        static Point barRightPos = new Point(800 - 50 - 20, 200 - 40); // 오른쪽 바의 위치값 저장
        static int barLeftYTarget = barLeftPos.y; // 타겟 지점 목표값, 바를 부드럽게 움직이기위해
        static int barRightYTarget = barRightPos.y;
        static MyPanel myPanel = null;
        static int scoreLeft = 0;
        static int scoreRight = 0;

        // 생성자, 환경설정
        public MyFrame(String title) {
            super(title);
            this.setVisible(true);
            this.setSize(800, 400);
            this.setLayout(new BorderLayout());
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // myPanel 추가
            myPanel = new MyPanel();
            this.add("Center", myPanel);

            setKeyListener();
            startTimer();
        }

        // 키보드를 읽어서 움직임을 줌
        public void setKeyListener() {
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) { // 키보드 위방향키 를 누르면
                        System.out.println("Pressed Up key");
                        barLeftYTarget -= 10;
                        if (barLeftPos.y < barLeftYTarget) {
                            barLeftYTarget = barLeftPos.y - 10;
                        }
                        barRightYTarget -= 10;
                        if (barRightPos.y < barRightYTarget) {
                            barRightYTarget = barRightPos.y - 10;
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 키보드 아래 방향키 를 누르면
                        System.out.println("Pressed Down key");
                        barLeftYTarget += 10;
                        if (barLeftPos.y > barLeftYTarget) {
                            barLeftYTarget = barLeftPos.y + 10;
                        }
                        barRightYTarget += 10;
                        if (barRightPos.y > barRightYTarget) {
                            barRightYTarget = barRightPos.y + 10;
                        }
                    }
                }
            });
        }

        // 타이머 설정
        public void startTimer() {
            timer = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (barLeftPos.y > barLeftYTarget) {
                        barLeftPos.y -= 5;
                    } else if (barLeftPos.y < barLeftYTarget) {
                        barLeftPos.y += 5;
                    }
                    if (barRightPos.y > barRightYTarget) {
                        barRightPos.y -= 5;
                    } else if (barRightPos.y < barRightYTarget) {
                        barRightPos.y += 5;
                    }

                    if (dir == 0) { // 공의 방향, 0 - Up-Right
                        ballPos.x += ballSpeed;
                        ballPos.y -= ballSpeed;
                    } else if (dir == 1) { // 1 - Down-Right
                        ballPos.x += ballSpeed;
                        ballPos.y += ballSpeed;
                    } else if (dir == 2) { // 2 - Up-Left
                        ballPos.x -= ballSpeed;
                        ballPos.y -= ballSpeed;
                    } else if (dir == 3) { // 3 - Down-Left
                        ballPos.x -= ballSpeed;
                        ballPos.y += ballSpeed;
                    }
                    checkCollision(); // 충돌 체크
                    myPanel.repaint(); // redraw
                }
            });
            timer.start(); // 해줘야 타이머 동작 시작됨.
        }

        // 충돌 체크 함수
        public void checkCollision() {
            if (dir == 0) { // 공의 방향, 0 - Up-Right
                // 위쪽 벽에 부딪힌 경우
                if (ballPos.y < 0) {
                    dir = 1;
                }
                // 오른쪽 벽
                if (ballPos.x > 800 - ballWidth) {
                    dir = 2;
                    scoreLeft += 1;
                }
                // 바의 위치에 부딪히도록
                if (ballPos.x > barRightPos.x - ballWidth &&
                        (ballPos.y >= barRightPos.y && ballPos.y <= barRightPos.y + barHeight)) {
                    dir = 2;
                }
            } else if (dir == 1) { // 1 - Down-Right
                // 아래쪽 벽
                if (ballPos.y > 400 - ballHeight - 20) {
                    dir = 0;
                }
                // 오른쪽 벽
                if (ballPos.x > 800 - ballWidth) {
                    dir = 3;
                    scoreLeft += 1;
                }
                // 바
                if (ballPos.x > barRightPos.x - ballWidth &&
                        (ballPos.y >= barRightPos.y && ballPos.y <= barRightPos.y + barHeight)) {
                    dir = 3;
                }
            } else if (dir == 2) { // 2 - Up-Left
                // 위쪽 벽
                if (ballPos.y < 0) {
                    dir = 3;
                }
                // 왼쪽 벽
                if (ballPos.x < 0) {
                    dir = 0;
                    scoreRight += 1;
                }
                // 바
                if (ballPos.x < barLeftPos.x + ballWidth &&
                        (ballPos.y >= barLeftPos.y && ballPos.y <= barLeftPos.y + barHeight)) {
                    dir = 0;
                }
            } else if (dir == 3) { // 3 - Down-Left
                // 아래쪽 벽
                if (ballPos.y > 400 - ballHeight - 20) {
                    dir = 2;
                }
                // 왼쪽 벽
                if (ballPos.x < 0) {
                    dir = 1;
                    scoreRight += 1;
                }
                // 바
                if (ballPos.x < barLeftPos.x + ballWidth &&
                        (ballPos.y >= barLeftPos.y && ballPos.y <= barLeftPos.y + barHeight)) {
                    dir = 1;
                }
            }
        }

    }

    public static void main(String[] args) {
        new MyFrame("PingPong Game Single");
    }

}
