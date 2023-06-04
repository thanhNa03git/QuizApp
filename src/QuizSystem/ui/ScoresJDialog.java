/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package QuizSystem.ui;

import QuizSystem.dao.ExamDAO;
import QuizSystem.dao.ResultDAO;
import QuizSystem.entity.Exam;
import QuizSystem.entity.Result;
import QuizSystem.utils.MsgBox;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author trant
 */
public class ScoresJDialog extends javax.swing.JDialog {

    /**
     * Creates new form ScoresJDialog
     */
    ExamDAO examDAO = new ExamDAO();
    ResultDAO resultDAO = new ResultDAO();

    ArrayList<Exam> listExams;
    ArrayList<Result> listResults;

    public ScoresJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        init();
    }

    public void init() {
        fillToCBBExam();
        fillToTableResult();
    }

    public void fillToCBBExam() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbExam.getModel();
        model.removeAllElements();

        listExams = examDAO.selectAll();
        listExams = listExams != null ? listExams : new ArrayList<>(); // Kiểm tra list lấy từ DB có null hay không

        for (Exam exam : listExams) {
            model.addElement(exam);
        }
    }

    public void fillToTableResult() {
        DefaultTableModel model = (DefaultTableModel) tableScore.getModel();
        model.setRowCount(0);

        if (cbbExam.getItemCount() == 0) return;
        Exam exam = (Exam) cbbExam.getSelectedItem();
        listResults = resultDAO.selectByMaDe(exam.getMaDe());

        for (Result result : listResults) {
            model.addRow(this.getResultOfExam(result));
        }
    }

    public Object[] getResultOfExam(Result result) {
        int quantity = new ExamDAO().selectByID(result.getMaDe()).getSoLuongCH();
        return new Object[]{result.getMaSV(), result.getTenSV(), result.getSoCauDung() + "/" + quantity, result.getTongThoiGianThi(), result.getDiem()};
    }

    public Workbook printToExcel(ArrayList<Result> list) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet workSheet = workbook.createSheet(((Exam) cbbExam.getSelectedItem()).toString());
        XSSFRow row = workSheet.createRow(1);
        XSSFCell cell;
        String[] titles = {"Mã sinh viên", "Tên sinh viên", "Số câu đúng", "Tổng thời gian (phút)", "Điểm"};
        for (int i = 0; i < titles.length; i++) {
            workSheet.setColumnWidth(i, 5000);
            cell = row.createCell(i);
            cell.setCellValue(titles[i]);
        }
        int rowID = 3;
        for (int i = 0; i < list.size(); i++) {
            row = workSheet.createRow(rowID++);

            int cellID = 0;
            for (Object value : this.getResultOfExam(list.get(i))) {
                XSSFCell c = row.createCell(cellID++);
                c.setCellValue(String.valueOf(value));
            }
        }
        return workbook;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbNguoiHoc = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cbbExam = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableScore = new javax.swing.JTable();
        btnToExcel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QuizSystem - Quản lý điểm");
        setIconImage(new ImageIcon(getClass().getResource("/QuizSystem/icon/quiz-comic.png")).getImage());

        lbNguoiHoc.setFont(new java.awt.Font("JetBrains Mono", 1, 24)); // NOI18N
        lbNguoiHoc.setForeground(new java.awt.Color(51, 204, 0));
        lbNguoiHoc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNguoiHoc.setText("BẢNG ĐIỂM");

        jLabel1.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        jLabel1.setText("Tên bài thi:");

        cbbExam.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        cbbExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbExamActionPerformed(evt);
            }
        });

        tableScore.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        tableScore.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sinh viên", "Tên sinh viên", "Số câu đúng", "Tổng thời gian (phút)", "Điểm"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableScore.setRowHeight(25);
        jScrollPane1.setViewportView(tableScore);

        btnToExcel.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        btnToExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/excel.png"))); // NOI18N
        btnToExcel.setText("Ghi file");
        btnToExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbNguoiHoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbbExam, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnToExcel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNguoiHoc)
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbbExam, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnToExcel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbbExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbExamActionPerformed
        this.fillToTableResult();
    }//GEN-LAST:event_cbbExamActionPerformed

    private void btnToExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToExcelActionPerformed
        JFileChooser fc = new JFileChooser();
        int isSelected = fc.showOpenDialog(this);
        if (isSelected == fc.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                FileOutputStream fout = new FileOutputStream(file + ".xlsx");
                this.printToExcel(listResults).write(fout);
                fout.close();
                MsgBox.alert(this, "Xuất file thành công.\n" + file + ".xlsx");
            } catch (IOException ex) {
                MsgBox.alert(this, "Xuất file không thành công");
            }
        }
    }//GEN-LAST:event_btnToExcelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScoresJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScoresJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScoresJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScoresJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ScoresJDialog dialog = new ScoresJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnToExcel;
    private javax.swing.JComboBox<String> cbbExam;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbNguoiHoc;
    private javax.swing.JTable tableScore;
    // End of variables declaration//GEN-END:variables
}
