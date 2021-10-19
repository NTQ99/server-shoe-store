package shoe.store.server.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.User;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_NOT_FOUND.message);
		}

		if (user.getStatus() == User.UserStatus.banned) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_BANNED.message);
		}

		if (user.getStatus() == User.UserStatus.locked) {
			throw new GlobalException(ErrorMessage.StatusCode.USER_LOCKED.message);
		}

		return UserDetailsImpl.build(user);
	}

}
