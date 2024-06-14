package com.example.shop.convert;

import com.example.shop.dtos.request.UserRequest;
import com.example.shop.dtos.response.UserResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Role;
import com.example.shop.model.User;
import com.example.shop.repository.RoleRepository;
import com.example.shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserConvert {

    ModelMapper modelMapper;
    UserRepository userRepository;
    RoleRepository roleRepository;

    public User convertToEntity(UserRequest userRequest){
        Set<Role> roles = new HashSet<>();
            roles = userRequest.getRoles().stream().map(role -> roleRepository.findByName(role)
                    .orElseThrow(() -> new AppException(ErrorResponse.ROLE_NOT_EXISTED))).collect(Collectors.toSet());

        User user = modelMapper.map(userRequest, User.class);
        user.setRoles(roles);
        return  user;
    }

    public UserResponse convertToDTO(User user)
    {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        return  userResponse;
    }

}
