package todo.example;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.ArrayList;

public class Card extends JPanel {
    private String title;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskField;
    private JButton addTaskButton;

    public Card(String title) {
        this.title = title;
        this.taskListModel = new DefaultListModel<>();
        this.taskList = new JList<>(taskListModel);
        this.taskField = new JTextField(20);
        this.addTaskButton = new JButton("Add Task");

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));

        JPanel inputPanel = new JPanel();
        inputPanel.add(taskField);
        inputPanel.add(addTaskButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        addTaskButton.addActionListener(e -> {
            String task = taskField.getText();
            if (!task.isEmpty()) {
                taskListModel.addElement(task);
                taskField.setText("");
            }
        });

        taskList.setDragEnabled(true);
        taskList.setTransferHandler(new TaskTransferHandler());

        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable transferable = dtde.getTransferable();
                    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                        String task = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                        addTask(task);
                        dtde.dropComplete(true);
                    } else {
                        dtde.rejectDrop();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtde.rejectDrop();
                }
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getTasks() {
        ArrayList<String> tasks = new ArrayList<>();
        for (int i = 0; i < taskListModel.size(); i++) {
            tasks.add(taskListModel.getElementAt(i));
        }
        return tasks;
    }

    public void addTask(String task) {
        taskListModel.addElement(task);
    }

    public void clearTasks() {
        taskListModel.clear();
    }

    private class TaskTransferHandler extends TransferHandler {
        private int sourceIndex;

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<String> list = (JList<String>) c;
            sourceIndex = list.getSelectedIndex();
            return new StringSelection(list.getSelectedValue());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                String task = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
                int index = dropLocation.getIndex();
                if (index == -1) {
                    taskListModel.addElement(task);
                } else {
                    taskListModel.add(index, task);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE) {
                taskListModel.remove(sourceIndex);
            }
        }
    }
}