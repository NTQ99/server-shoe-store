package shoe.store.server.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.User;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.BaseRequest;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/get")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BasePageResponse<List<User>>> getAll(@RequestBody(required = false) BaseRequest request) {
		List<User> allUsers = new ArrayList<>();
		if (request != null) {
			Pageable paging = PageRequest.of(request.getPage() - 1, request.getPerpage());
			Page<User> pageUsers = service.getAllUsers(paging);
			allUsers = pageUsers.getContent();
			allUsers.remove(0);

			return new ResponseEntity<>(new BasePageResponse<>(allUsers, pageUsers.getNumber(),
					pageUsers.getTotalPages(), pageUsers.getSize(), pageUsers.getTotalElements()), HttpStatus.OK);
		} else {
			allUsers = service.getAllUsers();
			allUsers.remove(0);
			return new ResponseEntity<>(new BasePageResponse<>(allUsers, ErrorMessage.StatusCode.OK.message),
					HttpStatus.OK);
		}

	}

	@PostMapping("/get/info")
	public ResponseEntity<BasePageResponse<User>> getUseInfor(@RequestHeader("Authorization") String jwt) {

		String id = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
		User userData = service.getUserById(id);
		if (userData == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}

		userData.setPassword(null);

		return new ResponseEntity<>(
				new BasePageResponse<>(userData, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

	}

	@PostMapping("/update/info")
	public ResponseEntity<BasePageResponse<User>> updateUseInfor(@RequestHeader("Authorization") String jwt, @RequestBody User newUserData) {

		String id = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
		User currUserData = service.getUserById(id);
		if (currUserData == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}

		User newUserModified = service.updateUser(id, newUserData);
		return new ResponseEntity<>(
				new BasePageResponse<>(newUserModified, ErrorMessage.StatusCode.USER_MODIFIED.message), HttpStatus.OK);

	}

	@PostMapping("/update/password")
	public ResponseEntity<BasePageResponse<User>> updateUserPassword(@RequestHeader("Authorization") String jwt, @RequestBody User newUserData) {

		String id = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
		User currUserData = service.getUserById(id);
		if (currUserData == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}

		User newUserModified = service.updatePassword(id, encoder.encode(newUserData.getPassword()));
		return new ResponseEntity<>(
				new BasePageResponse<>(newUserModified, ErrorMessage.StatusCode.USER_MODIFIED.message), HttpStatus.OK);

	}

	@PostMapping("/update/status/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BasePageResponse<User>> updateUserStatus(@PathVariable("id") String id, @RequestBody User newUserData) {

		User currUserData = service.getUserById(id);
		if (currUserData == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}

		User newUserModified = service.updateUserStatus(id, newUserData.getStatus().name());
		return new ResponseEntity<>(
				new BasePageResponse<>(newUserModified, ErrorMessage.StatusCode.USER_MODIFIED.message), HttpStatus.OK);

	}

	@PostMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BasePageResponse<User>> deleteUser(@PathVariable("id") String id) {

		service.deleteUser(id);
		return new ResponseEntity<>(
				new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

	}

	@PostMapping("/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BasePageResponse<User>> deleteAllUsers() {

		service.deleteAllUsers();
		return new ResponseEntity<>(
				new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

	}

}
