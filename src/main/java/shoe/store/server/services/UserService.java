package shoe.store.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import shoe.store.server.models.Role;
import shoe.store.server.models.User;
import shoe.store.server.repositories.RoleRepository;
import shoe.store.server.repositories.UserRepository;

@Service("userService")
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Page<User> findUserByUsername(String username, Pageable pagging) {
        return userRepository.findByUsernameContaining(username, pagging);
    }

    public Page<User> getAllUsers(Pageable paging) {
        return userRepository.findAll(paging);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updatePassword(String id, String newPw) {
        User newUserData = userRepository.findById(id).orElse(null);
        newUserData.setPassword(newPw);
        return userRepository.save(newUserData);
    }

    public User updateUser(String id, User newData) {
        User newUserData = userRepository.findById(id).orElse(null);
        newUserData.updateData(newData);
        return userRepository.save(newUserData);
    }

    public User updateUserStatus(String id, String newStatus) {
        User userData = userRepository.findById(id).orElse(null);
        if (userData == null) {
            return null;
        } else {
            userData.setStatus(User.UserStatus.valueOf(newStatus));
        }
        return userRepository.save(userData);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public Boolean checkUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean checkPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public Role getRoleByName(Role.ERole roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

}
