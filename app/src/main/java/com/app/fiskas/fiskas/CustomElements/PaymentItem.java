package com.app.fiskas.fiskas.CustomElements;

/**
 * Created by igorqua on 15.10.2017.
 */

public class PaymentItem {
    public String paymentNameTitle;
    public String paymentNameSubTitle;
    public double price;
    public String m_tag;

    public PaymentItem(String name_p, String description_p, double price_p, String tag){
        paymentNameTitle = name_p;
        paymentNameSubTitle = description_p;
        price = price_p;
        m_tag = tag;
    }
}
