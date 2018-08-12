import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Item {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty itemNumber;
    private SimpleStringProperty itemName;
    private SimpleStringProperty itemDesc;
    private SimpleIntegerProperty itemPrice;
    private SimpleStringProperty itemBuy;
    private SimpleStringProperty itemCrash;
    private SimpleStringProperty itemPlace;
    private SimpleStringProperty itemCompany;
    public static int priceCounter = 0;

    public Item(Integer id, Integer itemNumber, String itemName, String itemDesc, Integer itemPrice, String itemBuy, String itemCrash, String itemPlace, String itemCompany) {
        this.id = new SimpleIntegerProperty(id);
        this.itemNumber = new SimpleIntegerProperty(itemNumber);
        this.itemName = new SimpleStringProperty(itemName);
        this.itemDesc = new SimpleStringProperty(itemDesc);
        this.itemPrice = new SimpleIntegerProperty(itemPrice);
        this.itemBuy = new SimpleStringProperty(itemBuy);
        this.itemCrash = new SimpleStringProperty(itemCrash);
        this.itemPlace = new SimpleStringProperty(itemPlace);
        this.itemCompany = new SimpleStringProperty(itemCompany);
        priceCounter += itemPrice;
    }

    public Integer getId() {
        return id.get();
    }

    public Integer getItemNumber() {
        return itemNumber.get();
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber.set(itemNumber);
    }

    public SimpleIntegerProperty itemNumberProperty() {
        return itemNumber;
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String name) {
        this.itemName.set(name);
    }

    public SimpleStringProperty itemNameProperty() {
        return itemName;
    }


    public String getItemDesc() {
        return itemDesc.get();
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc.set(itemDesc);
    }

    public SimpleStringProperty itemDescProperty() {
        return itemDesc;
    }

    public Integer getItemPrice() {
        return itemPrice.get();
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice.set(itemPrice);
    }

    public SimpleIntegerProperty itemPriceProperty() {
        return itemPrice;
    }

    public String getItemBuy() {
        return itemBuy.get();
    }

    public void setItemBuy(String itemBuy) {
        this.itemBuy.set(itemBuy);
    }

    public SimpleStringProperty itemBuyProperty() {
        return itemBuy;
    }

    public String getItemCrash() {
        return itemCrash.get();
    }

    public void setItemCrash(String itemCrash) {
        this.itemCrash.set(itemCrash);
    }

    public SimpleStringProperty itemCrashProperty() {
        return itemCrash;
    }

    public String getItemPlace() {
        return itemPlace.get();
    }

    public void setItemPlace(String itemPlace) {
        this.itemPlace.set(itemPlace);
    }

    public SimpleStringProperty itemPlaceProperty() {
        return itemPlace;
    }

    public String getItemCompany() {
        return itemCompany.get();
    }

    public void setItemCompany(String itemCompany) {
        this.itemCompany.set(itemCompany);
    }

    public SimpleStringProperty itemCompanyProperty() {
        return itemCompany;
    }
}
