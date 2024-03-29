package Game.ItemShop;

import javax.swing.ImageIcon;

public class ShopItem {
    
    private String name;
    private int cost;
    private Runnable onPurchase;
    private ImageIcon itemIcon;

    public ShopItem(String name, int cost, Runnable onPurchase, ImageIcon itemIcon) {
        this.name = name;
        this.cost = cost;
        this.onPurchase = onPurchase;
        this.itemIcon = itemIcon;
    }
    
    public void purchase() {
        onPurchase.run();
    }


    public int getCost() {return cost;}
    public ImageIcon getIcon() {return itemIcon;}
    public String getName() {return name;}
    public Runnable getOnPurchase() {return onPurchase;}



}
