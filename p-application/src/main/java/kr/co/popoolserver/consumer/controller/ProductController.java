package kr.co.popoolserver.consumer.controller;

import io.swagger.annotations.ApiOperation;
import kr.co.popoolserver.consumer.service.ProductService;
import kr.co.popoolserver.dtos.response.ResponseProduct;
import kr.co.popoolserver.error.model.ResponseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @ApiOperation("모든 상품 조회 / 권한 : 일반 회원 이상")
    @GetMapping
    public ResponseFormat<List<ResponseProduct.READ_PRODUCT>> getAllProducts(){
        return ResponseFormat.ok(productService.getProducts());
    }

    @ApiOperation("상품 세부사항 조회 / 권한 : 일반 회원 이상")
    @GetMapping("/detail")
    public ResponseFormat<ResponseProduct.READ_PRODUCT_DETAIL> getProduct(@RequestParam("product_name") String productName){
        return ResponseFormat.ok(productService.getProduct(productName));
    }
}
