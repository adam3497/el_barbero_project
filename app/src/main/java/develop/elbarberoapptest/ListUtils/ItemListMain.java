package develop.elbarberoapptest.ListUtils;

/**
 * Created by labexp on 18/05/18.
 */

public class ItemListMain {
    private String itemName;
    private String itemDescription;
    private String itemPrice;
    private int itemImage;

    public ItemListMain(String name, String description, String price, int image){
        this.itemName = name;
        this.itemDescription = description;
        this.itemPrice = price;
        this.itemImage = image;
    }


    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public int getItemImage() {
        return itemImage;
    }
}
