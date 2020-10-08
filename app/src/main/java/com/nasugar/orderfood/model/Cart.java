package com.nasugar.orderfood.model;

public class Cart {
    private String tenMon;
    private String linkAnh;
    private long giaMon;
    private long soluong;
    private  long tongTien;

    public Cart() {
    }

    public Cart(String tenMon, String linkAnh, long giaMon, long soluong, long tongTien) {
        this.tenMon = tenMon;
        this.linkAnh = linkAnh;
        this.giaMon = giaMon;
        this.soluong = soluong;
        this.tongTien = tongTien;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }

    public long getGiaMon() {
        return giaMon;
    }

    public void setGiaMon(long giaMon) {
        this.giaMon = giaMon;
    }

    public long getSoluong() {
        return soluong;
    }

    public void setSoluong(long soluong) {
        this.soluong = soluong;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "tenMon='" + tenMon + '\'' +
                ", linkAnh='" + linkAnh + '\'' +
                ", giaMon=" + giaMon +
                ", soluong=" + soluong +
                ", tongTien=" + tongTien +
                '}';
    }
}
