package com.example.shop.service;

import com.example.shop.convert.AddressConvert;
import com.example.shop.dtos.request.AddressRequest;
import com.example.shop.dtos.response.AddressResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Address;
import com.example.shop.model.User;
import com.example.shop.repository.AddressRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.utils.AesUtil;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final AddressConvert addressConvert;

	AesUtil aesUtil;
	@Value("${base64.key}")
	private String base64Key;


	@PostConstruct
	private void init() {
		this.aesUtil = new AesUtil(base64Key);
	}

	@Transactional(readOnly = true)
    public AddressResponse getById(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        if (address.isPresent()) {
            return addressConvert.convertToDTO(decryptAddress(address.get()));
        }
        throw new AppException(ErrorResponse.ADDRESS_NOT_EXISTED);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Set<Address> addresses = addressRepository.findByUserAndIsDeleteFalse(user.get());
            return addresses.stream().map(address -> addressConvert.convertToDTO(decryptAddress(address))).collect(Collectors.toList());
        } else {
            throw new AppException( ErrorResponse.USER_NOT_EXISTED);
        }
    }

	@Transactional
	public AddressResponse delete(Long id) {
		Optional<Address> existedAddress = addressRepository.findById(id);
		if (existedAddress.isPresent()) {
			existedAddress.get().setDelete(true);
			Address address = addressRepository.save(existedAddress.get());
			return addressConvert.convertToDTO(address);
		}
		throw new AppException(ErrorResponse.ADDRESS_NOT_EXISTED);
	}

    @Transactional
	public AddressResponse update(Long id, AddressRequest request) {
		Address address = decryptAddress(addressRepository.findById(id)
				.orElseThrow(()-> new AppException(ErrorResponse.ADDRESS_NOT_EXISTED)));
        address = addressConvert.convertToEntity(request);
		address.setId(id);
        addressRepository.save(encryptAddress(address));
			return addressConvert.convertToDTO(address);
    }

    @Transactional
	public AddressResponse create(AddressRequest request) {
		Address address = encryptAddress(addressConvert.convertToEntity(request));
		address.setDefaultAddress(false);
		addressRepository.save(address);
		return addressConvert.convertToDTO(decryptAddress(address));
	}


	public AddressResponse setDefaultAddress(Long id) {
		var email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = Optional.ofNullable(userRepository.findByEmail(aesUtil.encrypt(email)).orElseThrow(() -> new AppException(ErrorResponse.USER_NOT_EXISTED)));
		Set<Address> addresses = addressRepository.findByUserAndIsDeleteFalse(user.get());
		addresses.forEach(address -> {
			if (address.getId().equals(id)) {
				address.setDefaultAddress(true);
			} else {
				address.setDefaultAddress(false);
			}
		});
		addressRepository.saveAll(addresses.stream().map(this::encryptAddress).collect(Collectors.toList()));
		return addressConvert.convertToDTO(decryptAddress(addressRepository.findById(id).get()));
	}

    public AddressResponse getDefaultAddress() {
		var email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByEmail(aesUtil.encrypt(email));
		Optional<Address> address = addressRepository.findByUserAndDefaultAddressTrueAndIsDeleteFalse(user.get());
		if (address.isPresent()) {
			return addressConvert.convertToDTO(decryptAddress(address.get()));
		}
		throw new AppException(ErrorResponse.ADDRESS_NOT_EXISTED);
    }

	public Address encryptAddress(Address address){
		Address encrypt = new Address();
		encrypt.setAddressDetail(aesUtil.encrypt(address.getAddressDetail()));
		encrypt.setCity(aesUtil.encrypt(address.getCity()));
		encrypt.setDistrict(aesUtil.encrypt(address.getDistrict()));
		encrypt.setPhone(aesUtil.encrypt(address.getPhone()));
		encrypt.setRecipientName(aesUtil.encrypt(address.getRecipientName()));
		encrypt.setWard(aesUtil.encrypt(address.getWard()));
		encrypt.setId(address.getId());
		encrypt.setDefaultAddress(address.isDefaultAddress());
		encrypt.setDelete(address.isDelete());
		return encrypt;
	}

	public Address decryptAddress(Address address){
		Address decrypt = new Address();
		decrypt.setAddressDetail(aesUtil.decrypt(address.getAddressDetail()));
		decrypt.setCity(aesUtil.decrypt(address.getCity()));
		decrypt.setDistrict(aesUtil.decrypt(address.getDistrict()));
		decrypt.setPhone(aesUtil.decrypt(address.getPhone()));
		decrypt.setRecipientName(aesUtil.decrypt(address.getRecipientName()));
		decrypt.setWard(aesUtil.decrypt(address.getWard()));
		decrypt.setId(address.getId());
		decrypt.setDefaultAddress(address.isDefaultAddress());
		decrypt.setDelete(address.isDelete());
		return decrypt;
	}
}
