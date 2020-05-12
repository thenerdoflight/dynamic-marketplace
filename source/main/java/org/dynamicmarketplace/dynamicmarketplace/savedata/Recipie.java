package org.dynamicmarketplace.dynamicmarketplace.savedata;

public class Recipie {
    public String[] items;
    public Double[] amounts;
    private int index = 0;
    public Recipie( int size ) {
        items = new String[size]; amounts = new Double[ size ];
    }
    public void pushItem ( String name, Double amount ){
        items[index] = name;
        amounts[index] = amount;
        index += 1;
    }
}