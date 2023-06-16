package br.com.guzzmega.springboot3.controllers;

import br.com.guzzmega.springboot3.dtos.ProductRecordDto;
import br.com.guzzmega.springboot3.models.ProductModel;
import br.com.guzzmega.springboot3.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProductController {

	@Autowired
	ProductRepository productRepository;

	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDto, productModel);

		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
		Optional<ProductModel> productOptional = productRepository.findById(id);
		if(productOptional.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Can't find product with ID %s", id));
		}

		return ResponseEntity.status(HttpStatus.OK).body(productOptional.get());
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> putProduct(@RequestBody @Valid ProductRecordDto productRecordDto, @PathVariable(value="id" ) UUID id){
		Optional<ProductModel> productOptional = productRepository.findById(id);
		if(productOptional.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Can't find product with ID %s", id));
		}
		var productModel = productOptional.get();
		BeanUtils.copyProperties(productRecordDto, productModel);

		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteModel(@PathVariable(value="id") UUID id){
		Optional<ProductModel> productOptional = productRepository.findById(id);
		if(productOptional.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Can't find product with ID %s", id));
		}

		productRepository.delete(productOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully!");
	}
}