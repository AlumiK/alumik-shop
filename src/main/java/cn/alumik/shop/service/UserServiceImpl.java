package cn.alumik.shop.service;

import cn.alumik.shop.dao.GenderRepository;
import cn.alumik.shop.dao.RoleRepository;
import cn.alumik.shop.dao.UserRepository;
import cn.alumik.shop.entity.User;
import cn.alumik.shop.entity.UserProfile;
import cn.alumik.shop.form.ChangePasswordForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private GenderRepository genderRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, GenderRepository genderRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.genderRepository = genderRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void save(User user, Boolean isNewUser) {
        if (isNewUser) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            UserProfile userProfile = new UserProfile();
            userProfile.setJoinDate(new Date(System.currentTimeMillis()));
            genderRepository.findByName("未知").ifPresent(userProfile::setGender);
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
            roleRepository.findByName("ROLE_USER").ifPresent(user::addRole);
        }
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> findAll(String username, Integer enabled, Pageable pageable) {
        if (enabled == -1) {
            return userRepository.findAllByUsernameContaining(username, pageable);
        }
        return userRepository.findAllByUsernameContainingAndEnabled(username, enabled != 0, pageable);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void setPassword(User user, ChangePasswordForm changePasswordForm) {
        user.setPassword(bCryptPasswordEncoder.encode(changePasswordForm.getPassword()));
        userRepository.save(user);
    }

    @Override
    public boolean passwordMatches(User user, String password) {
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }
}
