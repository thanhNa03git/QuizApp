/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package QuizSystem.ui;

import QuizSystem.dao.AnswerDAO;
import QuizSystem.dao.ExamDAO;
import QuizSystem.dao.QuestionDAO;
import QuizSystem.entity.Answer;
import QuizSystem.entity.Exam;
import QuizSystem.entity.Question;
import QuizSystem.utils.AuthOfLecturer;
import QuizSystem.utils.MsgBox;
import QuizSystem.utils.XDate;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author trant
 */
public class ExamDetailJDialog extends javax.swing.JDialog {

    /**
     * Creates new form Exam
     */
    String examCode;
    int maCH = -1;
    byte[] imageOfQuestion;
    int index = -1;

    JRadioButton[] radioButtons;
    JCheckBox[] checkBoxs;
    JTextField[] textFieldsSingleChoice;
    JTextField[] textFieldsMultipleChoice;

    ExamDAO examDAO = new ExamDAO();
    QuestionDAO questionDAO = new QuestionDAO();
    AnswerDAO answerDAO = new AnswerDAO();

    ArrayList<Question> listTemps = new ArrayList<>(); // List được sử dụng để lưu các câu hỏi khi chưa tồn tại đề thi
    ArrayList<Question> listQuestions;
    ArrayList<Answer> listAnswers;

    HashMap<JRadioButton, JTextField> hashSingleChoice = new HashMap<>();
    HashMap<JCheckBox, JTextField> hashMultipleChoice = new HashMap<>();

    public ExamDetailJDialog(java.awt.Frame parent, boolean modal, String examCode) {
        super(parent, modal);
        initComponents();
        this.examCode = examCode;
        this.setLocationRelativeTo(null);
        init();
    }

    public void init() {
        listQuestions = this.getQuestionFromDB();
        fillToTable(examCode == null ? listTemps : listQuestions);

        timePicker1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtStartTime.setText(timePicker1.getSelectedTime());
            }
        });

        timePicker2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtFinishTime.setText(timePicker2.getSelectedTime());
            }
        });

        tableQuestion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                index = tableQuestion.getSelectedRow();
                if (examCode == null) {
                    ExamDetailJDialog.this.writeToFormQuestion(listTemps.get(index));
                } else {
                    ExamDetailJDialog.this.maCH = listQuestions.get(index).getMaCH();
                    ExamDetailJDialog.this.clearFormQuestion();
                    ExamDetailJDialog.this.writeToFormQuestion(listQuestions.get(index));
                }

                updateStatusQuestion();
                tbpDetail.setSelectedIndex(0);
            }
        });

        radioButtons = new JRadioButton[]{rdoOne, rdoTwo, rdoThree, rdoFour};
        checkBoxs = new JCheckBox[]{cbOne, cbTwo, cbThree, cbFour};
        textFieldsSingleChoice = new JTextField[]{txtSingleOne, txtSingleTwo, txtSingleThree, txtSingleFour};
        textFieldsMultipleChoice = new JTextField[]{txtMultiOne, txtMultiTwo, txtMultiThree, txtMultiFour};

        for (int i = 0; i < 4; i++) {
            hashSingleChoice.put(radioButtons[i], textFieldsSingleChoice[i]);
            hashMultipleChoice.put(checkBoxs[i], textFieldsMultipleChoice[i]);
        }

        updateStatusExam();
        updateStatusQuestion();
    }

    public Exam getExamFromForm() {
        String startTime = txtStartTime.getText() + " " + XDate.toString("dd/MM/yyyy", txtStartDate.getDate());
        String finishTime = txtFinishTime.getText() + " " + XDate.toString("dd/MM/yyyy", txtFinishDate.getDate());

        Date start = XDate.toDate("hh:mm a dd/MM/yyyy", startTime);
        Date finish = XDate.toDate("hh:mm a dd/MM/yyyy", finishTime);

        if (start.compareTo(finish) >= 0) {
            MsgBox.alert(this, "Thời gian bắt đầu vè kết thúc không hợp lệ");
            return null;
        }

        Exam exam = new Exam();
        exam.setMaDe(txtExamCode.getText());
        exam.setTenDe(txtTitle.getText());
        exam.setThoiLuong(Integer.parseInt(spnDuration.getValue().toString()));
        exam.setThoiGianBatDau(start);
        exam.setThoiGianKetThuc(finish);
        exam.setMaGV(AuthOfLecturer.lecturer.getMaGV());

        return exam;
    }

    public void writeToFormExam() {
        Exam exam = examDAO.selectByID(examCode);
        txtExamCode.setText(exam.getMaDe());
        txtTitle.setText(exam.getTenDe());
        spnDuration.setValue(exam.getThoiLuong());
        txtStartTime.setText(XDate.toString("hh:mm a", exam.getThoiGianBatDau()));
        txtStartDate.setDate(exam.getThoiGianBatDau());
        txtFinishTime.setText(XDate.toString("hh:mm a", exam.getThoiGianKetThuc()));
        txtFinishDate.setDate(exam.getThoiGianKetThuc());

        updateStatusExam();
    }

    public void updateStatusExam() {
        boolean status = txtExamCode.getText().isEmpty();

        txtExamCode.setEnabled(status);

        btnCreateExam.setEnabled(status);
        btnRemoveExam.setEnabled(!status);
        btnRepairExam.setEnabled(!status);
    }

    public void insertExam() {
        try {
            Exam exam = this.getExamFromForm();
            if (exam == null) {
                return;
            } else {
                examDAO.insert(exam);
            }
            if (!listTemps.isEmpty()) {
                this.insertQuestions();
            }
            this.fillToTable(new ArrayList<>());
            this.clearFormExam();
            this.clearFormQuestion();
            MsgBox.alert(this, "Thêm đề thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Thêm đề không thành công");
        }
    }

    public void removeExam() {
        try {
            examDAO.remove(examCode);
            this.fillToTable(new ArrayList<>());
            this.clearFormExam();
            MsgBox.alert(this, "Xóa đề thành công");
        } catch (SQLException ex) {
            ex.printStackTrace();
            MsgBox.alert(this, "Xóa đề không thành công");
        }
    }

    public void repairExam() {
        try {
            Exam exam = this.getExamFromForm();
            if (exam == null) {
                return;
            } else {
                examDAO.repair(exam);
            }
            MsgBox.alert(this, "Sửa đề thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Sửa đề không thành công");
        }
    }

    public void clearFormExam() {
        txtExamCode.setText("");
        txtTitle.setText("");
        spnDuration.setValue(1);
        txtStartTime.setText("");
        txtFinishTime.setText("");
        txtStartDate.setDate(null);
        txtFinishDate.setDate(null);

        this.updateStatusExam();
    }

    public ArrayList<Question> getQuestionFromDB() {
        ArrayList<Question> list = questionDAO.selectByMaDe(examCode);
        return list;
    }

    public Question getQuestionFromForm() {
        Question question = new Question();
        question.setMaCH(maCH == -1 ? 0 : maCH); // Nếu chưa tồn tại đề thi thì maCH mặc định là 0, ngược lại là maCH lấy từ DB
        question.setTenCH(txtaQuestion.getText());
        question.setHinhAnh(imageOfQuestion == null ? null : imageOfQuestion);
        question.setLoaiCH(tbpQuestion.getSelectedIndex() == 1);
        question.setMaDe(examCode);

        return question;
    }

    public void writeToFormQuestion(Question question) {
        maCH = question.getMaCH();

        txtaQuestion.setText(question.getTenCH());
        if ((imageOfQuestion = question.getHinhAnh()) != null) {
            btnImage.setIcon(new ImageIcon(new ImageIcon(imageOfQuestion).getImage().getScaledInstance(120, 150, Image.SCALE_SMOOTH)));
        } else {
            btnImage.setIcon(new ImageIcon(getClass().getResource("/QuizSystem/icon/image-gallery.png")));
        }

        writeToFormAnswer(question);
        updateStatusQuestion();
    }

    public void updateStatusQuestion() {
        boolean status = maCH == -1;

        btnAddQuestion.setEnabled(status);
        btnRemoveQuestion.setEnabled(!status);
        btnRepairQuestion.setEnabled(!status);
    }

    public void fillToTable(ArrayList<Question> list) {
        DefaultTableModel model = (DefaultTableModel) tableQuestion.getModel();
        model.setRowCount(0);

        for (Question question : list) {
            model.addRow(question.getRecord());
        }
    }

    // Thêm một câu hỏi
    public void insertQuestion() {
        try {
            Question question = this.getQuestionFromForm();
            question.setAnswers(this.getAnswersFromForm());
            if (question.getTenCH().isEmpty()) {
                MsgBox.alert(this, "Thêm câu hỏi không thành công");
                return;
            }

            if (examCode == null) {
                listTemps.add(question);
            } else {
                questionDAO.insert(question);
                maCH = questionDAO.selectQuestion().getMaCH();

                for (Answer answer : this.getAnswersFromForm()) {
                    answerDAO.insert(answer);
                }

                listQuestions = this.getQuestionFromDB();
            }

            this.fillToTable(examCode == null ? listTemps : listQuestions);
            this.clearFormQuestion();
            MsgBox.alert(this, "Thêm câu hỏi thành công");
        } catch (SQLException e) {
            MsgBox.alert(this, "Thêm câu hỏi không thành công");
        }
    }

    // Thêm một danh sách câu hỏi
    public void insertQuestions() throws SQLException {
        int code;
        for (Question question : listTemps) {
            question.setMaDe(txtExamCode.getText());
            questionDAO.insert(question);
            code = questionDAO.selectQuestion().getMaCH();
            for (Answer answer : question.getAnswers()) {
                answer.setMaCH(code);
                answerDAO.insert(answer);
            }
        }
    }

    public void removeQuestion() {
        try {
            if (examCode == null) {
                listTemps.remove(index);
            } else {
                questionDAO.remove(maCH);
                listQuestions = this.getQuestionFromDB();
            }

            this.fillToTable(examCode == null ? listTemps : listQuestions);
            this.clearFormQuestion();
            MsgBox.alert(this, "Xóa câu hỏi thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Xóa câu hỏi thất bại");
        }
    }

    public void repairQuestion() {
        try {
            if (examCode == null) {
                Question question = this.getQuestionFromForm();
                question.setAnswers(this.getAnswersFromForm());
                listTemps.remove(index);
                listTemps.add(index, question);
            } else {
                Question question = this.getQuestionFromForm();
                questionDAO.repair(question);
                maCH = question.getMaCH();
                listAnswers = this.getAnswersFromForm();
                for (Answer answer : listAnswers) {
                    answerDAO.repair(answer);
                }
                listQuestions = this.getQuestionFromDB();
            }

            this.fillToTable(examCode == null ? listTemps : listQuestions);
            this.clearFormQuestion();
            MsgBox.alert(this, "Sửa câu hỏi thành công");
        } catch (SQLException ex) {
            MsgBox.alert(this, "Sửa câu hỏi không thành công");
        }
    }

    public void clearFormQuestion() {
        txtaQuestion.setText("");
        btnImage.setIcon(new ImageIcon(getClass().getResource("/QuizSystem/icon/image-gallery.png")));
        btnGroup.clearSelection();

        for (JCheckBox cb : checkBoxs) {
            cb.setSelected(false);
        }

        for (JTextField txt : textFieldsSingleChoice) {
            txt.setText("");
        }

        for (JTextField txt : textFieldsMultipleChoice) {
            txt.setText("");
        }

        maCH = -1;
        updateStatusQuestion();
        tbpQuestion.setSelectedIndex(0);
    }

    public ArrayList<Answer> getAnswersFromDB(int maCH) {
        ArrayList<Answer> list = answerDAO.selectByMaCH(maCH);
        return list;
    }

    public ArrayList<Answer> getAnswersFromForm() {
        ArrayList<Answer> listInserts = new ArrayList<>();
        ArrayList<Answer> listUpdates = answerDAO.selectByMaCH(maCH);
        int i = 0;
        if (examCode == null) {
            if (tbpQuestion.getSelectedIndex() == 0) {
                for (Map.Entry<JRadioButton, JTextField> entry : hashSingleChoice.entrySet()) {
                    Answer answer = new Answer();
                    answer.setMaDA(0);
                    answer.setNoiDung(entry.getValue().getText());
                    answer.setDungSai(entry.getKey().isSelected());
                    answer.setMaCH(0);

                    listInserts.add(answer);
                }
            } else {
                for (Map.Entry<JCheckBox, JTextField> entry : hashMultipleChoice.entrySet()) {
                    Answer answer = new Answer();
                    answer.setMaDA(0);
                    answer.setNoiDung(entry.getValue().getText());
                    answer.setDungSai(entry.getKey().isSelected());
                    answer.setMaCH(0);

                    listInserts.add(answer);
                }
            }
        } else {
            if (tbpQuestion.getSelectedIndex() == 0) {
                for (Map.Entry<JRadioButton, JTextField> entry : hashSingleChoice.entrySet()) {
                    Answer answer = new Answer();
                    answer.setMaDA(listUpdates == null ? 0 : listUpdates.get(i).getMaDA());
                    answer.setNoiDung(entry.getValue().getText());
                    answer.setDungSai(entry.getKey().isSelected());
                    answer.setMaCH(maCH);

                    i++;
                    listInserts.add(answer);
                }
            } else {
                for (Map.Entry<JCheckBox, JTextField> entry : hashMultipleChoice.entrySet()) {
                    Answer answer = new Answer();
                    answer.setMaDA(listUpdates == null ? 0 : listUpdates.get(i).getMaDA());
                    answer.setNoiDung(entry.getValue().getText());
                    answer.setDungSai(entry.getKey().isSelected());
                    answer.setMaCH(maCH);

                    i++;
                    listInserts.add(answer);
                }
            }
        }

        return listInserts;
    }

    public void writeToFormAnswer(Question question) {
        int i = 0;
        ArrayList<Answer> list = question.getAnswers();
        if (!question.isLoaiCH()) {
            tbpQuestion.setSelectedIndex(0);
            for (Map.Entry<JRadioButton, JTextField> entry : hashSingleChoice.entrySet()) {
                entry.getKey().setSelected(list.get(i).isDungSai());
                entry.getValue().setText(list.get(i).getNoiDung());
                i++;
            }
        } else {
            tbpQuestion.setSelectedIndex(1);
            for (Map.Entry<JCheckBox, JTextField> entry : hashMultipleChoice.entrySet()) {
                entry.getKey().setSelected(list.get(i).isDungSai());
                entry.getValue().setText(list.get(i).getNoiDung());
                i++;
            }
        }
    }

    public ArrayList<Question> getQuestionsFromExcel() throws FileNotFoundException, IOException {
        ArrayList<Question> listQues = new ArrayList<>();
        JFileChooser fc = new JFileChooser("D:/");
        int isSelected = fc.showOpenDialog(null);
        if (isSelected == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            FileInputStream fin = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fin);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

                if (row.getRowNum() == 0 || row.getRowNum() == 1) {
                    continue;
                } else {
                    ArrayList<Answer> listAns = new ArrayList<>();
                    for (int i = 2; i <= 5; i++) {
                        Answer answer = new Answer();
                        answer.setMaDA(0);
                        answer.setNoiDung(row.getCell(i).toString());
                        if (row.getCell(1).toString().contains(String.valueOf(i - 1))) {
                            answer.setDungSai(true);
                        } else {
                            answer.setDungSai(false);
                        }
                        answer.setMaCH(0);

                        listAns.add(answer);
                    }

                    Question question = new Question();
                    question.setMaCH(0);
                    question.setTenCH(row.getCell(0).toString());
                    question.setHinhAnh(null);
                    question.setLoaiCH(row.getCell(1).toString().length() > 1);
                    question.setMaDe(null);
                    question.setAnswers(listAns);

                    listQues.add(question);
                }
                fin.close();
            }
        }
        return listQues;
    }

    public void downloadFile() {
        try {
            String url = null;
            String fileName = null;
            JFileChooser fc = new JFileChooser("D:/");
            int isSelected = fc.showOpenDialog(null);
            if (isSelected == JFileChooser.APPROVE_OPTION) {
                url = "https://drive.google.com/uc?export=download&id=1MFHAi94aI7xnA7nYrnnBriCS_zNaW07G";
                fileName = fc.getSelectedFile().getPath() + ".xlsx";
                URL link = new URL(url);
                InputStream ins = link.openStream();
                FileOutputStream fos = new FileOutputStream(new File(fileName));
                byte[] bytes = new byte[1024];
                int len;
                while ((len = ins.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                ins.close();
                fos.close();
            }
            MsgBox.alert(this, "Tải file thành công.\n" + fileName);
        } catch (UnknownHostException e) {
            System.out.println("Bạn chưa kết nối mạng?");
        } catch (MalformedURLException ex) {
            System.out.println("Tải file không thành công");
        } catch (IOException ex) {
            System.out.println("Tải file không thành công");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timePicker1 = new com.raven.swing.TimePicker();
        timePicker2 = new com.raven.swing.TimePicker();
        btnGroup = new javax.swing.ButtonGroup();
        lbNguoiHoc = new javax.swing.JLabel();
        tbpDetail = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtExamCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        spnDuration = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        txtStartTime = new javax.swing.JTextField();
        btnStartTime = new javax.swing.JButton();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        jPanel12 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txtFinishTime = new javax.swing.JTextField();
        btnFinishTime = new javax.swing.JButton();
        txtFinishDate = new com.toedter.calendar.JDateChooser();
        jPanel13 = new javax.swing.JPanel();
        btnCreateExam = new javax.swing.JButton();
        btnRemoveExam = new javax.swing.JButton();
        btnRepairExam = new javax.swing.JButton();
        btnNewExam = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaQuestion = new javax.swing.JTextArea();
        btnImage = new javax.swing.JButton();
        tbpQuestion = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        rdoOne = new javax.swing.JRadioButton();
        rdoTwo = new javax.swing.JRadioButton();
        rdoThree = new javax.swing.JRadioButton();
        rdoFour = new javax.swing.JRadioButton();
        txtSingleFour = new javax.swing.JTextField();
        txtSingleThree = new javax.swing.JTextField();
        txtSingleTwo = new javax.swing.JTextField();
        txtSingleOne = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        cbOne = new javax.swing.JCheckBox();
        txtMultiOne = new javax.swing.JTextField();
        cbTwo = new javax.swing.JCheckBox();
        txtMultiTwo = new javax.swing.JTextField();
        cbThree = new javax.swing.JCheckBox();
        txtMultiThree = new javax.swing.JTextField();
        cbFour = new javax.swing.JCheckBox();
        txtMultiFour = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        btnDownload = new javax.swing.JButton();
        btnUpload = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        btnAddQuestion = new javax.swing.JButton();
        btnRemoveQuestion = new javax.swing.JButton();
        btnRepairQuestion = new javax.swing.JButton();
        btnNewQuestion = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableQuestion = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QuizSystem - Quản lý đề thi");
        setIconImage(new ImageIcon(getClass().getResource("/QuizSystem/icon/quiz-comic.png")).getImage());

        lbNguoiHoc.setFont(new java.awt.Font("JetBrains Mono", 1, 24)); // NOI18N
        lbNguoiHoc.setForeground(new java.awt.Color(51, 204, 0));
        lbNguoiHoc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNguoiHoc.setText("CHI TIẾT ĐỀ THI");

        tbpDetail.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Đề thi", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("JetBrains Mono", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel1.setText("Mã đề:");

        txtExamCode.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtExamCode.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jLabel2.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel2.setText("Tiêu đề bài kiểm tra:");

        txtTitle.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtTitle.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jLabel5.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel5.setText("Thời gian làm bài (phút):");

        spnDuration.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        spnDuration.setModel(new javax.swing.SpinnerNumberModel(1, 1, 180, 1));

        jLabel3.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel3.setText("Thời gian mở đề:");

        jLabel4.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel4.setText("Thời gian đóng đề:");

        jPanel9.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        txtStartTime.setEditable(false);
        txtStartTime.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtStartTime.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        btnStartTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/ontime.png"))); // NOI18N
        btnStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(txtStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel7);

        txtStartDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        txtStartDate.setDateFormatString("dd-MM-yyyy");
        txtStartDate.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jPanel9.add(txtStartDate);

        jPanel12.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        txtFinishTime.setEditable(false);
        txtFinishTime.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtFinishTime.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        btnFinishTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/offtime.png"))); // NOI18N
        btnFinishTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(txtFinishTime, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinishTime, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFinishTime, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnFinishTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel8);

        txtFinishDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        txtFinishDate.setDateFormatString("dd-MM-yyyy");
        txtFinishDate.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jPanel12.add(txtFinishDate);

        jPanel13.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        btnCreateExam.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnCreateExam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/check.png"))); // NOI18N
        btnCreateExam.setText("Tạo đề");
        btnCreateExam.setPreferredSize(new java.awt.Dimension(111, 30));
        btnCreateExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateExamActionPerformed(evt);
            }
        });
        jPanel13.add(btnCreateExam);

        btnRemoveExam.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnRemoveExam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/trash.png"))); // NOI18N
        btnRemoveExam.setText("Xóa đề");
        btnRemoveExam.setPreferredSize(new java.awt.Dimension(111, 30));
        btnRemoveExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveExamActionPerformed(evt);
            }
        });
        jPanel13.add(btnRemoveExam);

        btnRepairExam.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnRepairExam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/repair.png"))); // NOI18N
        btnRepairExam.setText("Sửa đề");
        btnRepairExam.setPreferredSize(new java.awt.Dimension(111, 30));
        btnRepairExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepairExamActionPerformed(evt);
            }
        });
        jPanel13.add(btnRepairExam);

        btnNewExam.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnNewExam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/eraser.png"))); // NOI18N
        btnNewExam.setText("Làm mới");
        btnNewExam.setPreferredSize(new java.awt.Dimension(111, 35));
        btnNewExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewExamActionPerformed(evt);
            }
        });
        jPanel13.add(btnNewExam);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtExamCode)
                    .addComponent(txtTitle)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(spnDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtExamCode, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Câu hỏi", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("JetBrains Mono", 0, 14))); // NOI18N

        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));
        jScrollPane1.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        txtaQuestion.setColumns(20);
        txtaQuestion.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtaQuestion.setLineWrap(true);
        txtaQuestion.setRows(1);
        jScrollPane1.setViewportView(txtaQuestion);

        btnImage.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        btnImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/QuizSystem/icon/image-gallery.png"))); // NOI18N
        btnImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageActionPerformed(evt);
            }
        });

        tbpQuestion.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        tbpQuestion.setFont(new java.awt.Font("JetBrains Mono", 0, 12)); // NOI18N

        btnGroup.add(rdoOne);
        rdoOne.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        btnGroup.add(rdoTwo);
        rdoTwo.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        btnGroup.add(rdoThree);
        rdoThree.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        btnGroup.add(rdoFour);
        rdoFour.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N

        txtSingleFour.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtSingleFour.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        txtSingleThree.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtSingleThree.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        txtSingleTwo.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtSingleTwo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        txtSingleOne.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtSingleOne.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rdoOne)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSingleOne, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rdoFour)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSingleFour))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rdoTwo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSingleTwo))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(rdoThree)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSingleThree)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rdoOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSingleOne, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rdoTwo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSingleTwo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rdoThree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSingleThree, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rdoFour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSingleFour, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        tbpQuestion.addTab("Chọn một", jPanel5);

        txtMultiOne.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtMultiOne.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        txtMultiTwo.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtMultiTwo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        txtMultiThree.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtMultiThree.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        txtMultiFour.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        txtMultiFour.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(cbOne)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMultiOne, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cbTwo)
                            .addComponent(cbThree)
                            .addComponent(cbFour))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMultiTwo)
                            .addComponent(txtMultiThree)
                            .addComponent(txtMultiFour))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMultiOne, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbTwo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMultiTwo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbThree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMultiThree, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbFour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMultiFour, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        tbpQuestion.addTab("Chọn nhiều", jPanel6);

        jPanel10.setPreferredSize(new java.awt.Dimension(189, 35));
        jPanel10.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        btnDownload.setBackground(new java.awt.Color(248, 255, 39));
        btnDownload.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnDownload.setText("Tệp mẫu");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });
        jPanel10.add(btnDownload);

        btnUpload.setBackground(new java.awt.Color(69, 255, 140));
        btnUpload.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnUpload.setText("Tải lên");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });
        jPanel10.add(btnUpload);

        jPanel11.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        btnAddQuestion.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnAddQuestion.setText("Thêm");
        btnAddQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddQuestionActionPerformed(evt);
            }
        });
        jPanel11.add(btnAddQuestion);

        btnRemoveQuestion.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnRemoveQuestion.setText("Xóa");
        btnRemoveQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveQuestionActionPerformed(evt);
            }
        });
        jPanel11.add(btnRemoveQuestion);

        btnRepairQuestion.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnRepairQuestion.setText("Sửa");
        btnRepairQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepairQuestionActionPerformed(evt);
            }
        });
        jPanel11.add(btnRepairQuestion);

        btnNewQuestion.setFont(new java.awt.Font("JetBrains Mono", 1, 14)); // NOI18N
        btnNewQuestion.setText("Mới");
        btnNewQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewQuestionActionPerformed(evt);
            }
        });
        jPanel11.add(btnNewQuestion);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImage, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbpQuestion)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnImage, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(12, 12, 12)
                .addComponent(tbpQuestion, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel4);

        tbpDetail.addTab("Chi tiết đề thi", jPanel1);

        tableQuestion.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        tableQuestion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã câu hỏi", "Tên câu hỏi", "Loại câu hỏi"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableQuestion.setRowHeight(30);
        jScrollPane2.setViewportView(tableQuestion);
        if (tableQuestion.getColumnModel().getColumnCount() > 0) {
            tableQuestion.getColumnModel().getColumn(0).setPreferredWidth(10);
            tableQuestion.getColumnModel().getColumn(1).setPreferredWidth(400);
            tableQuestion.getColumnModel().getColumn(2).setPreferredWidth(100);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1076, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );

        tbpDetail.addTab("Danh sách câu hỏi", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tbpDetail)
                    .addComponent(lbNguoiHoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNguoiHoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tbpDetail)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartTimeActionPerformed
        timePicker1.showPopup(btnStartTime, btnStartTime.getWidth(), -this.getHeight() / 3);
    }//GEN-LAST:event_btnStartTimeActionPerformed

    private void btnFinishTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishTimeActionPerformed
        timePicker2.showPopup(btnFinishTime, btnFinishTime.getWidth(), -this.getHeight() / 3);
    }//GEN-LAST:event_btnFinishTimeActionPerformed

    private void btnCreateExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateExamActionPerformed
        this.insertExam();
    }//GEN-LAST:event_btnCreateExamActionPerformed

    private void btnRemoveExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveExamActionPerformed
        this.removeExam();
    }//GEN-LAST:event_btnRemoveExamActionPerformed

    private void btnRepairExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepairExamActionPerformed
        this.repairExam();
    }//GEN-LAST:event_btnRepairExamActionPerformed

    private void btnNewExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewExamActionPerformed
        this.clearFormExam();
    }//GEN-LAST:event_btnNewExamActionPerformed

    private void btnImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageActionPerformed
        JFileChooser fc = new JFileChooser("D:/");
        int selected = fc.showOpenDialog(this);
        if (selected == fc.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                imageOfQuestion = Files.readAllBytes(Paths.get(file.getPath()));
                btnImage.setIcon(new ImageIcon(new ImageIcon(imageOfQuestion).getImage().getScaledInstance(120, 150, Image.SCALE_SMOOTH)));
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }
    }//GEN-LAST:event_btnImageActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        this.downloadFile();
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        try {
            listTemps = this.getQuestionsFromExcel();
            for (Question question : listTemps) {
                question.setMaDe(examCode);
            }
            this.fillToTable(listTemps);
            MsgBox.alert(this, "Tải file lên thành công");
        } catch (IOException ex) {
            MsgBox.alert(this, "Tải file lên thất bại");
        }
    }//GEN-LAST:event_btnUploadActionPerformed

    private void btnAddQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddQuestionActionPerformed
        this.insertQuestion();
    }//GEN-LAST:event_btnAddQuestionActionPerformed

    private void btnRemoveQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveQuestionActionPerformed
        this.removeQuestion();
    }//GEN-LAST:event_btnRemoveQuestionActionPerformed

    private void btnRepairQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepairQuestionActionPerformed
        this.repairQuestion();
    }//GEN-LAST:event_btnRepairQuestionActionPerformed

    private void btnNewQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewQuestionActionPerformed
        this.clearFormQuestion();
    }//GEN-LAST:event_btnNewQuestionActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ExamDetailJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ExamDetailJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ExamDetailJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ExamDetailJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                ExamDetailJDialog dialog = new ExamDetailJDialog(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddQuestion;
    private javax.swing.JButton btnCreateExam;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnFinishTime;
    private javax.swing.ButtonGroup btnGroup;
    private javax.swing.JButton btnImage;
    private javax.swing.JButton btnNewExam;
    private javax.swing.JButton btnNewQuestion;
    private javax.swing.JButton btnRemoveExam;
    private javax.swing.JButton btnRemoveQuestion;
    private javax.swing.JButton btnRepairExam;
    private javax.swing.JButton btnRepairQuestion;
    private javax.swing.JButton btnStartTime;
    private javax.swing.JButton btnUpload;
    private javax.swing.JCheckBox cbFour;
    private javax.swing.JCheckBox cbOne;
    private javax.swing.JCheckBox cbThree;
    private javax.swing.JCheckBox cbTwo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbNguoiHoc;
    private javax.swing.JRadioButton rdoFour;
    private javax.swing.JRadioButton rdoOne;
    private javax.swing.JRadioButton rdoThree;
    private javax.swing.JRadioButton rdoTwo;
    private javax.swing.JSpinner spnDuration;
    private javax.swing.JTable tableQuestion;
    private javax.swing.JTabbedPane tbpDetail;
    private javax.swing.JTabbedPane tbpQuestion;
    private com.raven.swing.TimePicker timePicker1;
    private com.raven.swing.TimePicker timePicker2;
    private javax.swing.JTextField txtExamCode;
    private com.toedter.calendar.JDateChooser txtFinishDate;
    private javax.swing.JTextField txtFinishTime;
    private javax.swing.JTextField txtMultiFour;
    private javax.swing.JTextField txtMultiOne;
    private javax.swing.JTextField txtMultiThree;
    private javax.swing.JTextField txtMultiTwo;
    private javax.swing.JTextField txtSingleFour;
    private javax.swing.JTextField txtSingleOne;
    private javax.swing.JTextField txtSingleThree;
    private javax.swing.JTextField txtSingleTwo;
    private com.toedter.calendar.JDateChooser txtStartDate;
    private javax.swing.JTextField txtStartTime;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JTextArea txtaQuestion;
    // End of variables declaration//GEN-END:variables
}
