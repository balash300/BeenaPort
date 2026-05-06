package az.beenaport.billingservice.mapper;

import az.beenaport.billingservice.dto.response.BillingScheduleResponse;
import az.beenaport.billingservice.entity.BillingSchedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillingScheduleMapper {

    BillingScheduleResponse toResponse(BillingSchedule schedule);
}