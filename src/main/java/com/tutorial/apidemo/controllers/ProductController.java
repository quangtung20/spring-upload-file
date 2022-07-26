package com.tutorial.apidemo.controllers;

import com.tutorial.apidemo.dto.CreateProductDto;
import com.tutorial.apidemo.dto.ResponseDto;
import com.tutorial.apidemo.models.Product;
import com.tutorial.apidemo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @GetMapping("")
    List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseDto> findById(@PathVariable Long id){
        Optional<Product> foundProducts = productRepository.findById(id);
        return foundProducts.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseDto("ok","Query successfully",foundProducts)
                ):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseDto("failed","Product not found by id = "+id,foundProducts)
        );
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseDto> insertProduct(@RequestBody CreateProductDto createProductDto){
        List<Product> foundProducts = productRepository.findByProductName(createProductDto.getProductName().trim());
        if(foundProducts.size()>0){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseDto("failed","The product name has already exists","")
            );
        }
        Product product = Product.builder()
                .productName(createProductDto.getProductName())
                .price(createProductDto.getPrice())
                .year(createProductDto.getYear())
                .url(createProductDto.getUrl())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseDto("created","insert product successfully", productRepository.save(product))
        );
    }

    @PutMapping ("/upsert/{id}")
    ResponseEntity<ResponseDto> upsertProduct(
            @RequestBody CreateProductDto createProductDto,
            @PathVariable Long id
    ){
        AtomicReference<Boolean> insert = new AtomicReference<>(false);
        Product updateProduct = productRepository.findById(id).orElseGet(()->{
            Product newProduct = Product.builder()
                    .productName(createProductDto.getProductName())
                    .url(createProductDto.getUrl())
                    .price(createProductDto.getPrice())
                    .year(createProductDto.getYear())
                    .build();
            insert.set(true);
            return productRepository.save(newProduct);
        });

        if(insert.get()==true){
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseDto("success","upsert product successfully",updateProduct)
            );
        }

        updateProduct.setProductName(createProductDto.getProductName());
        updateProduct.setPrice(createProductDto.getPrice());
        updateProduct.setUrl(createProductDto.getUrl());
        updateProduct.setYear(createProductDto.getYear());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseDto("success","update product successfully",productRepository.save(updateProduct))
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseDto> deleteProduct(@PathVariable Long id){
        Boolean exists = productRepository.existsById(id);
        if(!exists){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseDto("failed","delete product failed","delete product id = "+id)
            );
        }
        productRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseDto("success","delete product successfully","done")
        );
    }
}
