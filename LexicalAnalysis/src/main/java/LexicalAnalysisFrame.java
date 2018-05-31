package main.java;

import main.java.com.ui.LineNumberHeaderView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: MY.Xu
 * Date: 2018/5/31
 * Time: 1:28
 * Description:
 */
public class LexicalAnalysisFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private JMenuBar jMenuBar;

    // 文件菜单
    private JMenu jMenuFileOperate;
    private JMenuItem jMenuItemOpenFile;
    private JMenuItem jMenuItemCleanCode;

    // 操作菜单
    private JMenu jMenuLexicalAnalysis;
    private JMenuItem jMenuItemLexicalAnalysis;

    // 代码编辑区
    private JScrollPane jScrollPaneCode;
    private JTextArea textAreaCode;

    // Token信息显示区
    private JScrollPane jScrollPaneTokenInfo;
    private JTable tableTokenInfo;

    // 词法错误信息显示区
    private JScrollPane jScrollPaneErrorInfo;
    private JTable tableErrorInfo;

    // 符号表信息区
    private JScrollPane jScrollPaneSymbolInfo;
    private JTable tableSymbolInfo;

    public LexicalAnalysisFrame() {
        // 初始化菜单按钮和代码编辑区、Token信息显示区等四个容器
        initComponents();
    }


    private void initComponents() {

        jMenuBar = new javax.swing.JMenuBar();

        jMenuFileOperate = new javax.swing.JMenu();
        jMenuItemOpenFile = new javax.swing.JMenuItem();
        jMenuItemCleanCode = new javax.swing.JMenuItem();

        jMenuLexicalAnalysis = new javax.swing.JMenu();
        jMenuItemLexicalAnalysis = new javax.swing.JMenuItem();


        jScrollPaneCode = new javax.swing.JScrollPane();
        textAreaCode = new javax.swing.JTextArea();

        jScrollPaneTokenInfo = new javax.swing.JScrollPane();
        tableTokenInfo = new javax.swing.JTable();

        jScrollPaneErrorInfo = new javax.swing.JScrollPane();
        tableErrorInfo = new javax.swing.JTable();

        jScrollPaneSymbolInfo = new javax.swing.JScrollPane();
        tableSymbolInfo = new javax.swing.JTable();

        // 设置关闭模式
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);


        // 设置文件菜单和注册监听事件
        jMenuFileOperate.setText("文件");
        jMenuItemOpenFile.setText("打开文件");
        jMenuItemCleanCode.setText("清除编辑区代码");
        jMenuItemOpenFile.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemOpenFileActionPerformed(e);
            }
        });
        jMenuItemCleanCode.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemCleanCodeActionPerformed(e);
            }
        });
        jMenuFileOperate.add(jMenuItemOpenFile);
        jMenuFileOperate.add(jMenuItemCleanCode);

        // 设置操作菜单和注册监听事件
        jMenuLexicalAnalysis.setText("操作");
        jMenuItemLexicalAnalysis.setText("词法分析");
        jMenuItemLexicalAnalysis.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemLexicalAnalysisFileActionPerformed(e);
            }
        });
        jMenuLexicalAnalysis.add(jMenuItemLexicalAnalysis);

        jMenuBar.add(jMenuFileOperate);
        jMenuBar.add(jMenuLexicalAnalysis);
        setJMenuBar(jMenuBar);

        // 代码编辑区的相关设置
        textAreaCode.setColumns(20);
        textAreaCode.setRows(5);
        jScrollPaneCode.setViewportView(textAreaCode);
        jScrollPaneCode.setRowHeaderView(new LineNumberHeaderView()); // 显示行号

        // 设置Token信息显示区
        tableTokenInfo.setModel(
                new DefaultTableModel(new Object[][]{},
                        new String[]{"Token", "类别", "种别码", "行号"}) {

                    boolean[] canEdit = new boolean[]{false, false, false, false};

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit[columnIndex];
                    }
                }
        );
        jScrollPaneTokenInfo.setViewportView(tableTokenInfo);

        // 设置错误信息显示区
        tableErrorInfo.setModel(
                new DefaultTableModel(new Object[][]{},
                        new String[]{"错误行", "错误信息"}) {

                    boolean[] canEdit = new boolean[]{false, false};

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit[columnIndex];
                    }
                }
        );
        jScrollPaneErrorInfo.setViewportView(tableErrorInfo);

        // 设置符号表信息显示区
        tableSymbolInfo.setModel(
                new DefaultTableModel(new Object[][]{},
                        new String[]{"符号表", "Position"}) {

                    boolean[] canEdit = new boolean[]{false, false};

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit[columnIndex];
                    }
                }
        );
        jScrollPaneSymbolInfo.setViewportView(tableSymbolInfo);

        // 布局设置
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPaneErrorInfo,
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout
                                                                .DEFAULT_SIZE,
                                                        433, Short.MAX_VALUE)
                                        .addComponent(jScrollPaneCode,
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout
                                                                .DEFAULT_SIZE,
                                                        433, Short.MAX_VALUE)
                                ).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jScrollPaneSymbolInfo, 0,0,
                                                        Short.MAX_VALUE)
                                                .addComponent(jScrollPaneTokenInfo,
                                                        javax.swing.GroupLayout
                                                                .DEFAULT_SIZE, 298,
                                                        Short.MAX_VALUE))
                                .addContainerGap())
        );

        layout.setVerticalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                false)
                                                .addComponent(jScrollPaneTokenInfo,
                                                        javax.swing.GroupLayout.Alignment
                                                                .LEADING,
                                                        0, 0, Short.MAX_VALUE)
                                                .addComponent(
                                                        jScrollPaneCode,
                                                        javax.swing.GroupLayout.Alignment
                                                                .LEADING,
                                                        javax.swing.GroupLayout
                                                                .DEFAULT_SIZE,
                                                        317, Short.MAX_VALUE))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(
                                                        jScrollPaneSymbolInfo,
                                                        javax.swing.GroupLayout
                                                                .DEFAULT_SIZE,
                                                        151, Short.MAX_VALUE)
                                                .addComponent(
                                                        jScrollPaneErrorInfo,
                                                        javax.swing.GroupLayout
                                                                .DEFAULT_SIZE,
                                                        151, Short.MAX_VALUE))
                                .addContainerGap()));

        pack();
    }

    private void jMenuItemOpenFileActionPerformed(java.awt.event.ActionEvent evt) {
        FileDialog fileDialog;
        File codeFile;
        main.java.com.ui.Frame frame = null;
        fileDialog = new FileDialog(frame, "Open", FileDialog.LOAD);
        fileDialog.setVisible(true);

        try {
            //将textarea清空
            textAreaCode.setText("");
            codeFile = new File(fileDialog.getDirectory(), fileDialog.getFile());
            FileReader fileReader = new FileReader(codeFile);
            BufferedReader bufferReader = new BufferedReader(fileReader);
            String aline;
            while ((aline = bufferReader.readLine()) != null)

                textAreaCode.append(aline + "\r\n");
            fileReader.close();
            bufferReader.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void jMenuItemCleanCodeActionPerformed(java.awt.event.ActionEvent evt) {
        textAreaCode.setText("");

    }

    private void jMenuItemLexicalAnalysisFileActionPerformed(java.awt.event.ActionEvent evt) {
        String program = textAreaCode.getText();
        // 清除原有行
        DefaultTableModel tableModel1 = (DefaultTableModel) tableTokenInfo.getModel();
        tableModel1.setRowCount(0);
        tableTokenInfo.invalidate();
        DefaultTableModel tableModel2 = (DefaultTableModel) tableErrorInfo.getModel();
        tableModel2.setRowCount(0);
        tableErrorInfo.invalidate();
        DefaultTableModel tableModel3 = (DefaultTableModel) tableSymbolInfo.getModel();
        tableModel3.setRowCount(0);
        tableSymbolInfo.invalidate();

        // 创建词法分析类进行词法分析
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(program,tableTokenInfo,
                tableErrorInfo,tableSymbolInfo);

        lexicalAnalysis.LexAnalysis();

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LexicalAnalysisFrame().setVisible(true);
            }
        });
    }


}
