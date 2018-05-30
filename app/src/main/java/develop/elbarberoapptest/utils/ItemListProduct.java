package develop.elbarberoapptest.utils;

/**
 * Created by adma9717 on 29/05/18.
 */

public class ItemListProduct {
    private String title;
    private int price;
    private String image;

    public ItemListProduct(String title, int price, String image){
        this.title = title;
        this.price = price;
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
