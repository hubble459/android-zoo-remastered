package nl.saxion.quentin.myzooremastered.models;

/**
 * Shop item
 * Could be food or an animal
 */
public class ShopItem {
    private final boolean isAnimal;
    private String itemName;
    private int price;
    private int imageId;
    private int bgColor;

    public ShopItem(String itemName, int price, int imageId, int bgColor, boolean isAnimal) {
        this.itemName = itemName;
        this.price = price;
        this.imageId = imageId;
        this.bgColor = bgColor;
        this.isAnimal = isAnimal;
    }

    public boolean isAnimal() {
        return isAnimal;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public int getImageId() {
        return imageId;
    }

    public int getBgColor() {
        return bgColor;
    }
}
