package com.example.asus.model;

public class Comment {
    private String ten;
    private String ngay;
    private String comment;
    private String sao;

    public Comment() {
    }

    public Comment(String ten, String ngay, String comment, String sao) {
        this.ten = ten;
        this.ngay = ngay;
        this.comment = comment;
        this.sao = sao;
    }

    public String getSao() {
        return sao;
    }

    public void setSao(String sao) {
        this.sao = sao;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
