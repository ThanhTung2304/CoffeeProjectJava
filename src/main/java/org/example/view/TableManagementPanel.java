package org.example.view;

import org.example.controller.ReservationController;
import org.example.controller.TableController;
import org.example.entity.Reservation;
import org.example.entity.TableSeat;
import org.example.event.DataChangeEventBus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableManagementPanel extends JPanel {

    private static final Color BG        = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color BORDER    = new Color(0xE2E8F0);

    private static final Color STATUS_EMPTY    = new Color(0xDCFCE7);
    private static final Color STATUS_EMPTY_FG = new Color(0x166534);
    private static final Color STATUS_IN_USE   = new Color(0xFEE2E2);
    private static final Color STATUS_IN_USE_FG = new Color(0x991B1B);
    private static final Color STATUS_RESERVED = new Color(0xFEF9C3);
    private static final Color STATUS_RESERVED_FG = new Color(0x854D0E);

    private static final Color CARD_BG     = Color.WHITE;
    private static final Color CARD_HOVER  = new Color(0xF1F5F9);
    private static final Color CARD_BORDER = new Color(0xE2E8F0);
    private static final Color FLOOR_BG    = new Color(0xF8FAFC);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_AMBER = new Color(0xF59E0B);
    private static final Color BTN_RED   = new Color(0xEF4444);
    private static final Color BTN_GRAY  = new Color(0x64748B);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_LARGE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_FLOOR = new Font("Segoe UI", Font.BOLD, 16);

    private JPanel floor1Panel;
    private JPanel floor2Panel;
    private JPanel floor1Grid;
    private JPanel floor2Grid;
    private JLabel floor1Title;
    private JLabel floor2Title;
    private JLabel totalCountLabel;
    private JLabel emptyCountLabel;
    private JLabel inUseCountLabel;
    private JLabel reservedCountLabel;

    private final TableController controller = new TableController();
    private final ReservationController reservationController = new ReservationController();
    private final DataChangeEventBus.DataChangeListener dataListener = this::loadData;

    public TableManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        initUI();
        loadData();
        DataChangeEventBus.onRegister(dataListener);
    }

    public void cleanup() {
        DataChangeEventBus.onUnregister(dataListener);
    }

    private void initUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🍽");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản Lý Bàn");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Dữ liệu từ database");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(title);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        totalCountLabel = createStatLabel("0 Tổng");
        emptyCountLabel = createStatLabel("0 Trống");
        inUseCountLabel = createStatLabel("0 Đang dùng");
        reservedCountLabel = createStatLabel("0 Đặt trước");

        JButton btnRefresh = createSmallButton("↻ Làm mới", BTN_GRAY);
        btnRefresh.addActionListener(e -> loadData());

        right.add(totalCountLabel);
        right.add(emptyCountLabel);
        right.add(inUseCountLabel);
        right.add(reservedCountLabel);
        right.add(btnRefresh);

        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JLabel createStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0xCBD5E1));
        return lbl;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 20, 20, 20));

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(Color.WHITE);
        tabs.setForeground(new Color(0x1E293B));

        floor1Panel = createFloorPanel(1);
        floor2Panel = createFloorPanel(2);

        tabs.addTab("  Tầng 1  ", floor1Panel);
        tabs.addTab("  Tầng 2  ", floor2Panel);

        center.add(tabs, BorderLayout.CENTER);

        return center;
    }

    private JPanel createFloorPanel(int floor) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(FLOOR_BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JLabel titleLabel = new JLabel("🏢 Tầng " + floor);
        titleLabel.setFont(FONT_FLOOR);
        titleLabel.setForeground(new Color(0x1E293B));

        if (floor == 1) {
            floor1Title = titleLabel;
        } else {
            floor2Title = titleLabel;
        }

        JButton btnAddTable = createSmallButton("＋ Thêm bàn Tầng " + floor, BTN_GREEN);
        btnAddTable.addActionListener(e -> addTable(floor));

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(btnAddTable, BorderLayout.EAST);

        panel.add(titleBar, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 5, 12, 12));
        grid.setOpaque(false);

        if (floor == 1) {
            floor1Grid = grid;
        } else {
            floor2Grid = grid;
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(FLOOR_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTableCard(TableSeat table) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(160, 110));

        JLabel lblNumber = new JLabel(table.getName(), SwingConstants.CENTER);
        lblNumber.setFont(FONT_LARGE);
        lblNumber.setForeground(new Color(0x1E293B));

        JLabel lblStatus = new JLabel(table.getStatus(), SwingConstants.CENTER);
        lblStatus.setFont(FONT_BOLD);
        lblStatus.setOpaque(true);
        lblStatus.setBorder(new EmptyBorder(4, 12, 4, 12));
        applyStatusStyle(lblStatus, table.getStatus());

        JLabel lblCapacity = new JLabel(table.getCapacity() + " chỗ", SwingConstants.CENTER);
        lblCapacity.setFont(FONT_SMALL);
        lblCapacity.setForeground(new Color(0x64748B));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(lblNumber);
        center.add(Box.createVerticalStrut(6));
        center.add(lblStatus);
        center.add(Box.createVerticalStrut(4));
        center.add(lblCapacity);

        card.add(center, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showTableStatusDialog(table);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(CARD_HOVER);
                card.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(CARD_BG);
                card.repaint();
            }
        });

        return card;
    }

    private void applyStatusStyle(JLabel lbl, String status) {
        switch (status) {
            case "Trống" -> {
                lbl.setBackground(STATUS_EMPTY);
                lbl.setForeground(STATUS_EMPTY_FG);
            }
            case "Đang sử dụng" -> {
                lbl.setBackground(STATUS_IN_USE);
                lbl.setForeground(STATUS_IN_USE_FG);
            }
            case "Đặt trước" -> {
                lbl.setBackground(STATUS_RESERVED);
                lbl.setForeground(STATUS_RESERVED_FG);
            }
            default -> {
                lbl.setBackground(STATUS_EMPTY);
                lbl.setForeground(STATUS_EMPTY_FG);
            }
        }
    }

    private void showTableStatusDialog(TableSeat table) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông Tin Bàn", true);
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("🍽 " + table.getName());
        tableTitle.setFont(FONT_LARGE);

        JLabel floorLabel = new JLabel("Tầng " + table.getFloor());
        floorLabel.setFont(FONT_BODY);
        floorLabel.setForeground(new Color(0x64748B));

        headerPanel.add(tableTitle, BorderLayout.WEST);
        headerPanel.add(floorLabel, BorderLayout.EAST);
        content.add(headerPanel, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        statusPanel.setOpaque(false);

        JLabel currentStatus = new JLabel("Trạng thái hiện tại:");
        currentStatus.setFont(FONT_BOLD);
        statusPanel.add(currentStatus);

        JLabel statusValue = new JLabel("  " + table.getStatus());
        statusValue.setFont(FONT_BOLD);
        statusValue.setOpaque(true);
        statusValue.setBorder(new EmptyBorder(6, 12, 6, 12));
        applyStatusStyle(statusValue, table.getStatus());
        statusPanel.add(statusValue);

        JLabel capLabel = new JLabel("Sức chứa: " + table.getCapacity() + " người");
        capLabel.setFont(FONT_BODY);
        statusPanel.add(Box.createVerticalStrut(4));
        statusPanel.add(capLabel);

        if (table.getNote() != null && !table.getNote().isEmpty()) {
            JLabel noteLabel = new JLabel("Ghi chú: " + table.getNote());
            noteLabel.setFont(FONT_BODY);
            noteLabel.setForeground(new Color(0x64748B));
            statusPanel.add(noteLabel);
        }

        content.add(statusPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnEmpty = createDialogButton("Trống", STATUS_EMPTY, STATUS_EMPTY_FG);
        JButton btnInUse = createDialogButton("Đang sử dụng", STATUS_IN_USE, STATUS_IN_USE_FG);
        JButton btnReserved = createDialogButton("Đặt trước", STATUS_RESERVED, STATUS_RESERVED_FG);
        JButton btnClose = createDialogButton("Đóng", new Color(0xE2E8F0), new Color(0x475569));

        btnEmpty.addActionListener(e -> {
            table.setStatus("Trống");
            controller.updateTable(table);
            completePendingReservations(table.getTableNumber());
            loadData();
            dialog.dispose();
        });

        btnInUse.addActionListener(e -> {
            table.setStatus("Đang sử dụng");
            controller.updateTable(table);
            loadData();
            dialog.dispose();
        });

        btnReserved.addActionListener(e -> {
            table.setStatus("Đặt trước");
            controller.updateTable(table);
            loadData();
            dialog.dispose();
        });

        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnEmpty);
        btnPanel.add(btnInUse);
        btnPanel.add(btnReserved);
        btnPanel.add(btnClose);

        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void completePendingReservations(int tableNumber) {
        for (Reservation r : reservationController.getAllReservations()) {
            if (r.getTableNumber() == tableNumber && "Đang đặt".equals(r.getStatus())) {
                r.setStatus("Đã đặt");
                reservationController.updateReservation(r);
            }
        }
    }

    private JButton createDialogButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addTable(int floor) {
        JTextField txtName = new JTextField("Bàn " + (getNextTableIndex(floor)));
        JTextField txtCapacity = new JTextField("4");

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.add(new JLabel("Tên bàn:"));
        p.add(txtName);
        p.add(new JLabel("Sức chứa:"));
        p.add(txtCapacity);

        int result = JOptionPane.showConfirmDialog(this, p, "Thêm bàn mới - Tầng " + floor,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            List<TableSeat> existing = controller.getAllTables();
            boolean duplicate = existing.stream()
                    .anyMatch(t -> t.getFloor() == floor && t.getName().equalsIgnoreCase(name));
            if (duplicate) {
                JOptionPane.showMessageDialog(this, "Đã có bàn \"" + name + "\" ở tầng " + floor + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TableSeat table = new TableSeat();
            table.setName(name);
            table.setFloor(floor);
            try {
                table.setCapacity(Integer.parseInt(txtCapacity.getText().trim()));
            } catch (NumberFormatException ex) {
                table.setCapacity(4);
            }
            table.setStatus("Trống");
            table.setNote("");
            controller.addTable(table);
            loadData();
        }
    }

    private int getNextTableIndex(int floor) {
        List<TableSeat> all = controller.getAllTables();
        int max = 0;
        for (TableSeat t : all) {
            if (t.getFloor() == floor) {
                String name = t.getName().replaceAll("[^0-9]", "");
                try {
                    int num = Integer.parseInt(name);
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return max + 1;
    }

    private void loadData() {
        List<TableSeat> allTables = controller.getAllTables();

        int total = allTables.size();
        int empty = 0, inUse = 0, reserved = 0;
        for (TableSeat t : allTables) {
            switch (t.getStatus()) {
                case "Trống" -> empty++;
                case "Đang sử dụng" -> inUse++;
                case "Đặt trước" -> reserved++;
            }
        }

        totalCountLabel.setText(total + " Tổng");
        emptyCountLabel.setText(empty + " Trống");
        inUseCountLabel.setText(inUse + " Đang dùng");
        reservedCountLabel.setText(reserved + " Đặt trước");

        Map<Integer, List<TableSeat>> byFloor = allTables.stream()
                .collect(Collectors.groupingBy(TableSeat::getFloor));

        List<TableSeat> floor1Tables = byFloor.getOrDefault(1, new ArrayList<>());
        List<TableSeat> floor2Tables = byFloor.getOrDefault(2, new ArrayList<>());

        floor1Title.setText("🏢 Tầng 1  —  " + floor1Tables.size() + " bàn");
        floor2Title.setText("🏢 Tầng 2  —  " + floor2Tables.size() + " bàn");

        refreshGrid(floor1Grid, floor1Tables);
        refreshGrid(floor2Grid, floor2Tables);
    }

    private void refreshGrid(JPanel grid, List<TableSeat> tables) {
        grid.removeAll();

        if (tables.isEmpty()) {
            JLabel emptyLabel = new JLabel("Chưa có bàn nào", SwingConstants.CENTER);
            emptyLabel.setFont(FONT_BODY);
            emptyLabel.setForeground(new Color(0x94A3B8));
            grid.setLayout(new BorderLayout());
            grid.add(emptyLabel, BorderLayout.CENTER);
        } else {
            grid.setLayout(new GridLayout(0, 5, 12, 12));
            for (TableSeat t : tables) {
                grid.add(createTableCard(t));
            }
        }

        grid.revalidate();
        grid.repaint();
    }

    private JButton createSmallButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? base.darker() : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 32));
        return btn;
    }
}
