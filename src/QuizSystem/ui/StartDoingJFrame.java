/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package QuizSystem.ui;

import QuizSystem.dao.AnswerDAO;
import QuizSystem.dao.ExamDAO;
import QuizSystem.dao.QuestionDAO;
import QuizSystem.dao.ResultDAO;
import QuizSystem.dao.StudentDAO;
import QuizSystem.otherUI.MultipleChoice;
import QuizSystem.otherUI.SingleChoice;
import QuizSystem.entity.Exam;
import QuizSystem.entity.Question;
import QuizSystem.entity.Result;
import QuizSystem.entity.Student;
import QuizSystem.utils.AuthOfStudent;
import QuizSystem.utils.MsgBox;
import QuizSystem.utils.XDate;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author trant
 */
public class StartDoingJFrame extends javax.swing.JFrame {

    /**
     * Creates new form StartDoingJDialog
     */
    private int index = 0;

    Timer timer;
    private int minute;
    private int second = 0;
    private final String template = "mm:ss";

    private String examCode;
    Exam exam;

    ExamDAO examDAO = new ExamDAO();
    QuestionDAO questionDAO = new QuestionDAO();
    AnswerDAO answerDAO = new AnswerDAO();
    StudentDAO studentDAO = new StudentDAO();
    ResultDAO resultDAO = new ResultDAO();

    ArrayList<Question> listQuestions;
    ArrayList<JPanel> listEmbeds = new ArrayList<>();

    public StartDoingJFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    public StartDoingJFrame(String examCode) {
        initComponents();
        this.examCode = examCode;
        this.setLocationRelativeTo(null);
        init();
    }

    public void init() {
        listQuestions = this.getQuestionsFromDB();

        this.writeInfo();
        this.fillToTable();
        this.fillToFromQuestion();
        this.setPosition(index);
        this.updateStatus();

        tableQuestion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                index = tableQuestion.getSelectedRow();
                StartDoingJFrame.this.setPosition(index);
                StartDoingJFrame.this.updateStatus();
            }
        });
    }

    public Exam getExamFromDB() {
        Exam exam = examDAO.selectByID(examCode);
        return exam;
    }

    public Student getStudentFromDB() {
        Student student = studentDAO.selectByID(AuthOfStudent.student.getMaSV());
        return student;
    }

    public ArrayList<Question> getQuestionsFromDB() {
        ArrayList<Question> list = questionDAO.selectByMaDe(examCode);
        return list;
    }

    public void setInterval() {
        if (minute == 0 && second == 0) {
            timer.cancel();
        } else {
            if (second == 0) {
                second = 59;
                minute--;
            } else {
                second--;
            }
        }
    }

    public String render() {
        this.setInterval();
        String m = String.valueOf(minute);
        if (minute < 10) {
            m = "0" + m;
        }

        String s = String.valueOf(second);
        if (second < 10) {
            s = "0" + s;
        }

        if (minute == 0 && second == 0) {
            try {
                MsgBox.alert(this, "Đã hết thời gian làm bài!");
                Result result = this.insertResult();
                resultDAO.insert(result);
                this.dispose();
                timer.cancel();
                return "";
            } catch (SQLException ex) {
                throw new RuntimeException();
            }
        }

        return template.replaceAll("mm", m).replaceAll("ss", s);
    }

    public void timeRemaining() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                lbTimeRemaining.setText(render());
            }
        }, 0, 1000);
    }

    public void writeInfo() {
        exam = this.getExamFromDB();
        lbTitle.setText(exam.getTenDe());
        lbDuration.setText("Thời gian: " + exam.getThoiLuong() + " phút");
        lbTestDate.setText("Ngày thi: " + XDate.toString("dd-MM-yyyy", new Date()));

        this.minute = exam.getThoiLuong();

        Student student = this.getStudentFromDB();
        lbPhotoCard.setIcon(new ImageIcon(new ImageIcon(student.getHinhAnh()).getImage().getScaledInstance(lbPhotoCard.getWidth(), lbPhotoCard.getHeight(), Image.SCALE_SMOOTH)));
        lbMaSV.setText(student.getMaSV());
        lbTenSV.setText(student.getTenSV());
        lbNgaySinh.setText(XDate.toString("dd-MM-yyyy", student.getNgaySinh()));
        this.timeRemaining();
    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tableQuestion.getModel();

        for (int i = 0; i < listQuestions.size(); i++) {
            model.addRow(new Object[]{i + 1});
        }
    }

    public void updateToTable() {

    }

    public void fillToFromQuestion() {
        for (Question question : listQuestions) {
            if (!question.isLoaiCH()) {
                SingleChoice singleChoice = new SingleChoice();
                singleChoice.getTxtaQuestion().setText(question.getTenCH());
                if (question.getHinhAnh() != null) {
                    singleChoice.getLbImage().setIcon(new ImageIcon(new ImageIcon(question.getHinhAnh()).getImage().getScaledInstance(125, 135, Image.SCALE_SMOOTH)));
                } else {
                    singleChoice.getLbImage().setIcon(null);
                }
                for (int i = 0; i < question.getAnswers().size(); i++) {
                    singleChoice.getRdos()[i].setText(question.getAnswers().get(i).getNoiDung());
                }
                listEmbeds.add(singleChoice);
                panelQuestion.add(singleChoice);
            } else {
                MultipleChoice multipleChoice = new MultipleChoice();
                multipleChoice.getTxtaQuestion().setText(question.getTenCH());
                if (question.getHinhAnh() != null) {
                    multipleChoice.getLbImage().setIcon(new ImageIcon(new ImageIcon(question.getHinhAnh()).getImage().getScaledInstance(125, 135, Image.SCALE_SMOOTH)));
                } else {
                    multipleChoice.getLbImage().setIcon(null);
                }
                for (int i = 0; i < question.getAnswers().size(); i++) {
                    multipleChoice.getCbs()[i].setText(question.getAnswers().get(i).getNoiDung());
                }
                listEmbeds.add(multipleChoice);
                panelQuestion.add(multipleChoice);
            }
        }
    }

    public void updateStatus() {
        boolean first = index == 0;
        boolean last = index == listQuestions.size() - 1;
        btnFirst.setEnabled(!first);
        btnLast.setEnabled(!last);
    }

    public void setPosition(int index) {
        for (int i = 0; i < listQuestions.size(); i++) {
            if (index == i) {
                listEmbeds.get(i).setVisible(true);
            } else {
                listEmbeds.get(i).setVisible(false);
            }
        }

        tableQuestion.setRowSelectionInterval(index, index);
    }

    public int getCorrect() {
        int count = 0;
        for (int i = 0; i < listQuestions.size(); i++) {
            if (listEmbeds.get(i) instanceof SingleChoice) {
                SingleChoice singleChoice = (SingleChoice) listEmbeds.get(i);
                String correct = answerDAO.selectByDungSai(listQuestions.get(i).getMaCH());
                if (correct.equals(singleChoice.getSelected())) {
                    count++;
                }
            } else {
                MultipleChoice multipleChoice = (MultipleChoice) listEmbeds.get(i);
                String correct = answerDAO.selectByDungSai(listQuestions.get(i).getMaCH());
                if (correct.equals(multipleChoice.getSelected())) {
                    count++;
                }
            }
        }

        return count;
    }

    public Result insertResult() {
        int correct = this.getCorrect();
        float score = (float) (Math.floor((10 * 10 * correct / listQuestions.size())) / 10);
        Result result = new Result();
        result.setMaKQ(0);
        result.setMaSV(AuthOfStudent.student.getMaSV());
        result.setTenSV(AuthOfStudent.student.getTenSV());
        result.setMaDe(this.examCode);
        result.setTenDe(exam.getTenDe());
        result.setSoCauDung(correct);
        result.setDiem(score);
        result.setTongThoiGianThi(exam.getThoiLuong() - this.minute);
        result.setNgayThi(new Date());

        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelInfo = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        lbDuration = new javax.swing.JLabel();
        lbTestDate = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lbPhotoCard = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lbMaSV = new javax.swing.JLabel();
        lbTenSV = new javax.swing.JLabel();
        lbNgaySinh = new javax.swing.JLabel();
        lbTimeRemaining = new javax.swing.JLabel();
        panelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableQuestion = new javax.swing.JTable();
        panelQuestion = new javax.swing.JPanel();
        btnFinish = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QuizSystem - Bài Thi");
        setIconImage(new ImageIcon(getClass().getResource("/QuizSystem/icon/quiz-comic.png")).getImage());

        panelInfo.setBackground(new java.awt.Color(255, 255, 0));
        panelInfo.setLayout(new java.awt.GridLayout(1, 0));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true), "Thông tin bài thi", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("JetBrains Mono", 1, 12))); // NOI18N

        lbTitle.setFont(new java.awt.Font("JetBrains Mono", 1, 20)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(255, 0, 0));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText("KIỂM TRA 15 PHÚT MÔN ĐỊA LÝ");

        lbDuration.setFont(new java.awt.Font("JetBrains Mono", 1, 20)); // NOI18N
        lbDuration.setForeground(new java.awt.Color(255, 0, 0));
        lbDuration.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDuration.setText("Thời gian: 15 phút");

        lbTestDate.setFont(new java.awt.Font("JetBrains Mono", 1, 20)); // NOI18N
        lbTestDate.setForeground(new java.awt.Color(255, 0, 0));
        lbTestDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTestDate.setText("Ngày thi: 26-08-2023");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbTestDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbDuration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lbTitle)
                .addGap(18, 18, 18)
                .addComponent(lbDuration)
                .addGap(18, 18, 18)
                .addComponent(lbTestDate)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        panelInfo.add(jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true), "Thông tin sinh viên", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("JetBrains Mono", 1, 12))); // NOI18N

        lbPhotoCard.setBackground(new java.awt.Color(153, 255, 153));
        lbPhotoCard.setOpaque(true);

        jLabel5.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        jLabel5.setText("Mã sinh viên:");

        jLabel6.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        jLabel6.setText("Tên sinh viên:");

        jLabel7.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        jLabel7.setText("Ngày sinh:");

        jLabel8.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        jLabel8.setText("Thời gian còn lại:");

        lbMaSV.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        lbMaSV.setText("lbMaSV");

        lbTenSV.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        lbTenSV.setText("lbTenSV");

        lbNgaySinh.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        lbNgaySinh.setText("lbNgaySinh");

        lbTimeRemaining.setFont(new java.awt.Font("JetBrains Mono", 1, 22)); // NOI18N
        lbTimeRemaining.setForeground(new java.awt.Color(255, 0, 0));
        lbTimeRemaining.setText("remaining");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbPhotoCard, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(lbMaSV))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(lbTenSV))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(lbNgaySinh))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(lbTimeRemaining)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbPhotoCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbMaSV))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbTenSV))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbNgaySinh))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbTimeRemaining, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)))
                .addContainerGap())
        );

        panelInfo.add(jPanel5);

        panelTable.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true), "Danh sách câu hỏi", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("JetBrains Mono", 1, 12))); // NOI18N

        tableQuestion.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N
        tableQuestion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Câu hỏi"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableQuestion.setRowHeight(25);
        jScrollPane1.setViewportView(tableQuestion);

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelQuestion.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true), "Câu hỏi", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("JetBrains Mono", 1, 12))); // NOI18N
        panelQuestion.setLayout(new javax.swing.OverlayLayout(panelQuestion));

        btnFinish.setBackground(new java.awt.Color(0, 255, 204));
        btnFinish.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnFinish.setForeground(new java.awt.Color(255, 0, 0));
        btnFinish.setText("Nộp bài");
        btnFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishActionPerformed(evt);
            }
        });

        btnFirst.setFont(new java.awt.Font("MV Boli", 1, 12)); // NOI18N
        btnFirst.setText("I<");
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
        btnLast.setText(">I");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelQuestion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnFinish)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelQuestion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFinish)
                    .addComponent(btnFirst)
                    .addComponent(btnPrev)
                    .addComponent(btnNext)
                    .addComponent(btnLast))
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed
        if (MsgBox.confirm(this, "Bạn chắc chắn muốn nộp bài?")) {
            try {
                timer.cancel();
                Result result = this.insertResult();
                resultDAO.insert(result);
                MsgBox.alert(this, "Nộp bài thành công");
                this.dispose();
            } catch (SQLException ex) {
                MsgBox.alert(this, "Lỗi hệ thống");
            }
        }
    }//GEN-LAST:event_btnFinishActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        index = 0;
        setPosition(index);
        updateStatus();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        index--;
        if (index < 0) {
            index = listEmbeds.size() - 1;
        }
        setPosition(index);
        updateStatus();
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        index++;
        if (index == listEmbeds.size()) {
            index = 0;
        }
        setPosition(index);
        updateStatus();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        index = listEmbeds.size() - 1;
        setPosition(index);
        updateStatus();
    }//GEN-LAST:event_btnLastActionPerformed

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
            java.util.logging.Logger.getLogger(StartDoingJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartDoingJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartDoingJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartDoingJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartDoingJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbDuration;
    private javax.swing.JLabel lbMaSV;
    private javax.swing.JLabel lbNgaySinh;
    private javax.swing.JLabel lbPhotoCard;
    private javax.swing.JLabel lbTenSV;
    private javax.swing.JLabel lbTestDate;
    private javax.swing.JLabel lbTimeRemaining;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelQuestion;
    private javax.swing.JPanel panelTable;
    private javax.swing.JTable tableQuestion;
    // End of variables declaration//GEN-END:variables
}
