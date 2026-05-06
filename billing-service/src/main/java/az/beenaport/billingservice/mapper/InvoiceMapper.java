package az.beenaport.billingservice.mapper;

import az.beenaport.billingservice.dto.request.InvoiceRequest;
import az.beenaport.billingservice.dto.response.InvoiceResponse;
import az.beenaport.billingservice.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Invoice toEntity(InvoiceRequest request);

    InvoiceResponse toResponse(Invoice invoice);
}