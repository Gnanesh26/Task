package Task.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Task.En.Customer;
import Task.En.Order;
import Task.En.OrderItems;
import Task.En.OrderItemsPrimary;
import Task.En.Product;
import Task.Repo.CustomerRepo;
import Task.Repo.OrderItemsPrimaryRepo;
import Task.Repo.OrderRepo;
import Task.Repo.ProductRepo;
import io.swagger.v3.oas.annotations.Operation;

@RestController
//@CrossOrigin(origins = "http://localhost:8888")
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderRepo orderrepo;

	@Autowired
	private CustomerRepo cusRepo;

	@Autowired
	private ProductRepo productrepo;

	@Autowired
	private OrderItemsPrimaryRepo orderprimaryrepo;

	@GetMapping("/orderdate/{code}")
	public List<Order> getOrderByDate(@PathVariable(name = "code") LocalDate code) {
		var list = orderrepo.findByOrderDate(code);
		return list;
	}

//	5...
	@GetMapping("/orderstatus/{code}")
	@Operation(summary = "Get orders with given status")
	public List<Order> getOrderByStatus(@PathVariable(name = "code") String code) {
		var list = orderrepo.findByOrderStatus(code);
		return list;

	}

//	10....
	@GetMapping("/orderid/{code}")
	public List<Order> getAllOrders(@PathVariable(name = "code") int code) {
		List<Order> lis = orderrepo.findByorderId(code);
		return lis;
	}

//	3................
	@GetMapping("/ordersforcustomer/{code}")
	@Operation(summary = "Get Orders by using customerid")
	public List<Order> getAll(@PathVariable int code) {
		var list = cusRepo.findById(code);
		if (list.isPresent()) {
			var o = list.get();
			return orderrepo.findByCustomers(o);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Code Not Found");
		}

	}

//9....
	@PostMapping("/addorders/{cid}")
	@Operation(summary = "Adding orders by given customerid	")
	public void addorder(@RequestParam(value = "cid") int cid,
			@RequestParam(value = "prodlst") ArrayList<String> prodlst) {
		Order order = new Order();
		var list = cusRepo.findById(cid);
		if (list.isPresent()) {
			Customer v = list.get();
			order.setCustomerId(cid);
			order.setCustomers(v);
			order.setOrderDate(LocalDate.now());
			order.setDeliveryDate(LocalDate.now().plusDays(7));
			order.setOrderStatus("n");

			order = orderrepo.save(order);
			for (String p : prodlst) {
				String[] pl = p.split("&", 0);
				int prodid = Integer.parseInt(pl[0]);
				int qty = Integer.parseInt(pl[1]);
				int ordid = order.getOrderId();
				Optional<Product> prodlist = productrepo.findById(prodid);
				if (prodlist.isPresent()) {
					OrderItemsPrimary orderprimary = new OrderItemsPrimary();
					OrderItems orderitem = new OrderItems();
					orderitem.setOrderid(ordid);
					orderitem.setProdid(prodid);
					orderprimary.setOrdertoorderitems(order);
					orderprimary.setProductorderitem(prodlist.get());
					orderprimary.setOrderitems(orderitem);
					orderprimary.setQuantity(qty);
					orderprimary.setPrice(prodlist.get().getProductPrice() * qty * 0.15);
					orderprimaryrepo.save(orderprimary);
				} else {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CustomerId  Not Found");
				}
			}

		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CustomerId  Not Found");
		}

	}

//	4.
	@GetMapping("/orderdateqy/{date}")
	@Operation(summary = "Get orders after a given date")
	public List<Order> getByQueryDate(@PathVariable LocalDate date) {
		var dat = orderrepo.getByOrderDate(date);
		return dat;
	}

	@PutMapping("/updateorder/{id}")
	public Order updateStatus(@PathVariable int id, @RequestParam("status") String status) {
		var orderid = orderrepo.findById(id);
		if (orderid.isPresent()) {
			var order = orderid.get();
			order.setOrderStatus(status);
			orderrepo.save(order);
			return order;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderId  Not Found");
		}
	}

	@GetMapping("/allorders")
	public List<Order> getOrder() {
		var allorder = orderrepo.findAll();
		return allorder;
	}

	@DeleteMapping("/deleteorder/{id}")
	public void deleteOrder(@RequestParam int id) {
		var del = orderrepo.findById(id);
		if (del.isPresent()) {
			orderrepo.deleteById(id);

		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderId  Not Found");
		}
	}

// Adding Weight------------------------------------(optional)
	@PostMapping("/{custid}/{weight}")
	@Operation(summary = "addingweight")
	public ResponseEntity addWeight(@PathVariable int custid, @PathVariable double weight) {
		Order order = new Order();
		var cust = cusRepo.findById(custid);

		double wegprice = 0;
		if (cust.isPresent()) {
			if (weight == 0 || weight > 100) {

				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Entered weight is not supported by the System");
			} else if (weight < 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entered weight is Invalid");
			} else if (weight < 1) {
				wegprice = 1;
			} else if (weight >= 1 & weight < 5) {
				wegprice = 1.5;
			} else if (weight >= 5 & weight < 10) {
				wegprice = 2.8;
			} else if (weight >= 10 & weight < 15) {
				wegprice = 3.5;
			} else {
				wegprice = 5;
			}
			Customer v = cust.get();
			order.setOrderweight(weight);
			order.setWeightprice(wegprice);
			order.setCustomerId(custid);
			order.setCustomers(v);
			order.setOrderDate(LocalDate.now());
			order.setDeliveryDate(LocalDate.now().plusDays(7));
			order.setOrderStatus("n");
			order.setAmount(order.getOrderweight() * order.getWeightprice());
			order = orderrepo.save(order);
			return ResponseEntity.ok("Succes");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Orderid not found");
		}
	}

//Adding weight to orders ---------------------------(main)
//	@PostMapping("/addweight/{cid}/{weight}")
//	@Operation(summary = "Adding weight with product also...")
//	public ResponseEntity<String> addorder2(@RequestParam(value = "cid") int cid, @PathVariable double weight,
//			@RequestParam(value = "prodlst") ArrayList<String> prodlst) {
//		Order order = new Order();
//		var list = cusRepo.findById(cid);
//		double wegprice = 0;
//		if (list.isPresent()) {
//			if (weight == 0 || weight == 100) {
//
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//						.body("Entered weight is not supported by the System");
//
//			} else if (weight < 0) {
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entered weight is Invalid");
//			} else if (weight < 1) {
//				wegprice = 1;
//			} else if (weight >= 1 & weight < 5) {
//				wegprice = 1.5;
//			} else if (weight >= 5 & weight < 10) {
//				wegprice = 2.8;
//			} else if (weight >= 10 & weight < 15) {
//				wegprice = 3.5;
//			} else {
//				wegprice = 5;
//			}
//			Customer v = list.get();
//			order.setCustomerId(cid);
//			order.setCustomers(v);
//			order.setOrderDate(LocalDate.now());
//			order.setDeliveryDate(LocalDate.now().plusDays(7));
//			order.setOrderStatus("n");
//			order.setOrderweight(weight);
//			order.setWeightprice(wegprice);
//			order.setAmount(order.getOrderweight() * order.getWeightprice());
//
//			order = orderrepo.save(order);
//			for (String p : prodlst) {
//				String[] pl = p.split("&", 0);
//				int prodid = Integer.parseInt(pl[0]);
//				int qty = Integer.parseInt(pl[1]);
//				int ordid = order.getOrderId();
//				Optional<Product> prodlist = productrepo.findById(prodid);
//				if (prodlist.isPresent()) {
//					OrderItemsPrimary orderprimary = new OrderItemsPrimary();
//					OrderItems orderitem = new OrderItems();
//					orderitem.setOrderid(ordid);
//					orderitem.setProdid(prodid);
//					orderprimary.setOrdertoorderitems(order);
//					orderprimary.setProductorderitem(prodlist.get());
//					orderprimary.setOrderitems(orderitem);
//					orderprimary.setQuantity(qty);
//					orderprimary.setPrice(prodlist.get().getProductPrice() * qty * 0.15);
//					orderprimaryrepo.save(orderprimary);
//					return ResponseEntity.ok("ok");
//				} else {
//					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product  Not Found");
//				}
//			}
//
//		} else {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CustomerId  Not Found");
//		}
//		return ResponseEntity.ok("ok");
//
//	}

}