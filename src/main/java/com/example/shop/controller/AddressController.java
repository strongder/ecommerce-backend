package com.example.shop.controller;

import com.example.shop.dtos.request.AddressRequest;
import com.example.shop.dtos.response.AddressResponse;
import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@CrossOrigin(origins = "http://localhost:3000")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("")
    public ApiResponse<AddressResponse> create( @RequestBody AddressRequest request) {
        AddressResponse result = addressService.create(request);
        return ApiResponse.<AddressResponse>builder()
                .message("Create address success")
                .result(result)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable("id") Long id, @RequestBody AddressRequest request) {
        AddressResponse result = addressService.update(id, request);
        return ApiResponse.<AddressResponse>builder()
                .message("Update address success")
                .result(result)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<AddressResponse>> getAddressByUser(@PathVariable("userId") Long userId) {

        List<AddressResponse> addressDTOS = addressService.getAddressByUser(userId);
        return ApiResponse.<List<AddressResponse>>builder()
                .message("Get address by user success")
                .result(addressDTOS)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getById(@PathVariable("id") Long id) {

        AddressResponse addressDTO = addressService.getById(id);
        return ApiResponse.<AddressResponse>builder()
                .message("Get address by id success")
                .result(addressDTO)
                .build();
    }

    //xóa địa chỉ theo Id(xóa mềm)
    @DeleteMapping("/delete/{id}")
    public ApiResponse<AddressResponse> deleteById(@PathVariable Long id) {
        AddressResponse addressDTO = addressService.delete(id);
        return ApiResponse.<AddressResponse>builder()
                .message("Delete address success")
                .result(addressDTO)
                .build();
    }

}
