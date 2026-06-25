import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TodoMain {
    // タスクを保存するファイルの名前
    private static final String FILE_NAME = "todo.txt";
    private static JPanel listPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. ウィンドウの作成
            JFrame frame = new JFrame("タスク管理アプリ");
            frame.setSize(400, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // 2. 入力エリアの作成
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BorderLayout());
            JTextField taskField = new JTextField();
            JButton addButton = new JButton("追加");
            inputPanel.add(taskField, BorderLayout.CENTER);
            inputPanel.add(addButton, BorderLayout.EAST);
            frame.add(inputPanel, BorderLayout.NORTH);

            // 3. タスクを並べるメインパネルの作成
            listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(listPanel);
            frame.add(scrollPane, BorderLayout.CENTER);

            // 【新機能】起動時に保存されたタスクを読み込む
            loadTasks();

            // 4. ボタンが押されたときの処理
            addButton.addActionListener(e -> {
                String taskText = taskField.getText().trim();
                if (!taskText.isEmpty()) {
                    addTaskRow(taskText);
                    taskField.setText("");
                    saveTasks(); // 【新機能】追加したらファイルに保存
                }
            });

            frame.setVisible(true);
        });
    }

    // タスクを画面（リスト）に追加する共通メソッド
    private static void addTaskRow(String taskText) {
        JPanel taskRow = new JPanel();
        taskRow.setLayout(new BorderLayout());
        taskRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));

        JLabel taskLabel = new JLabel("  " + taskText);
        JButton deleteButton = new JButton("削除");

        taskRow.add(taskLabel, BorderLayout.CENTER);
        taskRow.add(deleteButton, BorderLayout.EAST);

        // 削除ボタンの処理
        deleteButton.addActionListener(delEvent -> {
            listPanel.remove(taskRow);
            listPanel.revalidate();
            listPanel.repaint();
            saveTasks(); // 【新機能】削除したらファイルにも反映
        });

        listPanel.add(taskRow);
        listPanel.revalidate();
        listPanel.repaint();
    }

    // 【新機能】タスクをテキストファイルに保存するメソッド
    private static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // 画面上の全タスク行から文字を取得してファイルに書き込む
            for (int i = 0; i < listPanel.getComponentCount(); i++) {
                JPanel row = (JPanel) listPanel.getComponent(i);
                JLabel label = (JLabel) row.getComponent(0); // 0番目がJLabel
                // 先頭のスペース「  」を削って保存
                writer.write(label.getText().substring(2));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 【新機能】テキストファイルからタスクを読み込むメソッド
    private static void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // ファイルがなければ何もしない

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addTaskRow(line); // 読み込んだタスクを画面に追加
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}