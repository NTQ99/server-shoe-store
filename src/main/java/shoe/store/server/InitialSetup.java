package shoe.store.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shoe.store.server.models.Category;
import shoe.store.server.models.Color;
import shoe.store.server.models.Role;
import shoe.store.server.models.Size;
import shoe.store.server.repositories.CategoryRepository;
import shoe.store.server.repositories.ColorRepository;
import shoe.store.server.repositories.RoleRepository;
import shoe.store.server.repositories.SizeRepository;

import java.util.List;

import javax.annotation.PostConstruct;

@Component
public class InitialSetup {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SizeRepository sizeRepository;
    
    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
            Color newColor1 = new Color("DE", "Đen");colorRepository.save(newColor1);
            Color newColor2 = new Color("XA", "Xanh");colorRepository.save(newColor2);
            Color newColor3 = new Color("BE", "Be");colorRepository.save(newColor3);
            Color newColor4 = new Color("NA", "Nâu");colorRepository.save(newColor4);
            Color newColor5 = new Color("DO", "Đỏ");colorRepository.save(newColor5);
            Color newColor6 = new Color("KE", "Kem");colorRepository.save(newColor6);
            Color newColor7 = new Color("TI", "Tím");colorRepository.save(newColor7);
            Color newColor8 = new Color("VA", "Vàng");colorRepository.save(newColor8);
            Color newColor9 = new Color("XS", "Xám");colorRepository.save(newColor9);
            Color newColor10 = new Color("CA", "Cam");colorRepository.save(newColor10);
            Color newColor11 = new Color("HO", "Hồng");colorRepository.save(newColor11);
            Color newColor13 = new Color("TR", "Trắng");colorRepository.save(newColor13);
        }
    }

    @PostConstruct
    public void initCategory() throws Exception {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            Category newCate1 = new Category();
            newCate1.setCategoryName("Nam");
            Category newCate2 = new Category();
            newCate2.setCategoryName("Nữ");
            Category newCate3 = new Category();
            newCate3.setCategoryName("Trẻ em");

            categoryRepository.save(newCate1);
            categoryRepository.save(newCate2);
            categoryRepository.save(newCate3);
        }
    }
}
