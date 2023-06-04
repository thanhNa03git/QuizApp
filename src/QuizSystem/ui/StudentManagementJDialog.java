/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package QuizSystem.ui;

import QuizSystem.dao.StudentDAO;
import QuizSystem.entity.Student;
import QuizSystem.utils.MsgBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author trant
 */
public class StudentManagementJDialog extends javax.swing.JDialog {

    /**
     * Creates new form StudentManagementJDialog
     */
    int index = -1;
    byte[] photoCard; // Ảnh được cập nhật hay không đều thông qua photoCard
    ArrayList<Student> listStudents;
    StudentDAO studentDAO = new StudentDAO();

    public StudentManagementJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        init();
    }

    public void init() {
        listStudents = getStudentFromDB();

        tbStudent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                index = tbStudent.getSelectedRow();
                writeToForm(listStudents.get(index));
                updateStatus();
                tabbedPane.setSelectedIndex(0);
            }
        });

        fillToTable();

        updateStatus();
    }

    public ArrayList<Student> getStudentFromDB() {
        ArrayList<Student> list = studentDAO.selectAll();
        return list;
    }

    public Student getStudentFromForm() {
        Student st = new Student();
        st.setMaSV(txtMaSV.getText());
        st.setTenSV(txtTenSV.getText());
        st.setTaiKhoan(txtTaiKhoan.getText());
        st.setMatKhau(new String(txtMatKhau.getPassword()));
        st.setNgaySinh(txtNgaySinh.getDate());
        st.setHinhAnh(photoCard);
        return st;
    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tbStudent.getModel();
        model.setRowCount(0);
        listStudents = this.getStudentFromDB();
        for (Student st : listStudents) {
            model.addRow(st.record());
        }

        updateStatus();
    }

    public void writeToForm(Student st) {
        txtMaSV.setText(st.getMaSV());
        txtTenSV.setText(st.getTenSV());
        txtTaiKhoan.setText(st.getTaiKhoan());
        txtMatKhau.setText(st.getMatKhau());
        txtNgaySinh.setDate(st.getNgaySinh());
        btnHinhAnh.setIcon(new ImageIcon(photoCard = st.getHinhAnh()));

        updateStatus();
    }

    public void updateStatus() {
        boolean isEdit = index >= 0;
        boolean isFirst = index == 0 || listStudents.isEmpty();
        boolean isLast = index == listStudents.size() - 1;

        txtMaSV.setEnabled(!isEdit);

        btnAdd.setEnabled(!isEdit);
        btnRemove.setEnabled(isEdit);
        btnRepair.setEnabled(isEdit);

        btnFirst.setEnabled(!isFirst);
        btnPrev.setEnabled(!listStudents.isEmpty());
        btnNext.setEnabled(!listStudents.isEmpty());
        btnLast.setEnabled(!isLast);

    }

    public void insertStudent() {
        try {
            Student st = this.getStudentFromForm();
            studentDAO.insert(st);
            fillToTable();
            clearForm();
            MsgBox.alert(this, "Thêm sinh viên thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Thêm sinh viên không thành công");
        }
    }

    public void removeStudent() {
        try {
            String maSV = txtMaSV.getText();
            studentDAO.remove(maSV);
            fillToTable();
            clearForm();
            MsgBox.alert(this, "Xóa sinh viên thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Xóa sinh viên không thành công");
        }
    }

    public void repairStudent() {
        try {
            Student st = this.getStudentFromForm();
            studentDAO.repair(st);
            fillToTable();
            clearForm();
            MsgBox.alert(this, "Sửa thông tin sinh viên thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Sửa thông tin sinh viên không thành công");
        }
    }

    public void clearForm() {
        txtMaSV.setText("");
        txtTenSV.setText("");
        txtTaiKhoan.setText("");
        txtMatKhau.setText("");
        txtNgaySinh.setDate(null);
        btnHinhAnh.setIcon(new ImageIcon(getClass().getResource("/QuizSystem/icon/picture.png")));

        index = -1;
        updateStatus();
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
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        btnHinhAnh = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnRepair = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMaSV = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTaiKhoan = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtTenSV = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JPasswordField();
        chbDisplayPassword = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        txtNgaySinh = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbStudent = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Đào tạo - Quản lý sinh viên");
        setIconImage(new ImageIcon(getClass().getResource("/QuizSystem/icon/quiz-comic.png")).getImage());

        lbNguoiHoc.setFont(new java.awt.Font("JetBrains Mono", 1, 24)); // NOI18N
        lbNguoiHoc.setForeground(new java.awt.Color(51, 204, 0));
        lbNguoiHoc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNguoiHoc.setText("QUẢN LÝ SINH VIÊN");

        tabbedPane.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N

        btnHinhAnh.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        btnHinhAnh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/picture.png"))); // NOI18N
        btnHinhAnh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHinhAnhActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel1.setText("Hình ảnh:");

        btnAdd.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        btnAdd.setText("Thêm");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        btnRemove.setText("Xóa");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnRepair.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        btnRepair.setText("Sửa");
        btnRepair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepairActionPerformed(evt);
            }
        });

        btnNew.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        btnNew.setText("Mới");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnFirst.setFont(new java.awt.Font("MV Boli", 1, 12)); // NOI18N
        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrev.setFont(new java.awt.Font("MV Boli", 1, 12)); // NOI18N
        btnPrev.setText("<<");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setFont(new java.awt.Font("MV Boli", 1, 12)); // NOI18N
        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setFont(new java.awt.Font("MV Boli", 1, 12)); // NOI18N
        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jLabel2.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel2.setText("Mã sinh viên:");

        txtMaSV.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtMaSV.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jLabel4.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel4.setText("Tài khoản:");

        txtTaiKhoan.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtTaiKhoan.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaSV)
                    .addComponent(txtTaiKhoan)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(0, 274, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaSV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTaiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel4);

        jLabel3.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel3.setText("Tên sinh viên:");

        txtTenSV.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtTenSV.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jLabel5.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel5.setText("Mật khẩu:");

        txtMatKhau.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtMatKhau.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        chbDisplayPassword.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        chbDisplayPassword.setText("Hiện mật khẩu");
        chbDisplayPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbDisplayPasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMatKhau)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(chbDisplayPassword))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5))
                                .addGap(0, 259, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addComponent(txtTenSV)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenSV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chbDisplayPassword)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel5);

        jLabel6.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel6.setText("Ngày sinh:");

        txtNgaySinh.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));
        txtNgaySinh.setDateFormatString("dd-MM-yyyy");
        txtNgaySinh.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(btnHinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtNgaySinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRepair, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFirst)
                    .addComponent(btnPrev)
                    .addComponent(btnNext)
                    .addComponent(btnLast)
                    .addComponent(btnNew)
                    .addComponent(btnRepair)
                    .addComponent(btnRemove)
                    .addComponent(btnAdd)))
        );

        tabbedPane.addTab("Cập nhật", jPanel1);

        tbStudent.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        tbStudent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sinh viên", "Tên sinh viên", "Tên đăng nhập", "Mật khẩu", "Ngày sinh"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbStudent.setRowHeight(30);
        jScrollPane1.setViewportView(tbStudent);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Danh sách", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbNguoiHoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNguoiHoc)
                .addGap(18, 18, 18)
                .addComponent(tabbedPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        this.insertStudent();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        this.removeStudent();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnRepairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepairActionPerformed
        this.repairStudent();
    }//GEN-LAST:event_btnRepairActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        this.clearForm();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        index = 0;
        writeToForm(listStudents.get(index));
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        --index;
        if (index < 0) {
            index = listStudents.size() - 1;
        }
        writeToForm(listStudents.get(index));
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        ++index;
        if (index == listStudents.size()) {
            index = 0;
        }
        writeToForm(listStudents.get(index));
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        index = listStudents.size() - 1;
        writeToForm(listStudents.get(index));
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnHinhAnhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHinhAnhActionPerformed
        JFileChooser fc = new JFileChooser("D:/");
        int selected = fc.showOpenDialog(this);
        if (selected == fc.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                photoCard = Files.readAllBytes(Paths.get(file.getPath()));
                btnHinhAnh.setIcon(new ImageIcon(photoCard));
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }
    }//GEN-LAST:event_btnHinhAnhActionPerformed

    private void chbDisplayPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbDisplayPasswordActionPerformed
        if (chbDisplayPassword.isSelected()) {
            txtMatKhau.setEchoChar((char) 0);
        } else {
            txtMatKhau.setEchoChar('*');
        }
    }//GEN-LAST:event_chbDisplayPasswordActionPerformed

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
            java.util.logging.Logger.getLogger(StudentManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StudentManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StudentManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StudentManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                StudentManagementJDialog dialog = new StudentManagementJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnHinhAnh;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnRepair;
    private javax.swing.JCheckBox chbDisplayPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbNguoiHoc;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tbStudent;
    private javax.swing.JTextField txtMaSV;
    private javax.swing.JPasswordField txtMatKhau;
    private com.toedter.calendar.JDateChooser txtNgaySinh;
    private javax.swing.JTextField txtTaiKhoan;
    private javax.swing.JTextField txtTenSV;
    // End of variables declaration//GEN-END:variables
}
