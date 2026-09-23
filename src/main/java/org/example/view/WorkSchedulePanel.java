package org.example.view;

import org.example.controller.EmployeeController;
import org.example.controller.EmployeeShiftController;
import org.example.controller.ShiftController;
import org.example.controller.WorkScheduleController;
import org.example.entity.Employee;
import org.example.entity.Shift;
import org.example.entity.WorkSchedule;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class WorkSchedulePanel extends JPanel {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(0xF5F7FA);
    private static final Color HEADER_BG    = new Color(0x1E293B);
    private static final Color ROW_ODD      = Color.WHITE;
    private static final Color ROW_EVEN     = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);
    private static final Color TH_BG        = new Color(0x334155);
    private static final Color TH_FG        = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2E8F0);

    private static final Color BTN_RED    = new Color(0xEF4444);
    private static final Color BTN_SLATE  = new Color(0x64748B);
    private static final Color BTN_BLUE   = new Color(0x3B82F6);

    private static final Color SHIFT_BG   = new Color(0xDBEAFE);
    private static final Color SHIFT_FG   = new Color(0x1E40AF);
    private static final Color EMPTY_BG   = Color.WHITE;
    private static final Color TODAY_BG   = new Color(0xFFF7ED);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BOLD_12 = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_WEEK   = new Font("Segoe UI", Font.BOLD, 15);

    // ── Fields ────────────────────────────────────────────────────────────────
    private JTable table;
    private WeeklyScheduleModel tableModel;
    private JLabel weekLabel;
    private JLabel shiftCountLabel;
    private JComboBox<String> cbWeek;

    private LocalDate weekStart;

    private final EmployeeController employeeController = new EmployeeController();
    private final ShiftController shiftController = new ShiftController();
    private final WorkScheduleController workScheduleController = new WorkScheduleController();
    private final EmployeeShiftController employeeShiftController = new EmployeeShiftController();

    private List<Employee> employees;
    private List<Shift> shifts;
    private Map<String, WorkSchedule> scheduleMap;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DAY_FMT  = DateTimeFormatter.ofPattern("EEE", new java.util.Locale("vi"));

    public WorkSchedulePanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        initUI();
        loadData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void initUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("📅");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản Lý Lịch Làm Việc");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Xem và sắp xếp lịch làm việc theo tuần");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(title);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        shiftCountLabel = new JLabel("0 ca đã xếp");
        shiftCountLabel.setFont(FONT_BOLD_12);
        shiftCountLabel.setForeground(new Color(0xCBD5E1));
        header.add(shiftCountLabel, BorderLayout.EAST);

        return header;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(12, 16, 16, 16));

        center.add(buildWeekNav(), BorderLayout.NORTH);
        center.add(buildLegend(), BorderLayout.CENTER);

        return center;
    }

    // ── Week navigation ───────────────────────────────────────────────────────
    private JPanel buildWeekNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);

        // Left: navigation buttons
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftNav.setOpaque(false);

        JButton btnPrev = createSmallButton("◀", BTN_SLATE, 36, 32);
        JButton btnNext = createSmallButton("▶", BTN_SLATE, 36, 32);
        JButton btnToday = createSmallButton("Hôm nay", BTN_BLUE, 90, 32);

        weekLabel = new JLabel();
        weekLabel.setFont(FONT_WEEK);
        weekLabel.setForeground(new Color(0x1E293B));
        weekLabel.setBorder(new EmptyBorder(0, 8, 0, 8));

        cbWeek = new JComboBox<>();
        cbWeek.setFont(FONT_BODY);
        cbWeek.setPreferredSize(new Dimension(260, 32));
        cbWeek.setBackground(Color.WHITE);

        leftNav.add(btnPrev);
        leftNav.add(weekLabel);
        leftNav.add(btnNext);
        leftNav.add(Box.createHorizontalStrut(8));
        leftNav.add(new JLabel("Chọn nhanh:"));
        leftNav.add(cbWeek);
        leftNav.add(Box.createHorizontalStrut(8));
        leftNav.add(btnToday);

        // Right: action buttons
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightNav.setOpaque(false);

        JButton btnRefresh = createSmallButton("↻ Làm mới", BTN_SLATE, 110, 32);
        JButton btnExport = createSmallButton("↓ Excel", BTN_BLUE, 90, 32);

        rightNav.add(btnRefresh);
        rightNav.add(btnExport);

        nav.add(leftNav, BorderLayout.WEST);
        nav.add(rightNav, BorderLayout.EAST);

        btnPrev.addActionListener(e -> { weekStart = weekStart.minusWeeks(1); refreshWeekCombo(); loadData(); });
        btnNext.addActionListener(e -> { weekStart = weekStart.plusWeeks(1); refreshWeekCombo(); loadData(); });
        btnToday.addActionListener(e -> {
            weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            refreshWeekCombo();
            loadData();
        });
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> exportToExcel());

        cbWeek.addActionListener(e -> {
            int idx = cbWeek.getSelectedIndex();
            if (idx >= 0) {
                LocalDate newStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(12).plusWeeks(idx);
                if (!newStart.equals(weekStart)) {
                    weekStart = newStart;
                    loadData();
                }
            }
        });

        refreshWeekCombo();

        return nav;
    }

    private void refreshWeekCombo() {
        cbWeek.removeActionListener(cbWeek.getActionListeners().length > 0 ? cbWeek.getActionListeners()[0] : null);

        LocalDate today = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate start = today.minusWeeks(12);
        int totalWeeks = 25;

        String currentDisplay = "Tuần " + weekStart.format(DATE_FMT) + " - " + weekStart.plusDays(6).format(DATE_FMT);

        cbWeek.removeAllItems();
        int selectedIndex = 0;
        for (int i = 0; i < totalWeeks; i++) {
            LocalDate ws = start.plusWeeks(i);
            LocalDate we = ws.plusDays(6);
            String label = ws.format(DATE_FMT) + " - " + we.format(DATE_FMT);
            if (ws.equals(weekStart)) {
                label = "▶ " + label + " ◀";
                selectedIndex = i;
            }
            cbWeek.addItem(label);
        }
        cbWeek.setSelectedIndex(selectedIndex);

        cbWeek.addActionListener(e -> {
            int idx = cbWeek.getSelectedIndex();
            if (idx >= 0) {
                LocalDate newStart = today.minusWeeks(12).plusWeeks(idx);
                if (!newStart.equals(weekStart)) {
                    weekStart = newStart;
                    loadData();
                }
            }
        });
    }

    // ── Legend ─────────────────────────────────────────────────────────────────
    private JPanel buildLegend() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setOpaque(false);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legend.setOpaque(false);

        legend.add(createLegendItem("● Có ca làm", SHIFT_BG, SHIFT_FG));
        legend.add(createLegendItem("● Hôm nay", TODAY_BG, new Color(0x9A3412)));
        legend.add(createLegendItem("● Trống", EMPTY_BG, new Color(0x94A3B8)));

        wrapper.add(legend, BorderLayout.NORTH);
        wrapper.add(buildTablePanel(), BorderLayout.CENTER);

        return wrapper;
    }

    private JLabel createLegendItem(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    // ── Table Panel ───────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        tableModel = new WeeklyScheduleModel();
        table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(new Color(0x1E40AF));
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Employee name column (column 0)
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(0).setMinWidth(140);
        table.getColumnModel().getColumn(0).setResizable(false);

        // Day columns
        for (int i = 1; i <= 7; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(120);
            table.getColumnModel().getColumn(i).setMinWidth(100);
        }

        // Custom header renderer
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(TH_BG);
        th.setForeground(TH_FG);
        th.setPreferredSize(new Dimension(0, 44));
        th.setReorderingAllowed(false);
        th.setDefaultRenderer(new DayHeaderRenderer());

        // Custom cell renderer
        table.setDefaultRenderer(Object.class, new DayCellRenderer());

        // Click to assign shift
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 1 && col <= 7) {
                    openShiftPicker(row, col);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);

        return scroll;
    }

    // ── Shift picker dialog ───────────────────────────────────────────────────
    private void openShiftPicker(int row, int col) {
        if (row < 0 || row >= employees.size()) return;

        Employee emp = employees.get(row);
        LocalDate workDate = weekStart.plusDays(col - 1);

        WorkSchedule existing = scheduleMap.get(emp.getId() + "_" + workDate);

        String dayName = workDate.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("vi"));
        String title = "📅 " + emp.getName() + " - " + workDate.format(DATE_FMT) + " (" + dayName + ")";

        // Build radio options
        ButtonGroup group = new ButtonGroup();
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        int currentSelection = 0;
        JRadioButton[] radios = new JRadioButton[shifts.size() + 1];

        // Option 0: No shift
        radios[0] = new JRadioButton("-- Không --");
        radios[0].setFont(FONT_BODY);
        radios[0].setActionCommand("0");
        group.add(radios[0]);
        radioPanel.add(radios[0]);
        radioPanel.add(Box.createVerticalStrut(4));

        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            String label = s.getName() + "  (" +
                    s.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                    s.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) + ")";
            radios[i + 1] = new JRadioButton(label);
            radios[i + 1].setFont(FONT_BODY);
            radios[i + 1].setActionCommand(String.valueOf(i + 1));
            group.add(radios[i + 1]);
            radioPanel.add(radios[i + 1]);
            if (i < shifts.size() - 1) radioPanel.add(Box.createVerticalStrut(2));

            if (existing != null && shifts.get(i).getId() == existing.getShiftId()) {
                currentSelection = i + 1;
            }
        }
        radios[currentSelection].setSelected(true);

        JScrollPane scroll = new JScrollPane(radioPanel);
        scroll.setPreferredSize(new Dimension(360, 220));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JLabel header = new JLabel("Chọn ca làm:");
        header.setFont(FONT_BOLD);
        header.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setBorder(new EmptyBorder(4, 4, 4, 4));
        content.add(header, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this, content, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int selectedIdx = Integer.parseInt(group.getSelection().getActionCommand());

            if (selectedIdx == 0) {
                // Remove shift
                if (existing != null) {
                    workScheduleController.delete(emp.getId(), existing.getShiftId(), workDate.toString());
                    loadData();
                }
            } else {
                Shift selectedShift = shifts.get(selectedIdx - 1);
                if (existing != null) {
                    if (existing.getShiftId() != selectedShift.getId()) {
                        employeeShiftController.update(emp.getId(), existing.getShiftId(), selectedShift.getId(), workDate.toString());
                        loadData();
                    }
                } else {
                    employeeShiftController.assignShift(emp.getId(), selectedShift.getId(), workDate);
                    loadData();
                }
            }
        }
    }

    // ── Context menu (right-click) ────────────────────────────────────────────
    private void showContextMenu(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (row < 0 || col < 1 || col > 7) return;

        Employee emp = employees.get(row);
        LocalDate workDate = weekStart.plusDays(col - 1);
        WorkSchedule existing = scheduleMap.get(emp.getId() + "_" + workDate);

        JPopupMenu menu = new JPopupMenu();
        menu.setFont(FONT_BODY);

        if (existing != null) {
            JMenuItem editItem = new JMenuItem("✎  Sửa ca làm");
            editItem.setFont(FONT_BODY);
            editItem.addActionListener(ev -> openShiftPicker(row, col));
            menu.add(editItem);

            menu.addSeparator();

            JMenuItem deleteItem = new JMenuItem("✕  Xóa ca làm");
            deleteItem.setFont(FONT_BODY);
            deleteItem.setForeground(BTN_RED);
            deleteItem.addActionListener(ev -> {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Xóa lịch làm của " + emp.getName() + " ngày " + workDate.format(DATE_FMT) + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    workScheduleController.delete(emp.getId(), existing.getShiftId(), workDate.toString());
                    loadData();
                }
            });
            menu.add(deleteItem);
        } else {
            JMenuItem addItem = new JMenuItem("＋  Thêm ca làm");
            addItem.setFont(FONT_BODY);
            addItem.addActionListener(ev -> openShiftPicker(row, col));
            menu.add(addItem);
        }

        menu.show(table, e.getX(), e.getY());
    }

    // ── Load data ─────────────────────────────────────────────────────────────
    private void loadData() {
        employees = employeeController.getAll();
        shifts = shiftController.getAll();

        scheduleMap = new HashMap<>();
        List<WorkSchedule> allSchedules = workScheduleController.getAll();
        LocalDate weekEnd = weekStart.plusDays(6);

        int shiftCount = 0;
        for (WorkSchedule ws : allSchedules) {
            if (!ws.getWorkDate().isBefore(weekStart) && !ws.getWorkDate().isAfter(weekEnd)) {
                String key = ws.getEmployeeId() + "_" + ws.getWorkDate();
                scheduleMap.put(key, ws);
                shiftCount++;
            }
        }

        // Update week label
        LocalDate weekEnd2 = weekStart.plusDays(6);
        weekLabel.setText("Tuần " + weekStart.format(DATE_FMT) + " - " + weekEnd2.format(DATE_FMT));
        shiftCountLabel.setText(shiftCount + " ca đã xếp");

        // Build table data
        tableModel.setRowCount(0);
        tableModel.setColumnHeaders(buildColumnHeaders());

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Employee emp : employees) {
            Object[] rowData = new Object[8];
            rowData[0] = emp.getName();

            for (int d = 0; d < 7; d++) {
                LocalDate day = weekStart.plusDays(d);
                WorkSchedule ws = scheduleMap.get(emp.getId() + "_" + day);
                if (ws != null) {
                    rowData[d + 1] = ws.getShiftName() + "\n" +
                            ws.getStartTime().format(timeFmt) + "-" + ws.getEndTime().format(timeFmt);
                } else {
                    rowData[d + 1] = "";
                }
            }

            tableModel.addRow(rowData);
        }
    }

    private String[] buildColumnHeaders() {
        String[] headers = new String[8];
        headers[0] = "Nhân Viên";
        for (int d = 0; d < 7; d++) {
            LocalDate day = weekStart.plusDays(d);
            String dayName = day.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("vi"));
            headers[d + 1] = dayName + "\n" + day.format(DATE_FMT);
        }
        return headers;
    }

    // ── Export Excel ──────────────────────────────────────────────────────────
    private void exportToExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu file Excel");
        chooser.setSelectedFile(new java.io.File("lich_lam_viec_tuan.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();

        try (
                org.apache.poi.ss.usermodel.Workbook wb =
                        new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                java.io.FileOutputStream fos = new java.io.FileOutputStream(file)
        ) {
            var sheet = wb.createSheet("Lịch Làm Việc Tuần");

            var headerStyle = wb.createCellStyle();
            var hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(
                    new org.apache.poi.xssf.usermodel.XSSFColor(
                            new byte[]{0x33, 0x41, 0x55}, null));
            headerStyle.setFillPattern(
                    org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(
                    org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            String[] cols = buildColumnHeaders();
            var hRow = sheet.createRow(0);
            hRow.setHeightInPoints(22);
            for (int i = 0; i < cols.length; i++) {
                var cell = hRow.createCell(i);
                cell.setCellValue(cols[i].replace("\n", " "));
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
            int ri = 1;
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                var row = sheet.createRow(ri++);
                row.setHeightInPoints(18);
                for (int c = 0; c < cols.length; c++) {
                    Object val = tableModel.getValueAt(r, c);
                    row.createCell(c).setCellValue(val != null ? val.toString() : "");
                }
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            wb.write(fos);
            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thành công!\n" + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Button factory ────────────────────────────────────────────────────────
    private JButton createSmallButton(String text, Color base, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? base.darker() : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD_12);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, height));
        return btn;
    }

    // ── Custom Table Model ────────────────────────────────────────────────────
    private static class WeeklyScheduleModel extends DefaultTableModel {
        private String[] columnHeaders;

        public WeeklyScheduleModel() {
            super(new String[]{"Nhân Viên", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"}, 0);
        }

        public void setColumnHeaders(String[] headers) {
            this.columnHeaders = headers;
            fireTableStructureChanged();
        }

        @Override
        public String getColumnName(int column) {
            return columnHeaders != null ? columnHeaders[column] : super.getColumnName(column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    // ── Day Header Renderer ───────────────────────────────────────────────────
    private class DayHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, col);

            lbl.setBackground(TH_BG);
            lbl.setForeground(TH_FG);
            lbl.setFont(FONT_HEADER);
            lbl.setHorizontalAlignment(CENTER);
            lbl.setBorder(new EmptyBorder(4, 4, 4, 4));

            if (col == 0) {
                lbl.setHorizontalAlignment(LEFT);
                lbl.setBorder(new EmptyBorder(4, 12, 4, 4));
            }

            return lbl;
        }
    }

    // ── Day Cell Renderer ─────────────────────────────────────────────────────
    private class DayCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, col);

            String text = value != null ? value.toString() : "";
            boolean hasShift = !text.isEmpty();
            boolean isToday = false;

            if (col >= 1 && col <= 7) {
                LocalDate day = weekStart.plusDays(col - 1);
                isToday = day.equals(LocalDate.now());
            }

            lbl.setHorizontalAlignment(CENTER);
            lbl.setVerticalAlignment(CENTER);

            if (col == 0) {
                lbl.setHorizontalAlignment(LEFT);
                lbl.setBorder(new EmptyBorder(4, 12, 4, 4));
                lbl.setFont(FONT_BOLD);
                if (isSelected) {
                    lbl.setBackground(ROW_SELECTED);
                    lbl.setForeground(new Color(0x1E40AF));
                } else {
                    lbl.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                    lbl.setForeground(new Color(0x1E293B));
                }
            } else {
                lbl.setBorder(new EmptyBorder(2, 4, 2, 4));
                lbl.setFont(hasShift ? FONT_BOLD_12 : FONT_SMALL);

                if (isSelected) {
                    lbl.setBackground(ROW_SELECTED);
                    lbl.setForeground(new Color(0x1E40AF));
                } else if (hasShift) {
                    lbl.setBackground(SHIFT_BG);
                    lbl.setForeground(SHIFT_FG);
                } else if (isToday) {
                    lbl.setBackground(TODAY_BG);
                    lbl.setForeground(new Color(0x9A3412));
                    lbl.setText("—");
                } else {
                    lbl.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                    lbl.setForeground(new Color(0x94A3B8));
                }
            }

            return lbl;
        }
    }
}
