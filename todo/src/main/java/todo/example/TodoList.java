package todo.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class TodoList extends JFrame {
    private ArrayList<Card> cards = new ArrayList<>();
    private JPanel cardPanel = new JPanel();
    private JTextField cardField = new JTextField(20);
    private JButton addCardButton = new JButton("Add Card");
    private JButton saveButton = new JButton("Save Tasks");
    private JButton loadButton = new JButton("Load Tasks");

    public TodoList() {
        setTitle("Todo List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.add(new JLabel("Card:"));
        panel.add(cardField);
        panel.add(addCardButton);
        panel.add(saveButton);
        panel.add(loadButton);

        add(panel, BorderLayout.NORTH);

        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Add MouseWheelListener for horizontal scrolling
        cardPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                    JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
                    int scrollAmount = e.getUnitsToScroll() * horizontalScrollBar.getUnitIncrement();
                    horizontalScrollBar.setValue(horizontalScrollBar.getValue() + scrollAmount);
                }
            }
        });

        addCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardTitle = cardField.getText();
                if (!cardTitle.isEmpty()) {
                    addCard(cardTitle);
                    cardField.setText("");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTasksToFile();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTasksFromFile();
            }
        });
    }

    public void addCard(String title) {
        Card card = new Card(title);
        cards.add(card);
        cardPanel.add(card);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public void saveTasksToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tasks");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                for (Card card : cards) {
                    writer.write("Card: " + card.getTitle() + "\n");
                    for (String task : card.getTasks()) {
                        writer.write(task + "\n");
                    }
                    writer.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Tasks saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving tasks.");
            }
        }
    }

    public void loadTasksFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Tasks");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                String line;
                cards.clear();
                cardPanel.removeAll();
                Card currentCard = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Card: ")) {
                        if (currentCard != null) {
                            cards.add(currentCard);
                            cardPanel.add(currentCard);
                        }
                        currentCard = new Card(line.substring(6));
                    } else if (!line.trim().isEmpty() && currentCard != null) {
                        currentCard.addTask(line);
                    }
                }
                if (currentCard != null) {
                    cards.add(currentCard);
                    cardPanel.add(currentCard);
                }
                cardPanel.revalidate();
                cardPanel.repaint();
                JOptionPane.showMessageDialog(this, "Tasks loaded successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading tasks.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TodoList().setVisible(true);
            }
        });
    }
}