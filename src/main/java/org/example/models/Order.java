package org.example.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "MyOrder")
public class Order {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int orderId;

    @Column(name = "name")
    String name;

    @ManyToOne
    @JoinColumn(name = "supplierId", referencedColumnName = "id")
    Supplier supplier;

    public Order() { }

    public Order(String name, Supplier supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", name=" + name +
                ", supplier=" + supplier.getId() +
                '}';
    }
}
