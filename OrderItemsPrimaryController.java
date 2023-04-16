package Task.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Task.En.OrderItemsPrimary;
import Task.Repo.CustomerRepo;
import Task.Repo.OrderItemsPrimaryRepo;
import Task.Repo.OrderRepo;
import io.swagger.v3.oas.annotations.Operation;

@RestController
//@CrossOrigin(origins = "http://localhost:8888")

public class OrderItemsPrimaryController {

	@Autowired
	OrderItemsPrimaryRepo orderitemrepo;

	@Autowired
	OrderRepo orderrepo;
	@Autowired
	CustomerRepo custrepo;

//	6.....
	@GetMapping("/orderitem/{orderId}")
	@Operation(summary = "Get productname by using orderid from orderitems")
	public List<Object> getOrderByProductNameQuantiy(@PathVariable("orderId") int orderId) {
		List<Object> oi = orderitemrepo.getproductorderitemsByorderid(orderId);
		if (oi.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderId  Not Found");
		} else {
			return oi;
		}

	}

//	8...
	@GetMapping("/orderitems/{prodid}")
	@Operation(summary = "Get productname by using productid from orderitems")
	public List<Object> getallorderitems(@PathVariable("prodid") int prodid) {
		List<Object> o = orderitemrepo.getcustomerorderproductorderitemsByprodid(prodid);
		if (o.isEmpty()) {

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PtoductId  Not Found");
		} else {
			return o;
		}

	}

	@GetMapping("/orderitemsall")
	public List<OrderItemsPrimary> getitems() {
		var all = orderitemrepo.findAll();
		return all;
	}
//	

}