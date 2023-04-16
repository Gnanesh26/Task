package Task.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Task.En.Customer;
import Task.Repo.CustomerRepo;
import io.swagger.v3.oas.annotations.Operation;


@RestController
//@CrossOrigin(origins = "http://localhost:8888")
public class CustomerController {

	@Autowired
	private CustomerRepo customrepo;

//	1....
	// ----------- List of Customers-----------------------
	@GetMapping("/customers")
	@Operation(summary = "Get all Customers")
	public List<Customer> getAll() {
		return customrepo.findAll();
	}

	@PutMapping("/updateCustomer/customerid/customermobile/customeremail")
	@Operation(summary = "Updating customer")
	public Customer updateCustomer(@RequestParam int customerid, @RequestParam String customermobile,
			@RequestParam String customeremail) {
		var customerlis = customrepo.findById(customerid);
		Customer customer = new Customer();
		if (customerlis.isPresent()) {
			var custom = customerlis.get();
			custom.setCustomerEmail(customeremail);
			custom.setCustomerEmail(customeremail);
			custom = customrepo.save(custom);
			return custom;

		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CustomerId  Not Found");
		}
	}

	@PostMapping("/addcustomer/customerid/customername/customeremail/customermobile")
	@Operation(summary = "Adding customer")
	public Customer addCustomer(@RequestParam int customerid, @RequestParam String customername,
			@RequestParam String cutomeremail, @RequestParam String customermobile) {
		Customer customer = new Customer();
		customer.setCustomerId(customerid);
		customer.setCustomerName(customername);
		customer.setCustomerEmail(cutomeremail);
		customer.setCustomerMobile(customermobile);
		customer = customrepo.save(customer);
		return customer;
	}

	@DeleteMapping("/deletecustomer/{id}")
	public void deleteCustomer(@RequestParam int id) {
		var del = customrepo.findById(id);
		if (del.isPresent()) {
			customrepo.deleteById(id);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CustomerId  Not Found");
		}

	}
}