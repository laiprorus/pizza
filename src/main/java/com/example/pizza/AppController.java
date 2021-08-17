package com.example.pizza;

import java.util.ArrayList;
import java.util.List;

import com.example.pizza.constraints.Constraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;

import com.example.pizza.orders.Order;
import com.example.pizza.orders.OrderClient;
import com.example.pizza.orders.exceptions.*;
import com.example.pizza.users.User;
import com.example.pizza.users.UserRepository;
import com.example.pizza.users.exceptions.*;

/*
    Controller that provides extension to Pizzeria API https://order-pizza-api.herokuapp.com/api/ui/
    Stores user database and uses Pizzeria API to manipulate orders.
    No authentication, users can be created and updated.
    Orders are always created and deleted only for given user.
    Provides constraints for allowed order values. Order creation requests with invalid values will be rejected.
    Changes to user database, orders and exceptions are logged.
    Successful and invalid requests are not logged.
    Exceptions caused by Pizzeria API are forwarded as 424 Failed Dependency.
    All other exceptions are forwarded as 500 internal Server Error.
    Notes & Issues:
     - Cannot delete users.
     - Cannot update order after creation.
     - Order can still be rejected if table number is taken during execution and there is no retry behaviour implemented.
 */
@RestController
public class AppController {

    private final Logger log = LoggerFactory.getLogger(AppController.class);

    private final UserRepository userRepository;
    private final OrderClient orderClient;

    public AppController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.orderClient = new OrderClient();
    }

    // Authenticate Pizzeria Order Client on initialization.
    @PostConstruct
    public void init() {
        try {
            orderClient.auth();
        } catch (AuthenticationException e) {
            log.error("INIT: Authentication failed.");
        } catch (Exception e) {
            log.error("INIT: " + e.toString());
            throw e;
        }
    }

    private boolean isUserNameUnique(String name) {
        User usernameExample = new User(name);
        Example<User> example = Example.of(usernameExample);
        return userRepository.exists(example);
    }

    private boolean isUserNameNotEmpty(String name) {
        if (name == null || name.length() == 0) return true;
        return false;
    }

    // CONSTRAINTS

    @GetMapping("/constraints")
    public Constraints getConstraints() {
        String logPrefix = "Serve GET /constraints";
        try {
            return new Constraints();
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    // USERS

    @GetMapping("/users")
    public List<User> getAllUsers() {
        String logPrefix = "Serve GET /users";
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        String logPrefix = "Serve GET /users/id";
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    @PostMapping("/users")
    ResponseEntity<User> createUser(@RequestBody User user) {
        String logPrefix = "Serve POST /users";
        try {
            // Validate that user name is unique and not empty.
            if (isUserNameNotEmpty(user.getName())) throw new UserNameEmptyException();
            if (isUserNameUnique(user.getName())) throw new UserNameAlreadyExistException(user.getName());
            User savedUser = userRepository.save(user);
            log.info(logPrefix + ": Saved user, " + savedUser.toString());

            return new ResponseEntity(savedUser, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    @PutMapping("/users/{id}")
    ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long id) {
        String logPrefix = "Serve PUT /users/id";
        try {
            User foundUser = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));

            // Do nothing if user name did not change.
            if (user.getName().equals(foundUser.getName())) new ResponseEntity(foundUser, HttpStatus.CREATED);

            // Validate that user name is unique and not empty.
            if (isUserNameNotEmpty(user.getName())) throw new UserNameEmptyException();
            if (isUserNameUnique(user.getName())) throw new UserNameAlreadyExistException(user.getName());

            // Update user.
            foundUser.setName(user.getName());
            User updatedUser = userRepository.save(foundUser);
            log.info(logPrefix + ": Updated user, " + updatedUser.toString());

            return new ResponseEntity(updatedUser, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    // ORDERS

    @GetMapping("/users/{id}/orders")
    public List<Order> getAllOrders(@PathVariable Long id) {
        String logPrefix = "Serve GET /users/id/orders";
        try {
            // Get user and all orders.
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            List<Order> orders = orderClient.getOrders();

            // Filter orders that user has ids stored for.
            List<Order> userOrders = new ArrayList<>();
            for (Order order : orders) {
                if (user.getOrderIds().contains(order.getOrderId())) {
                    userOrders.add(order);
                }
            }

            return userOrders;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    @PostMapping("/users/{id}/orders")
    Order createOrder(@RequestBody Order order, @PathVariable Long id) {
        String logPrefix = "Serve POST /users/id/orders";
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));

            // Validate order value constraints and set table number.
            orderClient.assertOrderConstraints(order);
            order.setTableNumber(orderClient.getNextTableNumber());

            // Post order.
            Order postedOrder = orderClient.postOrderWithAuth(order);

            // Add order id to user.
            user.getOrderIds().add(postedOrder.getOrderId());
            userRepository.save(user);
            log.info(logPrefix + ": Saved order for user, " + user.toString() + ", " + postedOrder.toString());

            return postedOrder;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

    @DeleteMapping("/users/{id}/orders/{orderId}")
    ResponseEntity<?> deleteOrder(@PathVariable Long id, @PathVariable Long orderId) {
        String logPrefix = "Serve DELETE /users/id/orders/id";
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));

            // Check that order id is stored for user. Do not delete otherwise.
            int orderIndex = user.getOrderIds().indexOf(orderId);
            if (orderIndex == -1) return ResponseEntity.noContent().build();

            // Delete order itself and delete order id from user.
            orderClient.deleteOrder(orderId);
            user.getOrderIds().remove(orderIndex);
            userRepository.save(user);
            log.info(logPrefix + ": Deleted order \'" + orderId + "\' for user, " + user.toString());

            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(logPrefix + ": " + e.toString());
            throw new InternalServerException();
        }
    }

}
