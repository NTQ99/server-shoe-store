package shoe.store.server.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shoe.store.server.security.jwt.*;
import shoe.store.server.security.services.*;
import shoe.store.server.services.CustomerService;
import shoe.store.server.services.UserService;
import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Customer;
import shoe.store.server.models.Role;
import shoe.store.server.models.User;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.payload.request.*;
import shoe.store.server.payload.response.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<BasePageResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());

			BasePageResponse<JwtResponse> response = new BasePageResponse<>(
					new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles), ErrorMessage.StatusCode.AUTH_SUCCESS.message);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			if (e.getMessage().equals("Bad credentials")) {
				throw new GlobalException(ErrorMessage.StatusCode.USER_WRONG_PASSWORD.message);
			} else {
				throw new GlobalException(e.getMessage());
			}
		}
	}

	@PostMapping("/updatepass")
	public ResponseEntity<BasePageResponse<?>> updatePassword(@Valid @RequestBody LoginRequest updateRequest) {
		User user = userService.getUserByUsername(updateRequest.getUsername());
		if (user == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}
		this.authenticateUser(updateRequest);
		user.setPassword(encoder.encode(updateRequest.getNewPassword()));
		userService.createUser(user);
		return ResponseEntity.ok(new BasePageResponse<>(null, ErrorMessage.StatusCode.USER_MODIFIED.message));
	}

	@PostMapping("/resetpass")
	public ResponseEntity<BasePageResponse<?>> resetPassword(@Valid @RequestBody RegisterRequest resetRequest) {
		User user = userService.getUserByEmail(resetRequest.getEmail());
		if (user == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}
		user.setPassword(encoder.encode(resetRequest.getPassword()));
		userService.createUser(user);
		return ResponseEntity.ok(new BasePageResponse<>(null, ErrorMessage.StatusCode.USER_MODIFIED.message));
	}

	@PostMapping("/validate")
	public ResponseEntity<BasePageResponse<?>> validateEmail(@Valid @RequestBody RegisterRequest resetRequest) {
		User user = userService.getUserByUsername(resetRequest.getUsername());
		if (user == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}
		if (!user.getEmail().equals(resetRequest.getEmail())) {
			throw new GlobalException(ErrorMessage.StatusCode.EMAIL_INVALID.message);
		}
		return ResponseEntity.ok(new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message));
	}

	@PostMapping("/register")
	public ResponseEntity<BasePageResponse<?>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		if (userService.checkUserExists(registerRequest.getUsername())) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_EXIST.message);
		}

		if (userService.checkEmailExists(registerRequest.getEmail())) {
			throw new GlobalException(ErrorMessage.StatusCode.EMAIL_EXIST.message);
		}

		String phone = registerRequest.getPhone();
		if (registerRequest.getPhone().startsWith("+84")) phone = registerRequest.getPhone().substring(3);
		if (phone != null && phone != "" && !phone.startsWith("0")) phone = "0" + phone;
		if (userService.checkPhoneExists(phone)) {
			throw new GlobalException(ErrorMessage.StatusCode.PHONE_EXIST.message);
		}

		// Create new user's account
		User user = new User(registerRequest.getUsername(), encoder.encode(registerRequest.getPassword()));

		Set<Role> roles = new HashSet<>();

		String registerRole = registerRequest.getRoleKey();

		Role userRole;
		if (registerRole == null || registerRole == "") {
			userRole = userService.getRoleByName(Role.ERole.ROLE_BUYER);
			if (userRole == null) throw new GlobalException("Quyền không tồn tại!");
		} else {
			switch (registerRole) {
				case "admin":
					userRole = userService.getRoleByName(Role.ERole.ROLE_ADMIN);
					if (userRole == null) throw new GlobalException("Quyền không tồn tại!");
					break;
				case "seller":
					userRole = userService.getRoleByName(Role.ERole.ROLE_SELLER);
					if (userRole == null) throw new GlobalException("Quyền không tồn tại!");
					break;
				default:
					throw new GlobalException("Role Key không hợp lệ!");
			}
		}
		roles.add(userRole);

		user.setRoles(roles);
		user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(phone);
		user.setEmailVerified(false);
		
		if (registerRole == null || registerRole == "") {
			Customer customer = customerService.getCustomerByPhone(phone);
			if (customer == null) {
				customer = new Customer(user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail());
				customer.setCustomerBirth(registerRequest.getBirthday());
			} else {
				customer.setCustomerFirstName(user.getFirstName());
				customer.setCustomerLastName(user.getLastName());
				customer.setCustomerEmail(user.getEmail());
				customer.setCustomerBirth(registerRequest.getBirthday());
			}
			user.setCustomerCode(customer.getCustomerCode());
			
			customerService.createCustomer(customer);
		}
		userService.createUser(user);

		BasePageResponse<?> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.USER_CREATED.message);

		return ResponseEntity.ok(response);
	}
}
