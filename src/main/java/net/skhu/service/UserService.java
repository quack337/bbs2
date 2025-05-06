package net.skhu.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import net.skhu.entity.User;
import net.skhu.model.OptionTag;
import net.skhu.model.Pagination;
import net.skhu.model.UserEdit;
import net.skhu.model.UserOAuth2SignUp;
import net.skhu.model.UserSignUp;
import net.skhu.repository.UserRepository;

@Service
public class UserService {

    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    ModelMapper modelMapper = new ModelMapper();

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void insert(UserSignUp userSignUp, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors())
            throw new Exception("사용자를 등록할 수 없습니다.");
        if (userSignUp.getPasswd1().equals(userSignUp.getPasswd2()) == false) {
            bindingResult.rejectValue("passwd2", null, "비밀번호가 일치하지 않습니다.");
            throw new Exception("사용자를 등록할 수 없습니다.");
        }
        Optional<User> user = userRepository.findByLoginName(userSignUp.getLoginName());
        if (user.isPresent()) {
            bindingResult.rejectValue("loginName", null, "사용자 아이디가 중복됩니다.");
            throw new Exception("사용자를 등록할 수 없습니다.");
        };
        User newUser = modelMapper.map(userSignUp, User.class);
        newUser.setPassword(passwordEncoder.encode(userSignUp.getPasswd1()));
        newUser.setEnabled(true);
        userRepository.save(newUser);
    }

    public User insert(OAuth2AuthenticationToken auth, UserOAuth2SignUp userOAuth2SignUp,
            BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors())
            throw new Exception("사용자를 등록할 수 없습니다.");
        Optional<User> user = userRepository.findByLoginName(userOAuth2SignUp.getLoginName());
        if (user.isPresent()) {
            bindingResult.rejectValue("loginName", null, "사용자 아이디가 중복됩니다.");
            throw new Exception("사용자를 등록할 수 없습니다.");
        };
        User newUser = modelMapper.map(userOAuth2SignUp, User.class);
        newUser.setEnabled(true);
        newUser.setOpenId(getOpenId(auth));
        userRepository.save(newUser);
        return newUser;
    }

    Map<String, Object> getAttributes(OAuth2AuthenticationToken auth) {
        String provider = auth.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = auth.getPrincipal().getAttributes();
        if (provider.equals("naver"))
            attributes = (Map<String, Object>)attributes.get("response");
        return attributes;
    }

    public String getOpenId(OAuth2AuthenticationToken auth) {
        String provider = auth.getAuthorizedClientRegistrationId();
        String id = "";
        switch (provider) {
            case "github": id = getAttributes(auth).get("id").toString(); break;
            case "google": id = getAttributes(auth).get("sub").toString(); break;
            case "naver": id = getAttributes(auth).get("id").toString(); break;
        }
        return provider + ":" + id;
    }

    public Optional<User> findByOpenId(OAuth2AuthenticationToken auth) {
        String openId = getOpenId(auth);
        return userRepository.findByOpenId(openId);
    }

    public UserOAuth2SignUp createUserOAuth2SignUp(OAuth2AuthenticationToken auth) {
        Map<String, Object> attributes = getAttributes(auth);
        var userOAuthSignUp = new UserOAuth2SignUp();
        userOAuthSignUp.setName(attributes.get("name").toString());
        userOAuthSignUp.setEmail(attributes.get("email").toString());
        return userOAuthSignUp;
    }

    static OptionTag[] orderOptions = new OptionTag[] {
        new OptionTag(0, "정렬 순서"),
        new OptionTag(1, "가입순서 내림차순"),
        new OptionTag(2, "사용자 아이디 오름차순"),
        new OptionTag(3, "이름 오름차순"),
    };

    static Sort[] sorts = new Sort[] {
        Sort.by(Sort.Direction.ASC, "id"),
        Sort.by(Sort.Direction.DESC, "id"),
        Sort.by(Sort.Direction.ASC, "loginName"),
        Sort.by(Sort.Direction.ASC, "name")
    };

    public OptionTag[] getOrderOptions() {
        return orderOptions;
    }

    static OptionTag[] searchOptions = new OptionTag[] {
        new OptionTag(0, "조회 조건"),
        new OptionTag(1, "사용자 아이디"),
        new OptionTag(2, "이름"),
        new OptionTag(3, "사용자 유형")
    };

    public OptionTag[] getSearchOptions() {
        return searchOptions;
    }

    public List<User> findAll(Pagination pagination) {
        int pg = pagination.getPg() - 1, sz = pagination.getSz(),
            si = pagination.getSi(), od = pagination.getOd();
        String st = pagination.getSt();
        Page<User> page = null;
        Sort sort = sorts[od];
        Pageable pageable = PageRequest.of(pg, sz, sort);
        if (si == 1)
            page = userRepository.findByLoginNameStartsWith(st, pageable);
        else if (si == 2)
            page = userRepository.findByNameStartsWith(st, pageable);
        else if (si == 3)
            page = userRepository.findByUserType(st, pageable);
        else
            page = userRepository.findAll(pageable);
        pagination.setRecordCount((int)page.getTotalElements());
        return page.getContent();
    }

    public UserEdit findById(int id) {
        var userEntity = userRepository.findById(id).get();
        var userEdit = modelMapper.map(userEntity, UserEdit.class);
        return userEdit;
    }

    public void update(@Valid UserEdit userEdit,
            BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors())
            throw new Exception("입력 데이터 오류");
        Optional<User> user2 = userRepository.findByLoginName(userEdit.getLoginName());
        if (user2.isPresent() && user2.get().getId() != userEdit.getId()) {
            bindingResult.rejectValue("loginName", null, "사용자 아이디가 중복됩니다");
            throw new Exception("입력 데이터 오류");
        }
        User user = modelMapper.map(userEdit, User.class);
        User user0 = userRepository.findById(userEdit.getId()).get();
        user.setPassword(user0.getPassword());
        userRepository.save(user);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
