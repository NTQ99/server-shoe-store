package shoe.store.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Role;
import shoe.store.server.models.User;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.payload.request.RegisterRequest;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class PageViewController {

    @Autowired
    private UserService service;

    @Autowired
	private JwtUtils jwtUtils;

    @Autowired
    private AuthController authController;

    @Value("${max.result.per.page}")
    private int maxResults;

    @Value("${max.card.display.on.pagination.tray}")
    private int maxPaginationTraySize;

    @GetMapping("/home")
    public ModelAndView home(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "4", required = false) Integer size,
            HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    String jwt = cookie.getValue();
                    String id = jwtUtils.getIdFromJwtToken(jwt.substring(9, jwt.length()));
                    User currUserData = service.getUserById(id);
                    Role adminRole = service.getRoleByName(Role.ERole.ROLE_ADMIN);
                    if (currUserData.getRoles().contains(adminRole)) {
                        ModelAndView modelAndView = new ModelAndView();
                        modelAndView.setViewName("home");
                        Page<User> allUsers = service.getAllUsers(PageRequest.of(page, size, Sort.by("username")));
                        modelAndView.addObject("allUsers", allUsers);
                        modelAndView.addObject("maxTraySize", size);
                        modelAndView.addObject("currentPage", page);
                        return modelAndView;
                    } else {
                        return new ModelAndView("redirect:/403");
                    }

                }
            }
        }

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/searchBox")
    public ModelAndView searchByTerm(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "4", required = false) Integer size,
            @RequestParam(value = "searchTerm", required = false) String searchTerm) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        Page<User> allUsers = service.findUserByUsername(searchTerm.trim(),
                PageRequest.of(page, size, Sort.by("username")));
        modelAndView.addObject("allUsers", allUsers);
        modelAndView.addObject("maxTraySize", size);
        modelAndView.addObject("currentPage", page);
        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView search() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search");
        return modelAndView;
    }

    // @PostMapping("/searchSubmit")
    // public ModelAndView searchSubmit(@ModelAttribute SearchDTO searchDto) {
    // List<User> result = service.searchBy(searchDto.getSearchKeyword(),
    // searchDto.getCriteria());
    // ModelAndView modelAndView = new ModelAndView();
    // modelAndView.setViewName("search");
    // modelAndView.addObject("result", result);
    // return modelAndView;
    // }

    @GetMapping("/addNewUser")
    public ModelAndView addNewUser() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create-user");
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<BasePageResponse<User>> update(@RequestBody User newUserData) {
        User currUserData = service.getUserById(newUserData.getId());
        if (currUserData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
        }

        newUserData.setEmail(currUserData.getEmail());
        User newUserModified = service.updateUser(newUserData.getId(), newUserData);
        return new ResponseEntity<>(
                new BasePageResponse<>(newUserModified, ErrorMessage.StatusCode.USER_MODIFIED.message), HttpStatus.OK);
    }

    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    return new ModelAndView("redirect:/home");
                }
            }
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request) {
        String result = "redirect:/home";
        if (service.checkUserExists(request.getUsername())) {
			return "redirect:/addNewUser?error=User Already Exists!";
		}

        String phone = request.getPhone();
		if (request.getPhone().startsWith("+84")) phone = request.getPhone().substring(3);
		if (phone != null && phone != "" && !phone.startsWith("0")) phone = "0" + phone;
		if (service.checkPhoneExists(phone)) {
			return "redirect:/addNewUser?error=Phone Already Exists!";
		}

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            return "redirect:/addNewUser?error=Enter valid fist name";
        } else if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            return "redirect:/addNewUser?error=Enter valid last name";
        } else if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return "redirect:/addNewUser?error=Enter valid email";
        } else if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return "redirect:/addNewUser?error=Enter valid password";
        }


        String registerRole = request.getRoleKey();
        if (!registerRole.equals(null) && !registerRole.equals("") && !registerRole.equals("admin") && !registerRole.equals("seller")){
            return "redirect:/addNewUser?error=Select a valid Role";
        }
        
        authController.registerUser(request);
        return result;
    }

    @GetMapping("/delete/{userId}")
    public String delete(@PathVariable String userId) {
        service.deleteUser(userId);
        return "redirect:/home";
    }

    @ResponseBody
    @GetMapping("/removeAll")
    public Boolean removeAll() {
        try {
            service.deleteAllUsers();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/403")
    public ModelAndView accessDenied() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("403");
        return modelAndView;
    }

    @GetMapping("/error")
    public ModelAndView error() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        return modelAndView;
    }

}
