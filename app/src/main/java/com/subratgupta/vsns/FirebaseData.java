package com.subratgupta.vsns;

public class FirebaseData {
    private String phone;
    private String header;
    private String link;
    private String qr_link;
    private Boolean editable;
    private String comment_paytm;

    public FirebaseData(){
    }

    public String getPhone() {
        return phone;
    }

    public String getHeader() {
        return header;
    }

    public String getLink() {
        return link;
    }

    public String getQr_link() {
        return qr_link;
    }

    public Boolean getEditable() {
        return editable;
    }

    public String getComment_paytm() {
        return comment_paytm;
    }
}