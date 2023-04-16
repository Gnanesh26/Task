package Task.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Task.En.Product;
import Task.Repo.ProductRepo;
import io.swagger.v3.oas.annotations.Operation;

@RestController
//@CrossOrigin(origins = "http://localhost:8888")
public class ProductController {

	@Autowired
	private ProductRepo productrepo;

//	7...
	// List of Products where name matches given String------------------
	@GetMapping("/products/{name}")
	@Operation(summary = "Get product where given name matches")
	public List<Product> getName(@PathVariable String name) {
		var p = productrepo.findByproductName(name);
		return p;
	}

//	2...
	@GetMapping("/productspage/{size}")
	@Operation(summary = "Get products by using Pagination")
	public List<Product> getAllProduct(@PathVariable int size) {
		return productrepo.findAll(PageRequest.of(0, size)).getContent();
	}

	// Adding Products......................
	@PostMapping("/addproduct/productid/productname/productdes/productprice")
	public Product addingProduct(@RequestParam int productid, @RequestParam String productname,
			@RequestParam String productdes, @RequestParam double productprice) {
		Product prod = new Product();
		prod.setProductName(productname);
		prod.setProductDescription(productdes);
		prod.setProductPrice(productprice);
		prod = productrepo.save(prod);
		return prod;
	}

	// Get Details of products................................................
	@GetMapping("/products")
	public List<Product> getAll() {
		var prod = productrepo.findAll();
		return prod;
	}

	// Updating Products........................................
	@PutMapping("/updateproduct/productid/productname")
	public Product updateProduct(@RequestParam int productid, @RequestParam String productname) {
		Product prod = new Product();
		var productlis = productrepo.findById(productid);
		if (productlis.isPresent()) {
			var pro = productlis.get();
			pro.setProductName(productname);

			pro = productrepo.save(pro);
			return pro;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ProductId  Not Found");
		}
	}

	@DeleteMapping("/deleteprodut/id")
	public void deleteProduct(@RequestParam int id) {
		var del = productrepo.findById(id);
		if (del.isPresent()) {
			productrepo.deleteById(id);

		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ProductId  Not Found");
		}
	}

}
//}