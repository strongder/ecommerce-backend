package com.example.shop.convert;


import com.example.shop.dtos.request.AddressRequest;
import com.example.shop.dtos.response.AddressResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Address;
import com.example.shop.model.User;
import com.example.shop.repository.AddressRepository;
import com.example.shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AddressConvert {

    ModelMapper modelMapper;
    AddressRepository addressRepository;
    UserRepository userRepository;

    public Address convertToEntity(AddressRequest request)
    {
        User user = userRepository.findById(request.getUserId()).orElseThrow(()-> new AppException(ErrorResponse.USER_NOT_EXISTED));
        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);
        user.getAddresses().add(address);
        return  address;
    }

    public AddressResponse convertToDTO(Address address)
    {
        AddressResponse response = modelMapper.map(address, AddressResponse.class);
        return response;
    }
}
