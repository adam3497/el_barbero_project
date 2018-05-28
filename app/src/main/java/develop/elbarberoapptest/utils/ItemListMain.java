package develop.elbarberoapptest.utils;

/**
 * Created by adma9717 on 28/05/18.
 */

public class ItemListMain {

    private String title;
    private String description;
    private int price;
    private String image;

    public ItemListMain(String title, String description, int price, String image){
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
