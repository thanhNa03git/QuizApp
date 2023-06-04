package QuizSystem.entity;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author trant
 */
public class Answer {

    private int maDA;
    private String noiDung;
    private boolean dungSai;
    private int maCH;

    public Answer() {
    }

    public Answer(int maDA, String noiDung, boolean dungSai, int maCH) {
        this.maDA = maDA;
        this.noiDung = noiDung;
        this.dungSai = dungSai;
        this.maCH = maCH;
    }

    public int getMaDA() {
        return maDA;
    }

    public void setMaDA(int maDA) {
        this.maDA = maDA;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public boolean isDungSai() {
        return dungSai;
    }

    public void setDungSai(boolean dungSai) {
        this.dungSai = dungSai;
    }

    public int getMaCH() {
        return maCH;
    }

    public void setMaCH(int maCH) {
        this.maCH = maCH;
    }

    @Override
    public String toString() {
        return this.getMaDA() + " " + this.getNoiDung() + " " + this.isDungSai() + " " + this.getMaCH();
    }
}
