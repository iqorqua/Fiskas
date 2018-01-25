package com.app.fiskas.fiskas.CustomElements;

/**
 * Created by igorqua on 08.01.2018.
 */

public class FactureItem {

    public String factureName;
    public String factureId;
    public String factureLink;
    public String factureDate;

        public FactureItem(String name, String id, String link, String date){
            factureName = name;
            factureDate = date;
            factureId = id;
            factureLink = link;
        }
}
