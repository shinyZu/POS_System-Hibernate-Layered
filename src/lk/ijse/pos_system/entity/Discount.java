package lk.ijse.pos_system.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Discount {
    @Id
    private String itemCode;
    private String description;
    @Column(columnDefinition = "double default '0.0'")
    private String discount;

    public Discount() {}

    public Discount(String itemCode, String description, String discount) {
        this.setItemCode(itemCode);
        this.setDescription(description);
        this.setDiscount(discount);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}
