package shoe.store.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shoe.store.server.models.Role;
import shoe.store.server.repositories.RoleRepository;

import javax.annotation.PostConstruct;

@Component
public class InitialSetup {

        @Autowired
        private RoleRepository roleRepository;

    @PostConstruct
    public void initIt() throws Exception {

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
}
