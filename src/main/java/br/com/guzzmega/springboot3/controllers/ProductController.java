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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
		List<ProductModel> productList = productRepository.findAll();

		if(!productList.isEmpty()){
			for(ProductModel product : productList){
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(product.getIdProduct())).withSelfRel());
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(productList);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
		Optional<ProductModel> productOptional = productRepository.findById(id);

		if(productOptional.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Can't find product with ID %s", id));
		}

		productOptional.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("productList"));
		return ResponseEntity.status(HttpStatus.OK).body(productOptional.get());
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> putProduct(@RequestBody @Valid ProductRecordDto productRecordDto, @PathVariable(value="id" ) UUID id){
		Optional<ProductModel> productOptional = productRepository.findById(id);

		if(productOptional.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Can't find product with ID %s", id));
		}

		BeanUtils.copyProperties(productRecordDto, productOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productOptional.get()));
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