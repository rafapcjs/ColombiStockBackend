package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.factory.SuppliersFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.repostories.SuppliersRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.SupplierPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.dto.SupplierDto;
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
    private final SuppliersFactory suppliersFactory;

    @Override
    @Transactional
    public void saveSupplier(SupplierPayload supplierPayload) {


            ModelMapper modelMapper = new ModelMapper();

            Suppliers suppliers = modelMapper.map(supplierPayload, Suppliers.class);
            suppliersRepository.save(suppliers);

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
            suppliersRepository.save(suppliers);
        } else {

            throw new SuppliersNotFoundException("Suppliers with" + dni + "not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDto> findSuppliersByEmail(String email) {

        return suppliersRepository.findSuppliersByEmailIgnoreCase(email)
                .map(suppliersFactory::createSupplierDTO);
    }

    @Override
    @Transactional
    public Optional<SupplierDto> findSuppliersByPhone(String phone) {
        return suppliersRepository.findSuppliersByPhoneIgnoreCase(phone)
                .map(suppliersFactory::createSupplierDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDto> findAllSuppliers(Pageable pageable) {

        Page<Suppliers> suppliersPage = suppliersRepository.findAll(pageable);

        List<SupplierDto> supplierDtoList = suppliersPage.stream()
                .map(suppliersFactory::createSupplierDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(supplierDtoList, pageable, suppliersPage.getTotalElements());
    }

    @Override
    @Transactional
    public String delete(String dni) {
        Optional<Suppliers> existingSuppliers = suppliersRepository.findSuppliersByDniIgnoreCase(dni);

        if (existingSuppliers.isPresent()) {
Suppliers suppliers = existingSuppliers.get();

if (!suppliers.getProductsList().isEmpty()){
    throw new ReferentialIntegrityException("No se puede eliminar el provedor con cedula " + dni + " porque tiene productos asociados.");

}

            suppliersRepository.deleteSuppliersByDni(dni);
            return "supplier deleted with CC :" + dni;

        } else {
            throw new ResourceNotFoundException("Supplier not found with dni :" + dni);
        }
    }
}
