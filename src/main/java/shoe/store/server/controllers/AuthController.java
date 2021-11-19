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

	@PostMapping("/register")
	public ResponseEntity<BasePageResponse<?>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		if (userService.checkUserExists(registerRequest.getUsername())) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_EXIST.message);
		}
		if (userService.checkPhoneExists(registerRequest.getPhone())) {
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
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());

		Customer customer = new Customer(user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail());

		userService.createUser(user);
		customerService.createCustomer(customer);

		BasePageResponse<?> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.USER_CREATED.message);

		return ResponseEntity.ok(response);
	}
}
