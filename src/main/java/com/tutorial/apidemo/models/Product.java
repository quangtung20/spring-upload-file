package com.tutorial.apidemo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Objects;

//pojo object la 1 object co day du constructor, getter, setter, toString
@Entity
@Builder
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 200)
    private String productName;
    private int year;
    private Double price;
    private String url;

    @Transient // truong duoc khai bao nhung ko xuat hien trong db
    private int age;

    public int getAge() {
        return Calendar.getInstance().get(Calendar.YEAR)-year;
    }

    public Product() {
    }

    public Product(Long id, String productName, int year, Double price, String url) {
        this.id = id;
        this.productName = productName;
        this.year = year;
        this.price = price;
        this.url = url;
    }

    public Product(String productName, int year, Double price, String url) {
        this.productName = productName;
        this.year = year;
        this.price = price;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getYear() {
        return year;
    }

    public Double getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return year == product.year && age == product.age && Objects.equals(id, product.id) && Objects.equals(productName, product.productName) && Objects.equals(price, product.price) && Objects.equals(url, product.url);
    }
}
