package shoe.store.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shoe.store.server.controllers.AuthController;
import shoe.store.server.models.Category;
import shoe.store.server.models.Color;
import shoe.store.server.models.Role;
import shoe.store.server.models.Size;
import shoe.store.server.models.User;
import shoe.store.server.payload.request.RegisterRequest;
import shoe.store.server.repositories.CategoryRepository;
import shoe.store.server.repositories.ColorRepository;
import shoe.store.server.repositories.RoleRepository;
import shoe.store.server.repositories.SizeRepository;
import shoe.store.server.repositories.UserRepository;

import java.util.List;

import javax.annotation.PostConstruct;

@Component
public class InitialSetup {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SizeRepository sizeRepository;
    
    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthController authController;

    @PostConstruct
    public void initRole() throws Exception {

        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN).orElse(null);
        Role sellerRole = roleRepository.findByName(Role.ERole.ROLE_SELLER).orElse(null);
        Role buyerRole = roleRepository.findByName(Role.ERole.ROLE_BUYER).orElse(null);

        if (adminRole == null) {
            adminRole = new Role(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        if (sellerRole == null) {
            sellerRole = new Role(Role.ERole.ROLE_SELLER);
            roleRepository.save(sellerRole);
        }

        if (buyerRole == null) {
            buyerRole = new Role(Role.ERole.ROLE_BUYER);
            roleRepository.save(buyerRole);
        }
    }

    @PostConstruct
    public void initAdminUser() throws Exception {
        User adminUser = userRepository.findByUsername("admin");
        if (adminUser == null) {
            RegisterRequest registerRequest = new RegisterRequest("admin", "admin", "admin", "admin@shoe.store.com", "Admin", "", "0", null, null);
            authController.registerUser(registerRequest);
        }
    }

    @PostConstruct
    public void initSize() throws Exception {
        List<Size> sizes = sizeRepository.findAll();
        if (sizes.isEmpty()) {
            for (int i = 16; i <= 47; i ++) {
                Size size = new Size(String.valueOf(i), i);
                sizeRepository.save(size);
            }
        }
    }

    @PostConstruct
    public void initColor() throws Exception {
        List<Color> colors = colorRepository.findAll();
        if (colors.isEmpty()) {
            Color newColor1 = new Color("DE", "??en", "#000000");colorRepository.save(newColor1);
            Color newColor13 = new Color("TR", "Tr???ng", "#FFFFFF");colorRepository.save(newColor13);
            Color newColor2 = new Color("XL", "Xanh l???c", "#00FF00");colorRepository.save(newColor2);
            Color newColor12 = new Color("XB", "Xanh bi???n", "#0000FF");colorRepository.save(newColor12);
            Color newColor5 = new Color("DO", "?????", "#FF0000");colorRepository.save(newColor5);
            Color newColor3 = new Color("BE", "Be", "#F5F5DC");colorRepository.save(newColor3);
            Color newColor4 = new Color("NA", "N??u", "#A0522D");colorRepository.save(newColor4);
            Color newColor7 = new Color("TI", "T??m", "#EE82EE");colorRepository.save(newColor7);
            Color newColor8 = new Color("VA", "V??ng", "#FFD700");colorRepository.save(newColor8);
            Color newColor9 = new Color("XA", "X??m", "#808080");colorRepository.save(newColor9);
            Color newColor10 = new Color("CA", "Cam", "#FFA500");colorRepository.save(newColor10);
            Color newColor11 = new Color("HO", "H???ng", "#FFC0CB");colorRepository.save(newColor11);
            Color newColor14 = new Color("XX", "Kh??c", "linear-gradient(45deg, orange , yellow, green, cyan, blue, violet)");colorRepository.save(newColor14);
        }
    }

    @PostConstruct
    public void initCategory() throws Exception {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            Category newCate1 = new Category();
            newCate1.setCategoryName("Gi??y nam");
            newCate1.setSizeRange(new Category.SizeRange(39, 45));
            Category newCate2 = new Category();
            newCate2.setCategoryName("Gi??y n???");
            newCate2.setSizeRange(new Category.SizeRange(35, 41));
            Category newCate3 = new Category();
            newCate3.setCategoryName("Gi??y tr??? em");
            newCate3.setSizeRange(new Category.SizeRange(28, 34));

            categoryRepository.save(newCate1);
            categoryRepository.save(newCate2);
            categoryRepository.save(newCate3);
        }
    }
}
