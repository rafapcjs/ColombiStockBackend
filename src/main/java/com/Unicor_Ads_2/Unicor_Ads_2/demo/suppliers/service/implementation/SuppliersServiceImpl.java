package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.DuplicateSuppliersException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.SuppliersNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.repostories.SuppliersRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.SupplierPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.dto.SupplierDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.service.interfaces.ISuppliersServices;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuppliersServiceImpl implements ISuppliersServices {
    private final SuppliersRepository suppliersRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void saveSupplier(SupplierPayload supplierPayload) {

        try {
            ModelMapper modelMapper = new ModelMapper();

            Suppliers suppliers = modelMapper.map(supplierPayload, Suppliers.class);
            suppliersRepository.save(suppliers);
        } catch (Exception e) {

            throw new UnsupportedOperationException("Error saving suppliers");
        }
    }

    @Override
    public void updateSupplier(SupplierPayload supplierPayload, String dni) {

        Optional<Suppliers> existSuppliers = suppliersRepository.findSuppliersByDniIgnoreCase(dni);
        if (existSuppliers.isPresent()) {
            Suppliers suppliers = existSuppliers.get();
            Optional<Suppliers> duplicateDniSuppliers = suppliersRepository.findSuppliersByDniIgnoreCase(supplierPayload.getDni());

            if (duplicateDniSuppliers.isPresent() && !duplicateDniSuppliers.get().equals(suppliers)) {

                throw new DuplicateSuppliersException("suppliers with dni" + supplierPayload.getDni() + "Already exists");
            }
            Optional<Suppliers> duplicateNameSuppliers = suppliersRepository.findSuppliersByNameIgnoreCase(supplierPayload.getName());
            if (duplicateNameSuppliers.isPresent() && !duplicateNameSuppliers.get().equals(suppliers)) {

                throw new DuplicateSuppliersException("Suppliers with name" + supplierPayload.getName() + "Already exists");
            }
            Optional<Suppliers> duplicatePhoneSuppliers = suppliersRepository.findSuppliersByPhoneIgnoreCase(supplierPayload.getPhone());
            if (duplicatePhoneSuppliers.isPresent() && !duplicatePhoneSuppliers.get().equals(suppliers)) {

                throw new DuplicateSuppliersException("Suppliers with phone" + supplierPayload.getPhone() + "Already exists");
            }
            Optional<Suppliers> duplicateEmailSuppliers = suppliersRepository.findSuppliersByEmailIgnoreCase(supplierPayload.getEmail());
            if (duplicateEmailSuppliers.isPresent() && !duplicateEmailSuppliers.get().equals(suppliers)) {

                throw new DuplicateSuppliersException("Suppliers with email" + supplierPayload.getEmail() + "Already exists");
            }

            suppliers.setEmail(supplierPayload.getEmail());
            suppliers.setName(supplierPayload.getName());
            suppliers.setPhone(supplierPayload.getPhone());
            suppliers.setLastName(supplierPayload.getLastName());
        } else {

            throw new SuppliersNotFoundException("Suppliers with" + dni + "not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDto> findSuppliersByEmail(String email) {

        return suppliersRepository.findSuppliersByEmailIgnoreCase(email)
                .map(suppliers -> SupplierDto.builder()
                        .name(suppliers.getName())
                        .lastName(suppliers.getLastName())
                        .dni(suppliers.getDni())
                        .email(suppliers.getEmail())
                        .phone(suppliers.getPhone())
                        .build());

    }

    @Override
    @Transactional
    public Optional<SupplierDto> findSuppliersByPhone(String phone) {
        return suppliersRepository.findSuppliersByPhoneIgnoreCase(phone)
                .map(suppliers -> SupplierDto.builder()
                        .name(suppliers.getName())
                        .lastName(suppliers.getLastName())
                        .dni(suppliers.getDni())
                        .email(suppliers.getEmail())
                        .phone(suppliers.getPhone())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDto> findAllSuppliers(Pageable pageable) {

        Page<Suppliers> suppliersPage = suppliersRepository.findAll(pageable);

        List<SupplierDto> supplierDtoList = suppliersPage.stream()
                .map(suppliers -> SupplierDto.builder()
                        .name(suppliers.getName())
                        .lastName(suppliers.getLastName())
                        .dni(suppliers.getDni())
                        .email(suppliers.getEmail())
                        .phone(suppliers.getPhone())
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(supplierDtoList, pageable, suppliersPage.getTotalElements());
    }

    @Override
    @Transactional
    public String delete(String dni) {
        Optional<Suppliers> existingSuppliers = suppliersRepository.findSuppliersByDniIgnoreCase(dni);

        if (existingSuppliers.isPresent()) {

            suppliersRepository.deleteSuppliersByDni(dni);
            return "supplier deleted with CC :" + dni;

        } else {
            throw new UnsupportedOperationException("Supplier not found with dni :" + dni);
        }
    }
}
