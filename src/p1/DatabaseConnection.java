package p1;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


public class DatabaseConnection extends JFrame implements ActionListener {

    private boolean DEBUG = true;
    private static final long serialVersionUID = 1L;
    TextField inputField;
    TextArea outputArea;

    Connection conn = null;
    String[][] data = null;
    JTable table;
    JButton exitButton;
    JButton plusButton;
    JButton minusButton;

    public DatabaseConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:stock.db");
            conn = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened Database Successfully");
        // GUI creation
        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1, 3));
        Label label = new Label("Item ID: ");

        inputField = new TextField(15);
        inputField.addActionListener(this);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        plusButton = new JButton("+");
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Item not found!", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    int id = Integer.parseInt(inputField.getText());
                    increaseAmount(id);
                }


            }
        });

        minusButton = new JButton("-");
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Item Not Found!", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    int id = Integer.parseInt(inputField.getText());
                    decreaseAmount(id);
                }

            }
        });

        p1.add(label);
        p1.add(inputField);
        p1.add(plusButton);
        p1.add(minusButton);
        p1.add(exitButton);
        add(p1, "North");

        outputArea = new TextArea(10, 50);
        add(outputArea, "Center");
        // Create a JTable
        table = new JTable(new MyTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                printDebugLine("Table changed. ");
            }
        });
        table.setAutoCreateRowSorter(true);
        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        // Add the scroll pane to this panel.
        add(scrollPane, "South");
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        // Create and set up the window.
        setTitle("Stock Management System");
        setSize(400, 200);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showRecord(int id) {

        try (Statement stmt = conn.createStatement();) {
            // to get the old price and amount from database of a specified record
            ResultSet rs = stmt.executeQuery("select * from stock where ID = " + id);
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Item not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            String title = rs.getString("Item");
            float price = rs.getFloat("Price");
            int amount = rs.getInt("Amount");

            System.out.println(title + "--------" + price + "------------" + amount + "----------------");
            // to display the old and new price and amount
            outputArea.append("Item : " + title + "\nPrice: " + price + "\nAmount: " + amount + "\n-------------\n");
        } catch (SQLException se) {
            System.err.println("Error in database access: ");
            se.printStackTrace();
        } /*finally {
            closeConnection();
        }*/

    }

    private void closeConnection() {
        try {
            System.out.println("Closing Connection...");
            if (conn != null)
                conn.close();
        } catch (SQLException e1) {
            e1.printStackTrace();

        }
    }

    private void printDebugLine(String line) {
        if (DEBUG) {
            System.out.println(line);
        }

    }

    //Method for changing item's price
    public void updateItemPrice(int id, float newprice) {
        try (Statement stmt = conn.createStatement()) {
            // to get the old price from database of a specified record
            ResultSet rs = stmt.executeQuery("select * from stock where ID = " + id);
            rs.next();
            float price = rs.getFloat("Price");
            // update the price
            String query = new String("Update stock set Price = " + newprice + " where ID = " + id);

            stmt.executeUpdate(query);
            // to get the new price from database
            rs = stmt.executeQuery("Select * from stock where ID = " + id);
            rs.next();

            String name = rs.getString("Item");
            float newPrice = rs.getFloat("Price");
            // to display the old and new price
            outputArea.append("Item Name: " + name + "\nOLD PRICE: " + price + "\nNEW PRICE: " + newPrice +
                    "\nIncrease in price is " + (newPrice - price) + "\n---------\n");

        } catch (SQLException se) {
            System.err.println("Error in database access: ");
            se.printStackTrace();
        }
    }

    public void updateItemAmount(int id, int newamount) {
        try (Statement stmt = conn.createStatement()) {
            // to get the old amount from database of a specified record
            ResultSet rs = stmt.executeQuery("select * from stock where ID = " + id);
            rs.next();
            int price = rs.getInt("Amount");
            // update the amount
            String query = new String("Update stock set Amount = " + newamount + " where ID = " + id);

            stmt.executeUpdate(query);
            // to get the new amount from database
            rs = stmt.executeQuery("Select * from stock where ID = " + id);
            rs.next();

            String name = rs.getString("Item");
            int newAmount = rs.getInt("Amount");
            // to display the old and new price
            outputArea.append("Item Name: " + name + "\nOUTDATED AMOUNT: " + price + "\nUPDATED AMOUNT: " + newAmount + "\n---------\n");
            //If else statement because if amount is zero, the item should be unavailable
            if (newamount == 0) {
                updateItemAvailability(id, false);
            } else {
                updateItemAvailability(id, true);
            }

        } catch (SQLException se) {
            System.err.println("Error in database access: ");
            se.printStackTrace();
        }
    }

    // Method for changing item's availability status
    public void updateItemAvailability(int id, boolean newavailability) {
        try (Statement stmt = conn.createStatement()) {
            // to get the old availability status from database of a specified record
            ResultSet rs = stmt.executeQuery("select * from stock where ID = " + id);
            rs.next();
            boolean availability = rs.getBoolean("Available");
            // update the availability status
            String query = new String("Update stock set Available = " + newavailability + " where ID = " + id);

            stmt.executeUpdate(query);
            // to get the new availability status from database
            rs = stmt.executeQuery("Select * from stock where ID = " + id);
            rs.next();

            String name = rs.getString("Item");
            boolean newAvailability = rs.getBoolean("Amount");
            // to display the old and new availability of item
            outputArea.append("Item Name: " + name + "\nOUTDATED AVAILABILITY: " + availability + "\nUPDATED AVAILABILITY: " + newAvailability + "\n---------\n");

        } catch (SQLException se) {
            System.err.println("Error in database access: ");
            se.printStackTrace();
        }
    }


    public void increaseAmount(int id) {
        try (Statement stmt = conn.createStatement()) {
            // Get the current amount of the item
            ResultSet rs = stmt.executeQuery("select * from stock where ID = " + id);
            rs.next();

            int currentAmount = rs.getInt("Amount");

            int newAmount = currentAmount + 1;

            // Update the amount in the stock table
            String query = "Update stock set Amount = " + newAmount + " where ID = " + id;
            stmt.executeUpdate(query);

            // Get the updated amount and item name from the stock table
            rs = stmt.executeQuery("Select * from stock where ID = " + id);
            rs.next();
            String name = rs.getString("Item");

            outputArea.append("Item Name: " + name + "\nUPDATED AMOUNT: " + newAmount + "\n---------\n");

            // If the new amount is zero, set the item as unavailable
            if (newAmount == 0) {
                updateItemAvailability(id, false);
            } else {
                updateItemAvailability(id, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decreaseAmount(int id) {
        try (Statement stmt = conn.createStatement()) {
            // Get the current amount of the item
            ResultSet rs = stmt.executeQuery("select * from stock where ID = " + id);
            rs.next();

            int currentAmount = rs.getInt("Amount");

            int newAmount = currentAmount - 1;

            // Update the amount in the stock table
            String query = "Update stock set Amount = " + newAmount + " where ID = " + id;
            stmt.executeUpdate(query);

            // Get the updated amount and item name from the stock table
            rs = stmt.executeQuery("Select * from stock where ID = " + id);
            rs.next();
            String name = rs.getString("Item");

            // Display the updated amount
            outputArea.append("Item Name: " + name + "\nUPDATED AMOUNT: " + newAmount + "\n---------\n");

            // If the new amount is zero, set the item as unavailable
            if (newAmount == 0) {
                updateItemAvailability(id, false);
            } else {
                updateItemAvailability(id, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void actionPerformed(ActionEvent e) {
        // We have two events. One for the Exit button. The other for the  text area.
        if (e.getSource() == exitButton) {
            closeConnection();
            System.exit(0);
        } else if (e.getSource() == plusButton) {

        } else if (e.getSource() == minusButton) {

        }
        showRecord(Integer.parseInt(inputField.getText()));
    }


    class MyTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private String[] columnNames = {"Item", "Amount", "Price", "Available", "Item ID"};
        private Object[][] data = null;

        public MyTableModel() {
            fillData();
        }

        private void fillData() {
            ResultSet rs;
            try (Statement stmt = conn.createStatement();) {

                // Find the number of rows
                String query = "SELECT COUNT(*) FROM stock";
                ResultSet rs1 = stmt.executeQuery(query);
                int numberOfRecords = rs1.getInt(1);

                query = "SELECT Item, Amount, Price, Available, ID FROM stock";
                rs = stmt.executeQuery(query);

                // Allocate two-dimensional array needed for JTable
                data = new Object[numberOfRecords][5];
                int indx = 0;

                // Extract data from result set
                while (rs.next()) {
                    // Retrieve by column name
                    data[indx][0] = rs.getString("Item");
                    data[indx][1] = String.valueOf(rs.getInt("Amount"));
                    data[indx][2] = String.valueOf(rs.getFloat("Price"));
                    data[indx][3] = Boolean.valueOf(rs.getBoolean("Available"));
                    data[indx][4] = String.valueOf(rs.getInt("ID"));
                    indx++;


                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public int getRowCount() {
            return data.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class<?> getColumnClass(int c) {
            if (DEBUG) {
                System.out.println(getValueAt(0, c).getClass());
            }
            return getValueAt(0, c).getClass();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public boolean isCellEditable(int row, int colNum) {
            switch (colNum) {
                case 1:
                    return true;
                case 2:
                    return true;
                case 3:
                    return true;
                default:
                    return false;

            }
        }

        public void setValueAt(Object value, int row, int col) {
            printDebugLine("Setting value at " + row + "," + col + " to " + value + " an Instance of " + value.getClass() + ")");
            data[row][col] = value;
            // item id is the primary key
            int id = Integer.parseInt((String) data[row][4]);
            switch (col) {
                case 1:
                    updateItemAmount(id, Integer.parseInt((String) value));
                    break;
                case 2:
                    updateItemPrice(id, Float.parseFloat((String) value));
                    break;
                case 3:
                    boolean b1 = (Boolean) value;
                    updateItemAvailability(id, b1);
                default:
                    break;
            }
            if (DEBUG) {
                System.out.println("New Value of Data: ");
                printDebugData();
            }
        }

        private void printDebugData() {
            for (int i = 0; i < getRowCount(); i++) {
                System.out.println("      row" + i + " : ");
                for (int j = 0; j < getColumnCount(); j++) {
                    System.out.println("    " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("-----------------------");
        }

    }


}


